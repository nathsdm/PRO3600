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
        master.master.loggers.log.info("Switched to settings menu")
        
        self.settings = "day/night mode\nvolume\nmode dev\n"
        self.settings_label = tk.Label(self, text=self.settings, bg="red")
        self.settings_label.pack()
        
        self.back_button = tk.Button(self, text="Back", command=lambda: master.change_menu(master.main_menu))
        self.back_button.pack()