"""
SettingsMenu.py

Configure the BT settings page.
"""

#-------------------------------------------------------------------#

import tkinter as tk

#-------------------------------------------------------------------#

class SettingsMenu(tk.Frame):
    def __init__(self, master=None):
        super().__init__(master)
        self.configure(bg="red")
        
        self.settings = "day/night mode\nmode dev\n"
        self.settings_label = tk.Label(self, text=self.settings, bg="red")
        self.settings_label.pack()
        
        self.reset_button = tk.Button(self, text="Reset", command=lambda: self.reset())
        self.reset_button.pack()
        
        self.back_button = tk.Button(self, text="Back", command=lambda: master.change_menu(master.main_menu))
        self.back_button.pack()
    
    def reset(self):
        print("reset")
        self.master.reset()
        self.master.change_menu(self.master.main_menu)