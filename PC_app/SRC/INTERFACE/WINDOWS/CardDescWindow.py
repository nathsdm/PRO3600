import tkinter as tk
import os
from PIL import Image, ImageTk
from tkinter import ttk

class CardDescWindow(tk.Frame):
    def __init__(self, master=None, card=None):
        super().__init__(master)
        self.card = card
        self.frames = [tk.Frame(self)]
        self.labels = []
        self.buttons = []
        self.images = []

        # Define a variable to store the value of the Spinbox
        value = tk.IntVar()

        # Configure the Spinbox
        self.spin_box = ttk.Spinbox(
            self.frames[0], 
            from_=0, to=10000, increment=1, 
            textvariable=value, 
            command=lambda: self.update_quantity(value.get()), 
            width=5, 
            state="readonly"
        )

    def setup_images(self):
        """Define the images used in the card description window."""
        card_img = ImageTk.PhotoImage(
            Image.open(os.path.join("DATA", "CARDS", "IMAGES", self.card.image_name + ".jpg"))
        )
        self.images.append(tk.Label(self, image=card_img))
        self.images[-1].image = card_img
        for image in self.images:
            image.pack(side=tk.LEFT, padx=10)

    def setup_labels(self):
        """Define the labels used in the card description window."""
        for i in range(len(self.card.infos)):
            info = self.card.infos[i]
            if info == "Sets":
                sets = self.card.infos[i+1:len(self.card.infos)]
                break
            self.labels.append(
                tk.Label(self, 
                    text=info, width=300, 
                    wraplength=1000, justify=tk.LEFT, 
                    anchor=tk.NW, 
                    font=("Matrix-Bold", 12)
                )
            )
            self.labels[-1].pack()
        length = len(self.labels)+2

        self.labels.append(
            tk.Label(
                self.frames[0], 
                text="Set:", justify=tk.LEFT, 
                anchor=tk.NW, 
                font=("Matrix-Bold", 12)
            )
        )
        self.labels[-1].pack(side="left", padx=(300, 30))
        self.edition = tk.StringVar()
        self.editions = self.card.sets
        self.buttons.append(
            ttk.OptionMenu(
                self.frames[0], 
                self.edition, *self.editions, 
                command=lambda num: self.edition_display(sets, self.editions.index(self.edition.get()), length)
            )
        )
        self.edition.set(self.card.set_code)
        self.buttons[0].pack(side="left", padx=(0, 30))
        self.labels.append(
            tk.Label(
                self.frames[0], 
                text="Quantity:", 
                justify=tk.LEFT, 
                anchor=tk.NW, 
                font=("Matrix-Bold", 12)
            )
        )
        self.labels[-1].pack(side="left", padx=(100, 30))
        self.spin_box.set(self.card.quantity)
        self.spin_box.pack(side="left")
        self.frames[0].pack(fill="x", pady=(20, 0))
        self.edition_display(sets, self.editions.index(self.card.set_code.replace("FR", "EN")), length)

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
        if self.card.set_code != self.edition.get():
            with open(os.path.join("DATA", "CARDS", "cards.txt"), "r") as file:
                lines = file.readlines()
            with open(os.path.join("DATA", "CARDS", "cards.txt"), "w") as file:
                for line in lines:
                    if line.strip("\n") == self.card.set_code:
                        file.write(self.edition.get())
                        file.write("\n")
                    else:
                        file.write(line)
            self.card.set_code = self.edition.get()
            self.card.update()
        
    def setup_buttons(self):
        """
        Defines the buttons used in the cards description window.
        """
        def back():
            self.master.cards_menu.update()
            self.master.change_menu(self.master.cards_menu)
        self.buttons.append(tk.Button(self, text="Delete", command=lambda: self.delete(self.master.cards_menu)))
        self.buttons.append(tk.Button(self, text="Back", command=back))
        for button in self.buttons:
            button.pack()
    
    def delete(self, menu):
        """
        Deletes the card from the database.
        """
        self.master.change_menu(menu)
        self.master.cards_manager.delete_card(self.card)
        
    def update_quantity(self, quantity):
        """
        Updates the quantity of the card.
        """
        self.master.cards_manager.update_quantity(self.card, quantity)    
        
        
        
    
    
    
    def update(self, card):
        self.card = card
        if self.card != None:
            self.clear()
            self.setup_images()
            self.setup_labels()
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