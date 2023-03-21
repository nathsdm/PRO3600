"""
App.py

This script describes the App class.
"""

#------------------------------------------------------------#

from SRC.Loggers import Loggers

from SRC.INTERFACE.GUI import GUI
from SRC.INTERFACE.LoadingScreen import LoadingScreen

#------------------------------------------------------------#


class App:
    NAME = "App"
    def __init__(self):
        self.loggers = Loggers(App.NAME)
        self.loggers.log.info("Launching...")
        
        splash_screen = LoadingScreen(self)
        
        self.loggers.log.info("End of the program")
        self.loggers.refreshLogs()
        
        
    def start(self):
        pass
    
    