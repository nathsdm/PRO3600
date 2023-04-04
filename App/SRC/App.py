"""
App.py

This script describes the App class.
"""

#------------------------------------------------------------#

from SRC.INTERFACE.GUI import GUI
from SRC.INTERFACE.LoadingScreen import LoadingScreen

#------------------------------------------------------------#


class App:
    NAME = "App"
    def __init__(self, master=None):
        self.master = master
        self.splash_screen = LoadingScreen(self)
        self.splash_screen.authentifier.update_filedata()
        
        
    def start(self):
        pass
    
    