"""
App.py

This script describes the App class.
"""

#------------------------------------------------------------#

import traceback

import tkinter as tk
from tkinter import font
from PIL import Image, ImageTk
import os
from tkinter import PhotoImage

from SRC.CARDS.CardsManager import CardsManager

from SRC.INTERFACE.GoogleAuth import GoogleAuth

from SRC.INTERFACE.MainMenu import MainMenu
from SRC.INTERFACE.CreditsMenu import CreditsMenu
from SRC.INTERFACE.SettingsMenu import SettingsMenu
from SRC.INTERFACE.CardsMenu import CardsMenu
from SRC.INTERFACE.WINDOWS.CardDescWindow import CardDescWindow

#------------------------------------------------------------#


class App(tk.Tk):
    def __init__(self, master=None):
        super().__init__()
        self.master = master
        self.card = None
    
    def loading(self):
        self.title("Loading...")

        # Remove border of the splash Window
        self.overrideredirect(True)
        self.wm_attributes("-topmost", True)
        try:
            self.wm_attributes("-disabled", True)
            self.wm_attributes("-transparentcolor", "chocolate4")
        except:
            pass
        
        image = Image.open(os.path.join("APP", "DATA", "IMAGES", "yugi_transparent.png"))
        image = image.resize((400, 200), Image.ANTIALIAS)
        test = ImageTk.PhotoImage(image)
        
        #Define the size of the window or frame
        self.geometry("{}x{}" .format(test.width(), test.height()))
        #self.iconphoto(True, PhotoImage(os.path.join(os.getcwd(), "DATA", "IMAGES", "icone.png")))

        #Define the label of the window
        tk.Label(image=test, background="chocolate4", border=0).pack()
        
        self.center()
            
        self.after(100, self.setup_window)
        
        self.mainloop()
    
    def change_menu(self, next_menu: tk.Frame):
        """
        This function changes the current view to the desired menu.
        """
        # Don't do anything if the desired menu is the same as the current menu
        if next_menu == self.current_menu:
            return

        self.current_menu.pack_forget()
        
        if next_menu == self.card_desc_window:
            self.card_desc_window.update(self.card)
        
        next_menu.pack(fill=tk.BOTH, expand=True)
        # Update the current menu reference
        self.current_menu = next_menu
    
    def setup_window(self):
        """
        Setup the window of the application.
        """
        self.authentifier = GoogleAuth(self)
        self.cards_manager = CardsManager(self)
        for w in self.winfo_children():
            w.destroy()
        self.overrideredirect(False)
        try:
            self.wm_attributes("-disabled", False)
        except:
            pass
        self.title("Yugi")
        photo = Image.open(os.path.join("APP", "DATA", "IMAGES", "icone.png"))
        photo = ImageTk.PhotoImage(photo)
        self.iconphoto(True, photo)
        self.attributes("-fullscreen", True)
        self.resizable(False, False)
        # Creating a Font object of "TkDefaultFont"
        self.defaultFont = font.nametofont("TkDefaultFont")
  
        # Overriding default-font with custom settings
        # i.e changing font-family, size and weight
        self.defaultFont.configure(family="Matrix-Bold",
                                   size=19,
                                   weight=font.BOLD)
        self.protocol("WM_DELETE_WINDOW", self.quit)
        self.focus_force()
        self.setup_menus()
        
    def setup_menus(self):
        """
        Setup the different menus of the application.
        """
        self.main_menu = MainMenu(self)
        self.credits_menu = CreditsMenu(self)
        self.settings_menu = SettingsMenu(self)
        self.cards_menu = CardsMenu(self, self.cards_manager)
        self.card_desc_window = CardDescWindow(self, self.card)
        
        self.main_menu.pack(fill=tk.BOTH, expand=True)
        self.current_menu = self.main_menu
        
        
    def center(self):
        """
        Centers a Tkinter window on the screen.
        """
        self.update_idletasks()
        width = self.winfo_width()
        height = self.winfo_height()
        
        frm_width = self.winfo_rootx() - self.winfo_x()
        frm_height = self.winfo_rooty() - self.winfo_y()
        
        win_width = width + 2 * frm_width
        win_height = height + frm_height + frm_width
        
        x = self.winfo_screenwidth() // 2 - win_width // 2
        y = self.winfo_screenheight() // 2 - win_height // 2
        
        self.geometry(f'{width}x{height}+{x}+{y}')
        self.deiconify()
    