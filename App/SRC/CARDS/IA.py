import matplotlib.pyplot as plt
import os
import keras_ocr

# keras-ocr will automatically download pretrained
# weights for the detector and recognizer.
pipeline = keras_ocr.pipeline.Pipeline()

# Get a set of three example images
images = [
    keras_ocr.tools.read(url) for url in [
        os.path.join(os.getcwd(), "App", "DATA", "CARDS", "IMAGES", "blue-eyes-white-dragon.jpg"),
        os.path.join(os.getcwd(), "App", "DATA", "CARDS", "IMAGES", "dark-magician.jpg")
    ]
]

# Each list of predictions in prediction_groups is a list of
# (word, box) tuples.
prediction_groups = pipeline.recognize(images)
print(prediction_groups)