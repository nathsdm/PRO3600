import cv2
import numpy as np
import sys
import pytesseract
import os

class Analyser():
	def __init__(self, image):
		pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'
		self.image = image
		self.result = None
	
	def analyse(self):
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

			# Rescale the image
			img = cv2.resize(img, None, fx=3, fy=3, interpolation=cv2.INTER_CUBIC)

			# Adjust contrast
			alpha = 1.5
			mean_luminosity = cv2.mean(img)[0]
			beta = - 100 * mean_luminosity / 255  # Scale beta based on mean luminosity
			img = cv2.convertScaleAbs(img, alpha=alpha, beta=beta)

			# Convert to grayscale
			img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

			# Apply dilation and erosion to remove noise
			kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (1, 1))
			img = cv2.dilate(img, kernel, iterations=10)
			img = cv2.erode(img, kernel, iterations=10)

			# Apply threshold to get image with only black and white
			img = cv2.adaptiveThreshold(cv2.medianBlur(img, 7), 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, 31, 2)

			# Recognize text with tesseract for python
			result = pytesseract.image_to_string(img, config='--psm 11')
			return result

		image = cv2.imread(self.image)
		scale_percent = 40 # percent of original size
		width = int(image.shape[1] * scale_percent / 100)
		height = int(image.shape[0] * scale_percent / 100)
		dim = (width, height)
		image = cv2.resize(image,  dim, interpolation = cv2.INTER_AREA)
		orig = image.copy()

		# Step 1, find edges
		# adjust contrast
		alpha = 1.7
		mean_luminosity = cv2.mean(image)[0]
		beta = - 200 * mean_luminosity / 255  # Scale beta based on mean luminosity
		image = cv2.convertScaleAbs(image, alpha=alpha, beta=beta)

		# Perform Gaussian blur on the input image
		blur = cv2.GaussianBlur(image, (3, 3), 0)

		# Convert the input image to grayscale
		gray = cv2.cvtColor(blur, cv2.COLOR_BGR2GRAY)

		# Apply Canny edge detection with limited non-linear edge detection
		edged = cv2.Canny(blur, 0, 350, L2gradient=True)

		# Create a structuring element for morphological operations
		kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (3, 3))

		# Apply morphological closing to the binary image to close small gaps in the edges
		closed_image = cv2.morphologyEx(edged, cv2.MORPH_CLOSE, kernel)

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
			approx = cv2.approxPolyDP(c, 0.02 * peri, True)
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
			tup = tuple(k[0])
			pts.append(tup)

		warped = four_point_transform(orig, pts = np.array(eval("{}".format(pts)), dtype = "float32"))

		cv2.imwrite(os.path.join("App", "DATA", "CARDS", "IMAGES", "test.jpg"), warped)

		w,h = warped.shape[:2]
		cropped = warped[23*w//32:3*w//4, 2*h//3:49*h//50]
  
		cv2.imwrite("ocr.jpg", cropped)
  
		result = get_string("ocr.jpg")
		print(result)
		self.result = result