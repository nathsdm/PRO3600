import SRC.CARDS.cardinfo as cardinfo
from SRC.CARDS.Card import Card
import os
import random
import difflib
from urllib.request import urlretrieve
import tkinter as tk
from slugify import slugify
from PIL import Image, ImageTk
from functools import partial
from SRC.CARDS.Analyser import Analyser
from SRC.CARDS.TOOLS.Tools import *
from tkinter import filedialog as fd
import cv2
import numpy as np


class CardsManager:
    def __init__(self, master=None):
        self.master = master
        self.cards = []
        self.cards_names = {}
        self.names = {}
        self.cards_path = os.path.join("App", "DATA", "CARDS", "cards.txt")
        self.info_fr = cardinfo.info_fr["data"]
        self.info_en = cardinfo.info_en["data"]
        self.refs = {}
        self.price = 0
        self.collections = []
        path = os.path.join("App", "DATA", "CARDS", "IMAGES")        
        if os.path.isdir(path):
            print("Images folder found")
        else:
            os.makedirs(name=path)
        self.setup_names()
        self.setup_refs()
        self.start()
        
    def collection_price(self):
        self.get_price()
        self.master.cards_menu.price_label.config(text="Collection price : " + str(self.price) + " $")
    
    def get_price(self):
        self.price = 0
        for card in self.cards:
            self.price += float(card.price)*card.quantity
        self.price = round(self.price, 2)
        return str(self.price)
        
            

    def setup_cards(self):
        """
        Defines the cards used in the cards menu.
        """
        self.cards_ref = []
        if os.path.isdir(os.path.join("App", "DATA", "CARDS")):
            print("Cards folder found")
        else:
            os.makedirs(name=os.path.join("App", "DATA", "CARDS"))
        if os.path.isfile(self.cards_path):
            for ref in open(file=os.path.join("App", "DATA", "CARDS", "cards.txt"), mode="r"):
                if ref != "UNKNOWN":
                    self.cards_ref.append(ref.rstrip('\n'))
        else:
            print("No cards found")
    
    def start(self):
        self.setup_cards()
        index = 0
        for k in self.cards_ref:
            if k in self.cards_ref[:index]:
                for card in self.cards:
                    if card.set_code == k:
                        card.add_quantity(1)
                    break
            else:
                self.find_card(k)
            
            index+=1
        
    def setup_refs(self):
        for ref in self.names.values():
            self.refs[''.join([char for char in ref if char.isupper() or char.isdigit()])] = ref
    
    def setup_names(self):
        for k in self.info_en:
            if k.get("card_sets") != None:
                for num in range(len(k.get("card_sets"))):
                    if k.get('name') in self.names.keys():
                        self.names[k.get("name")] += [k.get("card_sets")[num]["set_code"]]
                    else:
                        self.names[k.get("name")] = [k.get("card_sets")[num]["set_code"]]
                    if k.get('name') not in self.cards_names:
                        self.cards_names[k.get('name').replace(" ","").upper()] = k.get('name')
            else:
                pass
            
        for k in self.info_fr:
            if k.get("card_sets") != None:
                for num in range(len(k.get("card_sets"))):
                    if k.get('name') in self.names.keys():
                        self.names[k.get("name")] += [k.get("card_sets")[num]["set_code"].replace("EN", "FR")]
                    else:
                        self.names[k.get("name")] = [k.get("card_sets")[num]["set_code"].replace("EN", "FR")]
                        
                    if k.get('name') not in self.cards_names:
                        self.cards_names[k.get('name').replace(" ","").upper()] = k.get('name')
            else:
                pass
    
    def find_card(self, ref):
        if "FR" in ref:
            self.leng = "FR"
            self.info = self.info_fr
        else:
            self.leng = "EN"
            self.info = self.info_en
        card_name = [k for k in self.names.keys() if ref in self.names.get(k)]
        if len(card_name) == 0:
            return "UNKNOWN"
        card_name = card_name[0]
        self.cards.append(Card(self, ref, card_name, self.info, self.leng))
        self.download_card(self.cards[-1])
        return ref
    
    def recognize_card(self, ref, name=None, image_path=None):
        probas = difflib.get_close_matches(ref, self.refs.keys(), n=1, cutoff=0.5)
        if name != None:
            closest = difflib.get_close_matches(name, self.cards_names.keys(), n=1, cutoff=0.5)[0]
            closest = self.cards_names.get(closest)
        
        if len(probas) == 0:
            probas = [""]
            
        finding = self.refs.get(probas[0], None)
            
        if finding not in self.names.get(closest):
            finding = difflib.get_close_matches(ref, self.names.get(closest), n=1, cutoff=0.5)
            if len(finding) == 0:
                finding = self.names.get(closest)
            finding = finding[0]
        
        if "FR" in finding:
            self.leng = "FR"
            self.info = self.info_fr
        else:
            self.leng = "EN"
            self.info = self.info_en  
            
        card_name = closest
        for card in self.cards:
            if card.name == card_name:
                card.add_quantity(1)
                return finding if self.leng == "EN" else finding.replace("EN", "FR")
        self.cards.append(Card(self, finding, card_name, self.info, self.leng))
        self.download_card(self.cards[-1])
            
        return finding if self.leng == "EN" else finding.replace("EN", "FR")
    
    def get_buttons(self, select="All", sort="Name", race="All", attribute="All", collection="Global"):
        def display_card(card):
            self.master.card = card
            self.master.change_menu(self.master.card_desc_window)
        buttons = []
        match sort:
            case "Name":
                self.cards.sort(key=lambda x: x.name)
            case "Atk":
                self.cards.sort(key=lambda x: x.atk if x.atk != None else 0, reverse=True)
            case "Def":
                self.cards.sort(key=lambda x: x.defense if x.defense != None else 0, reverse=True)
            case "Level":
                self.cards.sort(key=lambda x: x.level if x.level != None else 0, reverse=True)
            case "Price":
                self.cards.sort(key=lambda x: float(x.price) if x.price != None else 0, reverse=True)
        if len(self.collections) != 0 and collection != "Global":
            collection = [col[1] for col in self.collections if col[0] == collection][0]
        for card in self.cards:
            if select in card.type or select == "All":
                if race in card.race or race == "All":
                    if attribute in card.attribute or attribute == "All":
                        if card.set_code in collection or collection == "Global":
                            buttons.append([card, partial(display_card, card)])
        return buttons
    
    def download_card(self, card):
        """
        Download the card image.
        """
        # Download the image from the URL
        filename = slugify(card.name) + ".jpg"
        path = os.path.join("App", "DATA", "CARDS", "IMAGES")
        image_path = os.path.join(path, filename)
        
        if not os.path.isfile(image_path):
            urlretrieve(card.image, image_path)
        
        return image_path
        
    def analyse_card(self):
        image_path = fd.askopenfilename(title = "Choose an image", filetypes=[("Image files", ".jpg .png")])
        if image_path == None or image_path == "":
            return
        analyser = Analyser(image_path)
        analyser.analyse()
        ref = self.recognize_card(analyser.result[0], analyser.result[1], image_path)
        if ref != "UNKNOWN":
            self.add_card(ref)
            
    def create_collection(self):
        collection_window = tk.Toplevel(self.master)
        collection_window.title("Create Collection")
        collection_window.geometry("800x600")
        collection_window.resizable(False, False)
        collection_window.iconbitmap(os.path.join("App", "DATA", "IMAGES", "icone.ico"))
        collection_window.config(bg="#1e1e1e")
        collection_window.focus_force()
        collection_window.grab_set()

        selected_cards = []

        def update_collection_checkboxes():
            for card, var in checkbox_vars.items():
                var.set(card in selected_cards)

        def update_selected_cards():
            selected_cards.clear()
            for card, var in checkbox_vars.items():
                if var.get():
                    selected_cards.append(card)

        def create_selected_collection():
            collection_name = collection_entry.get().strip()
            if collection_name in [col[0] for col in self.collections]:
                tk.messagebox.showerror("Collection Name Error", f"Collection '{collection_name}' already exists!")
                return
            if collection_name and selected_cards:
                selected_collection = [card.set_code for card in selected_cards]
                self.collections.append([collection_name, selected_collection])
                # Save the selected collection to a file or perform any other desired operation
                print(f"Selected Collection '{collection_name}': {selected_collection}")
                self.master.cards_menu.update_collections(self.collections)
                collection_window.destroy()
                tk.messagebox.showinfo("Collection Created", f"Collection '{collection_name}' created successfully!")
                if not os.path.isdir(os.path.join("App", "DATA", "COLLECTIONS")):
                    os.mkdir(os.path.join("App", "DATA", "COLLECTIONS"))
                with open(os.path.join("App", "DATA", "COLLECTIONS", "COLLECTION_" + str(len(self.collections)) + ".txt"), "w") as f:
                    f.write("nom:" + collection_name + "\n")
                    for card in selected_cards:
                        f.write(card.set_code + "\n")
                

        collection_label = tk.Label(collection_window, text="Select Cards for Collection:")
        collection_label.pack(pady=10)

        collection_frame = tk.Frame(collection_window)
        collection_frame.pack(padx=10, pady=10, fill=tk.BOTH, expand=True)

        canvas = tk.Canvas(collection_frame, bg="#1e1e1e")
        canvas.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)

        checkbox_frame = tk.Frame(canvas, bg="#1e1e1e")
        canvas.create_window((0, 0), window=checkbox_frame, anchor=tk.NW)

        scrollbar = tk.Scrollbar(collection_frame, orient=tk.VERTICAL, command=canvas.yview)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
        canvas.config(yscrollcommand=scrollbar.set)
        
        
        
        def configure_canvas(event):
            canvas.configure(scrollregion=canvas.bbox("all"))

        def mousewheel(event):
            canvas.yview_scroll(int(-1*(event.delta/120)), "units")
            
        # Bind mousewheel event to scrollbar
        canvas.bind("<Configure>", configure_canvas)
        canvas.bind_all("<MouseWheel>", mousewheel)
        
        checkbox_vars = {}
        for card in self.cards:
            var = tk.BooleanVar()
            checkbox_vars[card] = var
            checkbox = tk.Checkbutton(checkbox_frame, text=f"{card.name} ({card.quantity})", variable=var,
                                    onvalue=True, offvalue=False, command=update_selected_cards,
                                    bg="#1e1e1e", fg="white", activebackground="#1e1e1e", selectcolor="#1e1e1e")
            checkbox.pack(anchor=tk.W)

        update_collection_checkboxes()

        collection_entry_frame = tk.Frame(collection_window)
        collection_entry_frame.pack()

        collection_entry_label = tk.Label(collection_entry_frame, text="Collection Name:")
        collection_entry_label.pack(side=tk.LEFT)

        collection_entry = tk.Entry(collection_entry_frame, width=40)
        collection_entry.pack(side=tk.LEFT)

        create_button = tk.Button(collection_window, text="Create Collection", command=create_selected_collection)
        create_button.pack(pady=10)






    
    
    def import_cards(self):
        file = fd.askopenfilename(title = "Choose a file", filetypes=[("Text files", ".txt")])
        with open(file, "r") as f:
            lines = f.readlines()
        for line in lines:
            ref = self.find_card(line.strip("\n"))
            self.add_card(ref)
            
    def export_cards(self):
        file = fd.asksaveasfile(title = "Choose a file", filetypes=[("Text files", ".txt")])
        for card in self.cards:
            file.write(card.set_code)
            file.write("\n")
    
        
    def add_card(self, ref):
        with open(self.cards_path, 'a') as f:
            f.write(ref)
            f.write("\n")
        self.master.cards_menu.update()
        self.collection_price()
        
    def delete_card(self, card, mode=0):
        with open(os.path.join("App", "DATA", "CARDS", "cards.txt"), "r") as f:
            lines = f.readlines()
        with open(os.path.join("App", "DATA", "CARDS", "cards.txt"), "w") as f:
            count = 0
            for line in lines:
                if line.strip("\n") == card.set_code and count == 0:
                    count += 1
                else:
                    f.write(line)
        if mode == 0:
            self.cards.remove(card)
        self.master.cards_menu.update()
        self.collection_price()
        
    def update_quantity(self, card, quantity):
        count = 0
        for card_ref in self.cards:
            if card_ref.set_code == card.set_code:
                card_ref.quantity_update(quantity)
                break
        self.collection_price()
    
    def reset(self):
        message = "Êtes-vous sûr de vouloir réinitialiser votre collection ?"
        if tk.messagebox.askyesno("Réinitialiser", message):
            with open(os.path.join("App", "DATA", "CARDS", "cards.txt"), "w") as f:
                f.write("")
            self.cards = []
            self.cards_ref = []
            self.master.authentifier.update_filedata()
            self.master.cards_menu.update()
            self.collection_price()