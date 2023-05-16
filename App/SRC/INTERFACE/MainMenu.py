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
        self.title_label = tk.Label(self, text="Yu-gi-oh app", font=("Matrix-Bold" , 50), fg="white", bg="#103188")
        self.title_label.place(x=200, y=200, width=400, height=100)	
    
    def setup_buttons(self):
        """
        Defines the buttons used in the main menu.
        """
        play_btn_image = ImageTk.PhotoImage(Image.open(os.path.join("App", "DATA", "IMAGES", "Play_btn.png")))
        self.play_btn = tk.Button(self, text="View cards", command=lambda: self.master.change_menu(self.master.cards_menu), bg="#103188", fg="white", border=0, cursor="hand2", activebackground="#103188")
        self.quit_btn = tk.Button(self, text="Quit", command=self.master.quit, bg="#103188", fg="white", border=0, cursor="hand2", activebackground="#103188")
        
        self.play_btn.place(x=200, y=400, width=400, height=100)
        self.quit_btn.place(x=200, y=500, width=400, height=100)
        