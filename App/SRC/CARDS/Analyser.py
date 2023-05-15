import cv2
import numpy as np
import sys
import pytesseract
import os
import math
from SRC.CARDS.TOOLS.Tools import *
import tkinter as tk

class Analyser():
	def __init__(self, image):
		
		self.image = image
		self.result = None
	
	def analyse(self):


		image = cv2.imread(self.image)
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

		closed_image = invert_if_needed(closed_image)
  
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
			tk.messagebox.showerror("Error", "No card found")
			
		for k in screenCnt:
			tup = tuple(k[0]*100//scale_percent)
			pts.append(tup)

		best_points = find_best_points(pts)
		parallelogram_points = make_parallelogram(best_points)
  
		warped = four_point_transform(orig, pts = np.array(eval("{}".format(make_parallelogram(find_best_points(pts)))), dtype = "float32"))
		w,h = warped.shape[:2]
		if h > w:
			warped = cv2.rotate(warped, cv2.ROTATE_90_CLOCKWISE)
			w,h = warped.shape[:2]
   
		cv2.imwrite(os.path.join("App", "DATA", "CARDS", "IMAGES", "test.jpg"), warped)
		img = warped[0:w//8, 0:h]
		cv2.imwrite(os.path.join("App", "SRC", "CARDS", "TOOLS", "name.jpg"), img)
		img = warped[45*w//64:49*w//64, 2*h//3:49*h//50]
		cv2.imwrite(os.path.join("App", "SRC", "CARDS", "TOOLS", "id.jpg"), img)
		self.result = (get_id(w, h), get_name(w, h))