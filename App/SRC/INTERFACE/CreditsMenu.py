"""
CreditsMenu.py

Configure the BT credits page.
"""

#-------------------------------------------------------------------#

import tkinter as tk
from PIL import Image, ImageTk
import os

#-------------------------------------------------------------------#

class CreditsMenu(tk.Frame):
    def __init__(self, master=None):
        super().__init__(master)
        self.master = master
        self.master.master.loggers.log.info("(Credits menu)")
        
        
        self.lorem_ipsum = """
            Ce projet a été développé par:
            - Nathan SOARES DE MELO
            - Clément CHRETIEN
            - Clément BOURVIC
            - Alexis ROUX
        """

        
        self.setup_labels()
        
        self.back_button = tk.Button(self, text="Back", command=lambda: master.change_menu(master.main_menu))
        self.back_button.pack(side="bottom", pady=50)
        
    def setup_labels(self):
        """
        Defines the labels used in the credits menu.
        """
        
        background_image = Image.open(os.path.join("App", "DATA", "IMAGES", "background_credits.jpg"))
        image = ImageTk.PhotoImage(background_image)
        x, y = self.master.geometry().split("+")[0].split("x")
        background_label = tk.Label(self, image=image, width=x, height=y)
        background_label.image = image
        background_label.place(x=0, y=0, relwidth=1, relheight=1)
        self.lorem_label = tk.Label(self, text=self.lorem_ipsum, bg="white")
        self.lorem_label.pack(side="left", padx=50, pady=50)