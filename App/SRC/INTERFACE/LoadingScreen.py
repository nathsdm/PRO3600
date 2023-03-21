"""
SplashScreen.py

This script describes the SplashScreen class.
"""

#------------------------------------------------------------#

import tkinter as tk
from PIL import Image, ImageTk
import os
from tkinter import PhotoImage

from SRC.INTERFACE.GUI import GUI

from SRC.CARDS.CardsManager import CardsManager

from SRC.INTERFACE.GoogleAuth import GoogleAuth

#------------------------------------------------------------#

class LoadingScreen(tk.Tk):
    def __init__(self, app):
        super().__init__()
        self.app = app

        # Set the title of the window
        self.title("Loading...")

        # Remove border of the splash Window
        self.overrideredirect(True)
        self.wm_attributes("-topmost", True)
        try:
            self.wm_attributes("-disabled", True)
            self.wm_attributes("-transparentcolor", "white")
        except:
            pass
        
        image = Image.open("DATA/IMAGES/yugi_transparent.png")
        image = image.resize((400, 200), Image.ANTIALIAS)
        test = ImageTk.PhotoImage(image)
        
        #Define the size of the window or frame
        self.geometry("{}x{}" .format(test.width(), test.height()))
        #self.iconphoto(True, PhotoImage(os.path.join(os.getcwd(), "DATA", "IMAGES", "icone.png")))

        #Define the label of the window
        splash_image= tk.Label(image=test, background="white").pack()
        
        self.center()
        
        self.cards_manager = None
        
        def start():
            self.authentifier = GoogleAuth(self)
            self.deiconify()
            self.cards_manager = CardsManager(self)
            self.destroy()
            self.son = GUI(self.app, self.cards_manager)
            
        self.after(1000, start)

        tk.mainloop()
        
    def center(self):
        """
        centers a tkinter window
        :param self: the main window or Toplevel window to center
        """
        self.update_idletasks()
        width = self.winfo_width()
        frm_width = self.winfo_rootx() - self.winfo_x()
        win_width = width + 2 * frm_width
        height = self.winfo_height()
        titlebar_height = self.winfo_rooty() - self.winfo_y()
        win_height = height + titlebar_height + frm_width
        x = self.winfo_screenwidth() // 2 - win_width // 2
        y = self.winfo_screenheight() // 2 - win_height // 2
        self.geometry('{}x{}+{}+{}'.format(width, height, x, y))
        self.deiconify()