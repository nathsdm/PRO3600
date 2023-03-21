import tkinter as tk
import os
from PIL import Image, ImageTk

class CardDescWindow(tk.Frame):
    def __init__(self, master=None, card = None):
        super().__init__(master)
        master.master.loggers.log.info("(Cards description window)")
        self.card = card
        self.labels = []
        self.buttons = []
        self.images = []
        
    def setup_images(self):
        """
        Defines the images used in the cards description window.
        """
        card_img = ImageTk.PhotoImage(Image.open(os.path.join("App", "DATA", "CARDS", "IMAGES", self.card.image_name + ".jpg")))
        self.images.append(tk.Label(self, image=card_img))
        self.images[-1].image = card_img
        for image in self.images:
            image.pack()
    
    def setup_label(self):
        """
        Defines the labels used in the cards description window.
        """
        for info in self.card.infos:
            self.labels.append(tk.Label(self, text=info))
        for label in self.labels:
            label.pack()
        
    def setup_buttons(self):
        """
        Defines the buttons used in the cards description window.
        """
        self.buttons.append(tk.Button(self, text="Back", command=lambda: self.master.change_menu(self.master.cards_menu)))
        for button in self.buttons:
            button.pack()
        
    def update(self, card):
        self.card = card
        if self.card != None:
            self.clear()
            self.setup_images()
            self.setup_label()
            self.setup_buttons()
            
    def clear(self):
        for label in self.labels:
            label.destroy()
        for button in self.buttons:
            button.destroy()
        for image in self.images:
            image.destroy()
        self.images = []
        self.buttons = []
        self.labels = []