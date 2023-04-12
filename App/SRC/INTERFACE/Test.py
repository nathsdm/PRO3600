import cv2
import numpy as np
import pytesseract

# Chargement de l'image découpée
img = cv2.imread('App/SRC/INTERFACE/step4.jpg')

# Convertir l'image en niveaux de gris
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

# Appliquer un filtre de seuillage adaptatif pour améliorer le contraste
thresh = cv2.adaptiveThreshold(gray, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY_INV, 11, 2)

# Appliquer une érosion pour supprimer les petites taches ou bruits dans l'image
kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (2, 2))
erosion = cv2.erode(thresh, kernel, iterations=1)

# Appliquer une dilatation pour améliorer la connectivité des caractères
kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (4, 4))
dilation = cv2.dilate(erosion, kernel, iterations=1)

# Appliquer une seconde érosion pour affiner les contours des caractères
kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (3, 3))
erosion2 = cv2.erode(dilation, kernel, iterations=1)

# Utiliser un logiciel OCR pour lire le texte de l'image
custom_config = r'--oem 3 --psm 3 -c tessedit_char_whitelist=ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789- -c tessedit_font_properties=Matrix-Bold'
texte = pytesseract.image_to_string(erosion2, config=custom_config)

print(texte)