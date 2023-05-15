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


def get_string(img_path):
	# Read image using opencv
	img = cv2.imread(img_path)

	# Adjust contrast
	alpha = 1.5
	beta = 0
	img = cv2.convertScaleAbs(img, alpha=alpha, beta=beta)

	# Convert to grayscale
	img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

	# Apply threshold to get image with only black and white
	img = cv2.adaptiveThreshold(img, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, 91, 5)
	# Determine the threshold percentage
	threshold = 50

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
  
	cv2.imshow("Image", img)
	cv2.waitKey(0)
 
 	# Apply dilation and erosion to remove noise
	kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (5, 5))
	
	img = cv2.dilate(img, kernel, iterations=2)
	img = cv2.erode(img, kernel, iterations=2)
	
	kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (3, 3))
	img = cv2.dilate(img, kernel, iterations=3)
	img = cv2.erode(img, kernel, iterations=2)
 
	cv2.imshow("Image", img)
	cv2.waitKey(0)
	# Recognize text with tesseract for python
	pre_result = pytesseract.image_to_string(img, config='--psm 11')
	result = ""
	for char in pre_result:
		if char.isupper():
			result += char
	return result


image = cv2.imread(os.path.join("App", "SRC", "CARDS", "tocrop2.jpg"))
orig = image.copy()
scale_percent = 50 # percent of original size
width = int(image.shape[1] * scale_percent / 100)
height = int(image.shape[0] * scale_percent / 100)
dim = (width, height)
image = cv2.resize(image,  dim, interpolation = cv2.INTER_AREA)


# Step 1, find edges
# adjust contrast
alpha = 1.6
mean_luminosity = cv2.mean(image)[0]
beta = - 160 * mean_luminosity / 255  # Scale beta based on mean luminosity
image = cv2.convertScaleAbs(image, alpha=alpha, beta=beta)

# Perform Gaussian blur on the input image
blur = cv2.GaussianBlur(image, (3, 3), 0)

# Convert the input image to grayscale
gray = cv2.cvtColor(blur, cv2.COLOR_BGR2GRAY)

# Apply Canny edge detection with limited non-linear edge detection
edged = cv2.Canny(blur, 0, 280, L2gradient=True)

# Create a structuring element for morphological operations
kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (5, 5))

# Apply morphological closing to the binary image to close small gaps in the edges
closed_image = cv2.morphologyEx(edged, cv2.MORPH_CLOSE, kernel)
# Determine the threshold percentage
threshold = 60

# Count the number of white pixels
white_pixels = 0
total_pixels = closed_image.shape[0] * closed_image.shape[1]

if closed_image.shape[0]*closed_image.shape[1] < 10000000:
	# Loop over the pixels in the thresholded image
	for i in range(closed_image.shape[0]):
		for j in range(closed_image.shape[1]):
			# Get the pixel value at position (i,j)
			pixel_value = closed_image[i,j]
			if pixel_value == 255:
				white_pixels += 1

# Calculate the percentage of white pixels
white_percent = white_pixels / total_pixels * 100

# Check if the percentage of white pixels is above the threshold
if white_percent > threshold:
	# Revert the colors
	closed_image = cv2.bitwise_not(closed_image)

cv2.imshow("Image", closed_image)
cv2.waitKey(0)

# Step 2, find countours and sort in order of the area. 
# We assume the card is the focus of the picture so it should have the largest area
cnts = cv2.findContours(closed_image.copy(), cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
cnts = cnts[0] if len(cnts) == 2 else cnts[1]
cnts = sorted(cnts, key = cv2.contourArea, reverse = True)[:5]

# loop over the contours
screenCnt = []
for c in cnts:
	# approximate the contour
	peri = cv2.arcLength(c, True)
	approx = cv2.approxPolyDP(c, 0.1 * peri, True)
	# if our approximated contour has four points, then we
	# can assume that we have found our card
	if len(approx) == 4:
		screenCnt = approx

#Step 3
# apply the four point transform to obtain a top-down
pts=[]
if len(screenCnt) == 0:
	print("No card found")
	exit()
	
for k in screenCnt:
	tup = tuple(k[0]*100//scale_percent)
	pts.append(tup)





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
        for j, point2 in enumerate(points[i+1:], start=i+1):
            for k, point3 in enumerate(points[j+1:], start=j+1):
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
    
    diffx = p2[0] - p1[0]
    diffy = p2[1] - p1[1]
    
    p4 = (p3[0] - diffx, p3[1] - diffy)
    
    return [p1, p2, p3, p4]

# Test the functions with the given points
best_points = find_best_points(pts)
parallelogram_points = make_parallelogram(best_points)









warped = four_point_transform(orig, pts = np.array(eval("{}".format(make_parallelogram(find_best_points(pts)))), dtype = "float32"))
w,h = warped.shape[:2]

if h > w:
	warped = cv2.rotate(warped, cv2.ROTATE_90_CLOCKWISE)
	w,h = warped.shape[:2]

cv2.imwrite(os.path.join("App", "DATA", "CARDS", "IMAGES", "test.jpg"), warped)


cropped = warped[45*w//64:49*w//64, 33*h//48:49*h//50]
cropped = warped[0:w//8, 0:h]
cv2.imwrite("ocr.jpg", cropped)

result = get_string("ocr.jpg")
print(result)