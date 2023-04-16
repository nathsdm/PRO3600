"""
Main.py

This script is the main entry point for the BT application.
"""

#------------------------------------------------------------#

from SRC.App import App

#------------------------------------------------------------#

if __name__ == "__main__":
    print("Starting Yugi...")
    app = App()
    app.loading()
    app.authentifier.update_filedata()
    print("Yugi stopped.")