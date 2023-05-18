import cv2
import numpy as np
import sys
import pytesseract
import os
import math


pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'


def order_points(pts):
    # initialzie a list of coordinates that will be ordered
    # such that the first entry in the list is the top-left,
    # the second entry is the top-right, the third is the
    # bottom-right, and the fourth is the bottom-left
    rect = np.zeros((4, 2), dtype = "float32")
    # the top-left point will have the smallest sum, whereas
    # the bottom-right point will have the largest sum
    s = pts.sum(axis = 1)
    rect[0] = pts[np.argmin(s)]
    rect[2] = pts[np.argmax(s)]
    # now, compute the difference between the points, the
    # top-right point will have the smallest difference,
    # whereas the bottom-left will have the largest difference
    diff = np.diff(pts, axis = 1)
    rect[1] = pts[np.argmin(diff)]
    rect[3] = pts[np.argmax(diff)]
    # return the ordered coordinates
    return rect

def four_point_transform(image, pts):
    # obtain a consistent order of the points and unpack them
    # individually
    rect = order_points(pts)
    (tl, tr, br, bl) = rect
    # compute the width of the new image, which will be the
    # maximum distance between bottom-right and bottom-left
    # x-coordiates or the top-right and top-left x-coordinates
    widthA = np.sqrt(((br[0] - bl[0]) ** 2) + ((br[1] - bl[1]) ** 2))
    widthB = np.sqrt(((tr[0] - tl[0]) ** 2) + ((tr[1] - tl[1]) ** 2))
    maxWidth = max(int(widthA), int(widthB))
    # compute the height of the new image, which will be the
    # maximum distance between the top-right and bottom-right
    # y-coordinates or the top-left and bottom-left y-coordinates
    heightA = np.sqrt(((tr[0] - br[0]) ** 2) + ((tr[1] - br[1]) ** 2))
    heightB = np.sqrt(((tl[0] - bl[0]) ** 2) + ((tl[1] - bl[1]) ** 2))
    maxHeight = max(int(heightA), int(heightB))
    # now that we have the dimensions of the new image, construct
    # the set of destination points to obtain a "birds eye view",
    # (i.e. top-down view) of the image, again specifying points
    # in the top-left, top-right, bottom-right, and bottom-left
    # order
    dst = np.array([
        [0, 0],
        [(maxWidth - 1)*2, 0],
        [(maxWidth - 1)*2, (maxHeight - 1)*2],
        [0, (maxHeight - 1)*2]], dtype = "float32")
    # compute the perspective transform matrix and then apply it
    M = cv2.getPerspectiveTransform(rect, dst)
    warped = cv2.warpPerspective(image, M, (maxWidth*2, maxHeight*2))
    # return the warped image
    return warped


def get_id(w, h):
    # Read the image code by returning all upper case and digits
    # found in the cropped image
    
    # Read the image
    img = cv2.imread(os.path.join("App", "SRC", "CARDS", "TOOLS", "id.jpg"))

    # Rescale the image
    img = cv2.resize(img, None, fx=3, fy=3, interpolation=cv2.INTER_CUBIC)

    # Adjust contrast
    alpha = 1.6
    mean_luminosity = cv2.mean(img)[0]
    beta = - 50 * mean_luminosity / 255  # Scale beta based on mean luminosity
    img = cv2.convertScaleAbs(img, alpha=alpha, beta=beta)

    # Convert to grayscale
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    # Apply threshold to get image with only black and white
    img = cv2.adaptiveThreshold(cv2.medianBlur(img, 7), 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, 31, 2)

    # Apply dilation and erosion to remove noise
    kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (3, 3))
    img = cv2.dilate(img, kernel, iterations=3)
    img = cv2.erode(img, kernel, iterations=7)
    img = cv2.dilate(img, kernel, iterations=8)
    img = cv2.erode(img, kernel, iterations=9)
    img = cv2.dilate(img, kernel, iterations=4)
    

    # Recognize text with tesseract for python
    pre_result = pytesseract.image_to_string(img, config='--psm 11')
    result = ''
    for char in pre_result:
        if char.isupper() or char.isdigit():
            result += char
    return result

def get_name(w, h):
    
    img = cv2.imread(os.path.join("App", "SRC", "CARDS", "TOOLS", "name.jpg"))

    # Adjust contrast
    alpha = 1.5
    beta = 0
    img = cv2.convertScaleAbs(img, alpha=alpha, beta=beta)

    # Convert to grayscale
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    # Apply threshold to get image with only black and white
    img = cv2.adaptiveThreshold(img, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, 91, 5)
    img = invert_if_needed(img, 50)

    # Apply dilation and erosion to remove noise
    kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (5, 5))

    img = cv2.dilate(img, kernel, iterations=2)
    img = cv2.erode(img, kernel, iterations=2)

    kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (3, 3))
    img = cv2.dilate(img, kernel, iterations=3)
    img = cv2.erode(img, kernel, iterations=2)

    # Recognize text with tesseract for python
    pre_result = pytesseract.image_to_string(img, config='--psm 11')
    result = ""
    for char in pre_result:
        if char.isupper():
            result += char
    return result

def invert_if_needed(img, threshold=60):
    if img.shape[0]*img.shape[1] < 10000000:
        # Count the number of white pixels
        white_pixels = 0
        total_pixels = img.shape[0] * img.shape[1]
        # Loop over the pixels in the thresholded image
        for i in range(img.shape[0]):
            for j in range(img.shape[1]):
                # Get the pixel value at position (i,j)
                pixel_value = img[i,j]
                if pixel_value == 255:
                    white_pixels += 1

        # Calculate the percentage of white pixels
        white_percent = white_pixels / total_pixels * 100

        # Check if the percentage of white pixels is above the threshold
        if white_percent > threshold:
            # Revert the colors
            img = cv2.bitwise_not(img)
        
        return img
    else:
        return img

# Define a function to calculate the angle between three points
def calculate_angle(point1, point2, point3):
    a = math.sqrt((point2[0]-point1[0])**2 + (point2[1]-point1[1])**2)
    b = math.sqrt((point2[0]-point3[0])**2 + (point2[1]-point3[1])**2)
    c = math.sqrt((point3[0]-point1[0])**2 + (point3[1]-point1[1])**2)
    return math.degrees(math.acos((a**2 + b**2 - c**2) / (2*a*b)))

# Define a function to find the three points with the angle closest to 90 degrees
def find_best_points(points):
    best_points = []
    best_angle_diff = None
    for i, point1 in enumerate(points):
        point2 = points[(i+1)%4]
        point3 = points[(i+2)%4]
        angle = calculate_angle(point1, point2, point3)
        angle_diff = abs(90 - angle)
        if best_angle_diff is None or angle_diff < best_angle_diff:
            best_angle_diff = angle_diff
            best_points = [point1, point2, point3]
    return best_points

# Define a function to rearrange the three points into a parallelogram
def make_parallelogram(points):
    p1 = points[0]
    p2 = points[1]
    p3 = points[2]
    
    diffx = abs(p2[0] - p1[0])
    diffy = abs(p2[1] - p1[1])
    
    p4 = (p3[0] - diffx, p3[1] - diffy)
    
    return [p1, p2, p3, p4]

def center(window):
    """
    Centers a Tkinter window on the screen.
    """
    window.update_idletasks()
    width = window.winfo_width()
    height = window.winfo_height()
    
    frm_width = window.winfo_rootx() - window.winfo_x()
    frm_height = window.winfo_rooty() - window.winfo_y()
    
    win_width = width + 2 * frm_width
    win_height = height + frm_height + frm_width
    
    x = window.winfo_screenwidth() // 2 - win_width // 2
    y = window.winfo_screenheight() // 2 - win_height // 2
    
    window.geometry(f'{width}x{height}+{x}+{y}')
    window.deiconify()