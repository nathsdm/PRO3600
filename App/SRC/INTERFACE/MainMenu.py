"""
MainMenu.py

Configure the BT main page.
"""

#-------------------------------------------------------------------#

import tkinter as tk

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
        return
    
    def setup_label(self):
        """
        Defines the labels used in the main menu.
        """
        self.title_label = tk.Label(self, text="Yu-gi-oh app", font=("System", 30))
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
        