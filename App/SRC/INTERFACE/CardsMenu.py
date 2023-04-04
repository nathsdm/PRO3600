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
        
        self.master = master
        self.cards_manager = cards_manager
        self.setup_buttons()
        self.setup_text()
        self.display_cards()
        
    def setup_buttons(self):
        self.back_button = tk.Button(self, text="Back", command=lambda: self.master.change_menu(self.master.main_menu))
        self.back_button.pack()
        self.analyse_button = tk.Button(self, text="Analyse", command=lambda: self.cards_manager.analyse_card())
        self.analyse_button.pack()
        self.set_code_button = tk.Button(self, text="Set code", command=lambda: self.set_code_query())
        self.set_code_button.pack()
        self.type_options = ["All", "Monster", "Trap", "Spell"]
        self.type_var = tk.StringVar(self)
        self.type_var.set(self.type_options[0])
        self.type_button = tk.OptionMenu(self, self.type_var, *self.type_options, command=lambda x: self.update())
        self.type_button.pack(side="top")
        self.sort_options = ["Name", "Atk", "Def"]
        self.sort_var = tk.StringVar(self)
        self.sort_var.set(self.sort_options[0])
        self.type_button_2 = tk.OptionMenu(self, self.sort_var, *self.sort_options, command=lambda x: self.update())
        self.type_button_2.pack(side="top")
    
    def setup_text(self):
        self.text = tk.Text(self, width = self.master.winfo_screenwidth(), height = self.master.winfo_screenheight()-50, wrap="none", cursor="arrow")
        self.sb = tk.Scrollbar(self, command=self.text.yview, orient="vertical", cursor="arrow", width=20, activebackground="blue")
        self.sb.pack(side="right", fill="y")
        self.text.pack(side="left", fill="both", expand=True)
        self.text.configure(yscrollcommand=self.sb.set)
        
    def set_code_query(self):
        """
        Ask the user to enter the code of the card.
        """
        def set_code():
            code = self.code_query_entry.get()
            self.code_query.destroy()
            self.cards_manager.recognize_card(code)
            self.update()
        
        def cancel_code_query():
            self.code_query.destroy()
        
        self.code_query = tk.Tk()
        self.code_query.title("Set code")
        self.code_query.geometry("300x100")
        self.code_query.protocol("WM_DELETE_WINDOW", cancel_code_query)
        self.code_query_label = tk.Label(self.code_query, text="Enter the code of the card:")
        self.code_query_label.pack()
        
        def upper(*args): # callback function
            code.set(code.get().upper()) # change to Upper case
            if len(code.get()) >= 10:
                code.set(code.get()[:10])
        
        code = tk.StringVar(self.code_query) # declare StringVar()
        self.code_query_entry = tk.Entry(self.code_query,textvariable=code,font=28,width=30)
        code.trace('w',upper) # trigger when variable changes
        
        self.code_query_entry.pack()
        self.code_query_button = tk.Button(self.code_query, text="Ok", command=set_code)
        self.code_query_button.pack()
        
    
    def display_cards(self, select="All", sort="Name"):
        """
        Display the cards in the cards manager.
        """
        self.card_buttons = self.cards_manager.get_buttons(self.text, select, sort)
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
        self.text = tk.Text(self, width = self.master.winfo_screenwidth(), height = self.master.winfo_screenheight(), wrap="none", cursor="arrow")
        self.text.pack(side="left", fill="both", expand=True)
        self.display_cards(self.type_var.get(), self.sort_var.get())
       