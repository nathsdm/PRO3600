import tkinter as tk
import os
from PIL import Image, ImageTk
from tkinter import ttk

class CardDescWindow(tk.Frame):
    def __init__(self, master=None, card = None):
        super().__init__(master)
        self.card = card
        self.frames = [tk.Frame(self)]
        self.labels = []
        self.buttons = []
        self.images = []
        # Définir une variable pour stocker la valeur de la Spinbox
        value = tk.IntVar()
        # Configurer la Spinbox
        self.spin_box = ttk.Spinbox(self.frames[0], from_=0, to=1000000, increment=1, textvariable=value)
        
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
        length = len(self.labels)
        
        self.edition = tk.StringVar()
        self.editions = self.card.sets
        self.buttons.append(ttk.OptionMenu(self.frames[0], self.edition, *self.editions, command=lambda num: self.edition_display(sets, self.editions.index(self.edition.get()), length)))
        self.edition.set(self.editions[0])
        self.buttons[0].pack(side="left", padx=(300, 30))
        self.spin_box.set(self.card.quantity)
        self.spin_box.pack(side="left")
        self.frames[0].pack(fill="x")
        self.edition_display(sets, 0, length)
    
    def edition_display(self, sets, num, length):
        if len(self.labels) > length:
            self.labels[-1].destroy()
            self.labels.pop(-1)
        try:
            self.buttons[1].pack_forget()
            self.buttons[2].pack_forget()
        except:
            pass
        for j in range(len(sets)):
            if j == num:
                self.labels.append(tk.Label(self, text=sets[j], wraplength=250, justify=tk.LEFT, anchor=tk.NW, font=("Matrix-Bold", 12)))
                self.labels[-1].pack()
        try:
            self.buttons[1].pack()
            self.buttons[2].pack()
        except:
            pass
        
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
            frame.pack_forget()
        for label in self.labels:
            label.destroy()
        for button in self.buttons:
            button.destroy()
        for image in self.images:
            image.destroy()
        self.spin_box.pack_forget()
        self.images = []
        self.buttons = []
        self.labels = []