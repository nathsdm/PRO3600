"""
App.py

This script describes the App class.
"""

#------------------------------------------------------------#

from SRC.INTERFACE.GUI import GUI
from SRC.INTERFACE.LoadingScreen import LoadingScreen

import traceback

#------------------------------------------------------------#


class App:
    NAME = "App"
    def __init__(self, master=None):
        self.master = master
        self.splash_screen = LoadingScreen(self)
        self.splash_screen.authentifier.update_filedata()
    
    def report_callback_exception(self, exc, val, tb):
        print("Exception in Tkinter callback")
        traceback.print_exception(exc, val, tb)
    
    