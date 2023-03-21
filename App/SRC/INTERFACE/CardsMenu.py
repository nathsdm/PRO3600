"""
CardsMenu.py

Configure the BT settings page.
"""

#-------------------------------------------------------------------#

import tkinter as tk
from SRC.CARDS.CardsManager import CardsManager

#-------------------------------------------------------------------#

class CardsMenu(tk.Frame):
    def __init__(self, master=None, cards_manager=None):
        super().__init__(master)
        master.master.loggers.log.info("Switched to cards menu")
        
        self.master = master
        self.cards_manager = cards_manager
        self.setup_buttons()
        
    def setup_buttons(self):
        self.back_button = tk.Button(self, text="Back", command=lambda: self.master.change_menu(self.master.main_menu))
        self.back_button.pack()
        self.analyse_button = tk.Button(self, text="Analyse", command=lambda: self.cards_manager.analyse_card())
        self.analyse_button.pack()
        self.display_cards()
        
    def display_cards(self):
        """
        Display the cards in the cards manager.
        """
        self.text = tk.Text(self, width = self.master.winfo_screenwidth(), height = self.master.winfo_screenheight(), wrap="none")
        self.text.pack(side="left")
        sb = tk.Scrollbar(self, command=self.text.yview)
        sb.pack(side="right")
        self.text.configure(yscrollcommand=sb.set)
        self.card_buttons = self.cards_manager.get_buttons(self.text)
        count = 0
        for button in self.card_buttons:
            self.text.window_create("end", window=button)
            count += 1
            if count % 7 == 0:
                self.text.insert("end", "\n")
        self.text.configure(state="disabled")
        
    def update(self):
        """
        Update the cards menu.
        """
        self.text.destroy()
        self.display_cards()
       