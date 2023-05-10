import tkinter as tk
import os
from PIL import Image, ImageTk

class CardDescWindow(tk.Frame):
    def __init__(self, master=None, card = None):
        super().__init__(master)
        self.card = card
        self.frames = []
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
            image.pack(side=tk.LEFT, padx=10)
    
    def setup_label(self):
        """
        Defines the labels used in the cards description window.
        """
        for i in range(len(self.card.infos)):
            info=self.card.infos[i]
            if info == "Sets":
                sets = self.card.infos[i+1:len(self.card.infos)]
                break
            self.labels.append(tk.Label(self, text=info, width=300, wraplength=1000, justify=tk.LEFT, anchor=tk.NW, font=("Matrix-Bold", 12)))
            self.labels[-1].pack()
        
        for j in range(len(sets)):
            if j%4 == 0:
                self.frames.append(tk.Frame(self))
                self.frames[-1].pack(fill="x")
            self.labels.append(tk.Label(self.frames[-1], text=sets[j], wraplength=250, justify=tk.LEFT, anchor=tk.NW, font=("Matrix-Bold", 12)))
            self.labels[-1].pack(side="left", padx=(0, 30))
        
    def setup_buttons(self):
        """
        Defines the buttons used in the cards description window.
        """
        self.buttons.append(tk.Button(self, text="Delete", command=lambda: self.delete(self.master.cards_menu)))
        self.buttons.append(tk.Button(self, text="Back", command=lambda: self.master.change_menu(self.master.cards_menu)))
        for button in self.buttons:
            button.pack()
    
    def delete(self, menu):
        """
        Deletes the card from the database.
        """
        self.master.change_menu(menu)
        self.master.cards_manager.delete_card(self.card)
    
    def update(self, card):
        self.card = card
        if self.card != None:
            self.clear()
            self.setup_images()
            self.setup_label()
            self.setup_buttons()
            
    def clear(self):
        for frame in self.frames:
            frame.destroy()
        for label in self.labels:
            label.destroy()
        for button in self.buttons:
            button.destroy()
        for image in self.images:
            image.destroy()
        self.images = []
        self.buttons = []
        self.labels = []