import unittest
import cv2
import numpy as np
import os
import math
from tkinter import filedialog
from SRC.CARDS.TOOLS.Tools import *
from SRC.CARDS.Analyser import *

class TestYourProgram(unittest.TestCase):

    def test_order_points(self):
        pts = np.array([[0, 0], [2, 0], [1, 2], [3, 2]], dtype="float32")
        rect = order_points(pts)
        expected_rect = np.array([[0, 0], [2, 0], [3, 2], [1, 2]], dtype="float32")
        self.assertTrue(np.array_equal(rect, expected_rect))

    def test_invert_if_needed(self):
        img = np.zeros((10, 10), dtype=np.uint8)
        threshold = 60  # Example threshold value
        result = invert_if_needed(img, threshold)
        self.assertTrue(np.array_equal(result, img))  # Test if the image remains unchanged

    def test_calculate_angle(self):
        point1 = (1, 0)
        point2 = (1, 1)
        point3 = (0, 0)
        angle = calculate_angle(point1, point2, point3)
        expected_angle = 45.0
        self.assertAlmostEqual(angle, expected_angle)

    def test_find_best_points(self):
        points = [(2, 0), (3, 3), (0, 2), (0, 0)]
        best_points = find_best_points(points)
        expected_points = [(0, 2), (0, 0), (2, 0)]
        self.assertListEqual(best_points, expected_points)

    def test_make_parallelogram(self):
        points = [(0, 0), (2, 0), (3, 3)]
        parallelogram = make_parallelogram(points)
        expected_parallelogram = [(0, 0), (2, 0), (3, 3), (1, 3)]
        self.assertListEqual(parallelogram, expected_parallelogram)

def test_card_recognition():
    path = filedialog.askopenfilename()
    analyser = Analyser(path, debug=True)
    analyser.analyse()
    print(analyser.result)


if __name__ == '__main__':
    #test_card_recognition()
    unittest.main()