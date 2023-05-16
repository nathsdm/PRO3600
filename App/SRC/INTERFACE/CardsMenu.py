"""
CardsMenu.py

Configure the BT settings page.
"""

#-------------------------------------------------------------------#

import tkinter as tk
from SRC.CARDS.CardsManager import CardsManager

from SRC.INTERFACE.ScrollableImageList import ScrollableImageList

#-------------------------------------------------------------------#

class CardsMenu(tk.Frame):
    def __init__(self, master=None, cards_manager=None):
        super().__init__(master)
        
        self.master = master
        self.cards_manager = cards_manager
        self.mode = 0
        self.setup_buttons()
        self.scrollable_frame = tk.Frame(self)
        self.tk_images = self.cards_manager.get_buttons()
        self.canvas = ScrollableImageList(self.scrollable_frame, self.tk_images, num_columns=7, mode=self.mode)
        
        self.setup_frame()
        self.setup_scroll()
    
    def setup_frame(self):
        self.scrollable_frame.pack(side="left", fill="both", expand=True)
        self.canvas.pack(side="left", fill="both", expand=True)
    
    def create_collection(self):
        self.cards_manager.create_collection()
        
    def setup_buttons(self):
        self.top_frame = tk.Frame(self)
        self.price_label = tk.Label(self.top_frame, text="Collection price : " + self.master.cards_manager.get_price() + " $")
        self.price_label.pack(side="left", padx=(0, 50))
        self.top_frame.pack(side="top", pady=(50, 0))
        
        self.buttons_frame = tk.Frame(self)
        
        self.type_label = tk.Label(self.buttons_frame, text="Type:")
        self.type_label.pack(side="left", padx=10)
        self.type_options = ["All", "Monster", "Trap", "Spell"]
        self.type_var = tk.StringVar(self)
        self.type_var.set(self.type_options[0])
        self.type_button = tk.OptionMenu(self.buttons_frame, self.type_var, *self.type_options, command=lambda x: self.update())
        self.type_button.pack(side="left", padx=(0, 50))
        
        self.race_label = tk.Label(self.buttons_frame, text="Race:")
        self.race_label.pack(side="left", padx=10)
        self.race_options = ["All", "Aqua", "Beast", "Beast-Warrior", "Creator-God", "Cyberse", "Dinosaur", "Divine-Beast", "Dragon", "Fairy", "Fiend", "Fish", "Insect", "Machine", "Plant", "Psychic", "Pyro", "Reptile", "Rock", "Sea Serpent", "Spellcaster", "Thunder", "Warrior", "Winged Beast", "Wyrm", "Zombie", "Normal", "Field", "Equip", "Continuous", "Quick-Play", "Ritual", "Counter"]
        self.race_var = tk.StringVar(self)
        self.race_var.set(self.race_options[0])
        self.race_button = tk.OptionMenu(self.buttons_frame, self.race_var, *self.race_options, command=lambda x: self.update())
        self.race_button.pack(side="left", padx=(0, 50))
        
        self.attribute_label = tk.Label(self.buttons_frame, text="Attribute:")
        self.attribute_label.pack(side="left", padx=10)
        self.attribute_options = ["All", "Dark", "Divine", "Earth", "Fire", "Light", "Water", "Wind"]
        self.attribute_var = tk.StringVar(self)
        self.attribute_var.set(self.attribute_options[0])
        self.attribute_button = tk.OptionMenu(self.buttons_frame, self.attribute_var, *self.attribute_options, command=lambda x: self.update())
        self.attribute_button.pack(side="left", padx=(0, 50))
        
        self.sort_label = tk.Label(self.buttons_frame, text="Sort by:")
        self.sort_label.pack(side="left", padx=10)
        self.sort_options = ["Name", "Atk", "Def", "Level", "Price"]
        self.sort_var = tk.StringVar(self)
        self.sort_var.set(self.sort_options[0])
        self.sort_button = tk.OptionMenu(self.buttons_frame, self.sort_var, *self.sort_options, command=lambda x: self.update())
        self.sort_button.pack(side="left", padx=(0, 50))
        
        self.collection_label = tk.Label(self.buttons_frame, text="Collection:")
        self.collection_label.pack(side="left", padx=10)
        self.collection_options = ["Global"]
        self.collection_var = tk.StringVar(self)
        self.collection_var.set(self.collection_options[0])
        self.collection_button = tk.OptionMenu(self.buttons_frame, self.collection_var, *self.collection_options, command=lambda x: self.update())
        self.collection_button.pack(side="left", padx=(0, 50))
        
        self.back_button = tk.Button(self.buttons_frame, text="Back", command=lambda: self.master.change_menu(self.master.main_menu))
        self.back_button.pack(side="left", padx=(150, 0))
        self.buttons_frame.pack(side="top", pady=(25, 25))
        
    def change_view(self):
        self.mode = (self.mode + 1) % 2
        self.canvas.change_view(self.mode)
        
    def setup_scroll(self, select="All", sort="Name", race="All", attribute="All", collection="Global"):
        self.tk_images = self.cards_manager.get_buttons(select, sort, race, attribute, collection)
        self.canvas.update(self.tk_images)
        
    def update_collections(self, collections):
        self.collection_options = ["Global"] + [collection[0] for collection in collections]
        self.collection_button.destroy()
        self.collection_button = tk.OptionMenu(self.buttons_frame, self.collection_var, *self.collection_options, command=lambda x: self.update())
        self.back_button.pack_forget()
        self.collection_button.pack(side="left", padx=(0, 50))
        self.back_button.pack(side="left", padx=(150, 0))
        
        
    
    def set_code_query(self):
        """
        Ask the user to enter the code of the card.
        """
        def center(window):
            """
            Centers a Tkinter window on the screen.
            """
            window.update_idletasks()
            width = window.winfo_width()
            height = window.winfo_height()
            
            frm_width = window.winfo_rootx() - window.winfo_x()
            frm_height = window.winfo_rooty() - window.winfo_y()
            
            win_width = width + 2 * frm_width
            win_height = height + frm_height + frm_width
            
            x = window.winfo_screenwidth() // 2 - win_width // 2
            y = window.winfo_screenheight() // 2 - win_height // 2
            
            window.geometry(f'{width}x{height}+{x}+{y}')
            window.deiconify()
            
        def set_code():
            code = self.code_query_entry.get()
            self.code_query.destroy()
            code = self.cards_manager.find_card(code)
            if code == "UNKNOWN":
                tk.messagebox.showerror("Error", "The code you entered is not valid.")
                return
            self.cards_manager.add_card(code)
        
        def cancel_code_query():
            self.code_query.destroy()
        
        self.code_query = tk.Toplevel(self)
        self.code_query.title("Set code")
        self.code_query.geometry("300x100")
        self.code_query.protocol("WM_DELETE_WINDOW", cancel_code_query)
        self.code_query.resizable(False, False)
        self.code_query.focus_force()
        self.code_query.grab_set()
        self.code_query.attributes("-topmost", True)
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
        
        center(self.code_query)
        
        
    def update(self):
        """
        Update the cards menu.
        """
        self.setup_scroll(self.type_var.get(), self.sort_var.get(), self.race_var.get(), self.attribute_var.get(), self.collection_var.get())
       