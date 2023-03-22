"""
MainMenu.py

Configure the BT main page.
"""

#-------------------------------------------------------------------#

import tkinter as tk
from PIL import ImageTk, Image
import os

#-------------------------------------------------------------------#

class MainMenu(tk.Frame):
    def __init__(self, master=None):
        super().__init__(master)
        master.master.loggers.log.info("(Main menu)")
        self.setup_images()
        self.setup_label()
        self.setup_buttons()
        
    def setup_images(self):
        """
        Defines the images used in the main menu.
        """
        x, y = self.master.geometry().split("+")[0].split("x")
        background_image = Image.open(os.path.join("App", "DATA", "IMAGES", "background.jpg"))
        background_image = background_image.resize((int(x), int(y)), Image.ANTIALIAS)
        background_image = ImageTk.PhotoImage(image=background_image)
        background_label = tk.Label(self, image=background_image, width=x, height=y)
        background_label.image = background_image
        background_label.place(x=0, y=0, relwidth=1, relheight=1)
    
    def setup_label(self):
        """
        Defines the labels used in the main menu.
        """
        self.title_label = tk.Label(self, text="Yu-gi-oh app", font=("Matrix-Bold" , 50), fg="white", bg="black")
        self.title_label.pack()	
    
    def setup_buttons(self):
        """
        Defines the buttons used in the main menu.
        """
        self.play_btn = tk.Button(self, text="View cards", command=lambda: self.master.change_menu(self.master.cards_menu))
        self.credits_btn = tk.Button(self, text="Credits", command=lambda: self.master.change_menu(self.master.credits_menu))
        self.settings_btn = tk.Button(self, text="Settings", command=lambda: self.master.change_menu(self.master.settings_menu))
        self.quit_btn = tk.Button(self, text="Quit", command=self.master.quit)
        
        self.play_btn.pack()
        self.credits_btn.pack()
        self.settings_btn.pack()
        self.quit_btn.pack()
        