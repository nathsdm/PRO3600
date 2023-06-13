import SRC.CARDS.cardinfo as cardinfo
from SRC.CARDS.Card import Card
import os
import difflib
from urllib.request import urlretrieve
import tkinter as tk
from slugify import slugify
from PIL import Image, ImageTk
from functools import partial
from SRC.CARDS.Analyser import Analyser
from SRC.CARDS.TOOLS.Tools import *
from tkinter import filedialog as fd
import numpy as np
from tkinter import ttk
from unidecode import unidecode


class CardsManager:
    def __init__(self, master=None, debug=False):
        self.master = master
        self.debug = debug
        self.cards = []
        self.cards_names = {}
        self.names = {}
        self.cards_path = os.path.join("DATA", "CARDS", "cards.txt")
        self.info_fr = cardinfo.info_fr["data"]
        self.info_en = cardinfo.info_en["data"]
        self.refs = {}
        self.price = 0
        self.collections = []
        path = os.path.join("DATA", "CARDS", "IMAGES")        
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
        
    def setup_collections(self):
        if os.path.isfile(os.path.join("DATA", "COLLECTIONS", "COLLECTION_1.txt")):
            for filename in os.listdir(os.path.join("DATA", "COLLECTIONS")):
                collection = []
                with open(file=os.path.join("DATA", "COLLECTIONS", filename), mode="r") as f:
                    for line in f:
                        collection.append(line.rstrip('\n'))
                self.collections.append([collection[0][4:], collection[1:]])
            return self.collections

        else:
            return []
            print("No collections found")

    def setup_cards(self):
        """
        Defines the cards used in the cards menu.
        """
        self.cards_ref = []
        if os.path.isdir(os.path.join("DATA", "CARDS")):
            print("Cards folder found")
        else:
            os.makedirs(name=os.path.join("DATA", "CARDS"))
        if os.path.isfile(self.cards_path):
            file = open(file=self.cards_path, mode="r+")
            for ref in file:
                if ref.strip('/n') != "UNKNOWN":
                    self.cards_ref.append(ref.rstrip('\n'))
                else:
                    del ref
        else:
            print("No cards found")
    
    def start(self):
        self.setup_cards()
        index = 0
        for k in self.cards_ref:
            if k in self.cards_ref[:index]:
                for card in self.cards:
                    if card.set_code == k:
                        card.add_quantity()
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
                        self.cards_names[unidecode(k.get('name').replace(" ","").upper())] = k.get('name')
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
                        self.cards_names[unidecode(k.get('name').replace(" ","").upper())] = k.get('name')
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
        for card in self.cards:
            if card.name == card_name:
                card.add_quantity(new_card=True)
                return ref if self.leng == "EN" else ref.replace("EN", "FR")
        self.cards.append(Card(self, ref, card_name, self.info, self.leng))
        self.download_card(self.cards[-1])
        return ref
    
    def recognize_card(self, ref, name=None, image_path=None):
        probas = difflib.get_close_matches(ref, self.refs.keys(), n=1, cutoff=0.5)
        if name != None:
            closest = difflib.get_close_matches(name, self.cards_names.keys(), n=1, cutoff=0.5)
            if len(closest) != 0:
                closest = self.cards_names.get(closest[0])
            else:
                tk.messagebox.showerror("Card Recognition Error", f"Card '{name}' not found! Error while reading the card, please try again.")
        
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
                card.add_quantity(new_card=True)
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
        path = os.path.join("DATA", "CARDS", "IMAGES")
        image_path = os.path.join(path, filename)
        
        if not os.path.isfile(image_path):
            urlretrieve(card.image, image_path)
        
        return image_path
        
    def analyse_card(self):
        image_path = fd.askopenfilename(title = "Choose an image", filetypes=[("Image files", ".jpg .png")])
        if image_path == None or image_path == "":
            return
        analyser = Analyser(image_path, self.debug)
        analyser.analyse()
        if analyser.result == None:
            return
        ref = self.recognize_card(analyser.result[0], analyser.result[1], image_path)
        if ref != "UNKNOWN":
            self.add_card(ref)
            
    def create_collection(self):
        collection_window = tk.Toplevel(self.master)
        collection_window.title("Create Collection")
        collection_window.geometry("800x600")
        collection_window.resizable(False, False)
        collection_window.iconbitmap(os.path.join("DATA", "IMAGES", "icone.ico"))
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
                if not os.path.isdir(os.path.join("DATA", "COLLECTIONS")):
                    os.mkdir(os.path.join("DATA", "COLLECTIONS"))
                with open(os.path.join("DATA", "COLLECTIONS", "COLLECTION_" + str(len(self.collections)) + ".txt"), "w") as f:
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


    def delete_collection(self):
        collection_window = tk.Toplevel(self.master)
        collection_window.title("Create Collection")
        collection_window.geometry("600x400")
        collection_window.resizable(False, False)
        collection_window.iconbitmap(os.path.join("DATA", "IMAGES", "icone.ico"))
        collection_window.config(bg="#1e1e1e")
        collection_window.focus_force()
        collection_window.grab_set()
        def delete_selected_collection():
            collection_name = collection_combobox.get()
            if collection_name:
                for i, col in enumerate(self.collections):
                    if col[0] == collection_name:
                        del self.collections[i]
                        # Delete the selected collection file or perform any other desired operation
                        print(f"Deleted Collection '{collection_name}'")
                        self.master.cards_menu.update_collections(self.collections)
                        collection_window.destroy()
                        os.remove(os.path.join("DATA", "COLLECTIONS", "COLLECTION_" + str(i+1) + ".txt"))
                        for file in os.listdir(os.path.join("DATA", "COLLECTIONS")):
                            if file[11:-4] > str(i+1):
                                os.rename(os.path.join("DATA", "COLLECTIONS", file), os.path.join("DATA", "COLLECTIONS", "COLLECTION_" + str(int(file[11:-4])-1) + ".txt"))
                                
                        tk.messagebox.showinfo("Collection Deleted", f"Collection '{collection_name}' deleted successfully!")
                        break
            else:
                tk.messagebox.showerror("Collection Selection Error", "Please select a collection to delete.")

        collection_label = tk.Label(collection_window, text="Select Collection to Delete:")
        collection_label.pack(pady=10)

        collection_combobox_frame = tk.Frame(collection_window)
        collection_combobox_frame.pack()

        collection_combobox = ttk.Combobox(collection_combobox_frame, values=[col[0] for col in self.collections])
        collection_combobox.pack(side=tk.LEFT)

        delete_button = tk.Button(collection_window, text="Delete Collection", command=delete_selected_collection)
        delete_button.pack(pady=10)



    
    
    def import_cards(self):
        collection = False
        file = fd.askopenfilename(title="Choose a file", filetypes=[("Text files", ".txt")])
        if not file:
            return
        with open(file, "r") as f:
            lines = f.readlines()
        for line in lines:
            if collection:
                self.collections[-1][1].append(line.strip("\n"))
            if line.startswith("nom:"):
                collection_name = line.strip("\n")[4:]

                if collection_name in [col[0] for col in self.collections]:
                    top = tk.Toplevel(self.master)
                    label = tk.Label(top, text="Collection name already exists. Please enter a different name.",
                                    font=("Matrix-Bold", 12))
                    entry = tk.Entry(top)

                    def change_name():
                        new_collection_name = entry.get()
                        if new_collection_name in [col[0] for col in self.collections]:
                            tk.messagebox.showerror("Collection Name Error",
                                                    "Collection name already exists. Please enter a different name.")
                        else:
                            nonlocal collection_name
                            collection_name = new_collection_name
                            top.destroy()

                    button = tk.Button(top, text="OK", command=change_name)
                    label.pack()
                    entry.pack(side=tk.LEFT, padx=30)
                    button.pack(side=tk.LEFT)
                    top.wait_window()  # Wait until the top window is closed

                self.collections.append([collection_name, []])
                collection = True
                continue
            ref = self.find_card(line.strip("\n"))
            self.add_card(ref)
        if collection:
            if not os.path.isdir(os.path.join("DATA", "COLLECTIONS")):
                os.mkdir(os.path.join("DATA", "COLLECTIONS"))
            with open(os.path.join("DATA", "COLLECTIONS", "COLLECTION_" + str(len(self.collections)) + ".txt"), "w") as f:
                f.write("nom:" + collection_name + "\n")
                for code in self.collections[-1][1]:
                    f.write(code + "\n")
        self.master.cards_menu.update_collections(self.collections)
        tk.messagebox.showinfo("Import Successful", "Cards imported successfully!")



            
    def export_cards(self):
        top = tk.Toplevel(self.master)
        
        def choose_location(collection):
            file = fd.asksaveasfile(title = "Choose a file", filetypes=[("Text files", ".txt")], defaultextension=".txt", initialfile=collection)
            if not file:
                return
            if collection != "global":
                file.write("nom:" + collection + "\n")
                for name, cards in self.collections:
                    if name == collection:
                        for card in cards:
                            file.write(card + "\n")
            else:
                for card in self.cards:
                    for i in range(card.quantity):
                        file.write(card.set_code + "\n")
            tk.messagebox.showinfo("Export Successful", "{} exported successfully!".format(collection))
            file.close()
            top.destroy()
        
        choose_collection_label = tk.Label(top, text="Choose Collection:")
        choose_collection_label.pack()
        
        choose_collection_combobox_frame = tk.Frame(top)
        choose_collection_combobox_frame.pack()
        
        choose_collection_combobox = ttk.Combobox(choose_collection_combobox_frame, values= ["global"] + [col[0] for col in self.collections])
        choose_collection_combobox.current(0)
        choose_collection_combobox.pack(side=tk.LEFT)
        
        choose_location_button = tk.Button(top, text="Choose Location", command=lambda: choose_location(choose_collection_combobox.get()))
        choose_location_button.pack()
    
        
    def add_card(self, ref):
        with open(self.cards_path, 'a') as f:
            f.write(ref)
            f.write("\n")
        self.master.cards_menu.update()
        self.collection_price()
        
    def delete_card(self, card, mode=0):
        with open(os.path.join("DATA", "CARDS", "cards.txt"), "r") as f:
            lines = f.readlines()
        with open(os.path.join("DATA", "CARDS", "cards.txt"), "w") as f:
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
        for card_ref in self.cards:
            if card_ref.set_code == card.set_code:
                card_ref.quantity_update(quantity)
                break
        self.collection_price()
    
    def reset(self):
        message = "Confirm reset ?"
        if tk.messagebox.askyesno("Reset", message):
            with open(os.path.join("DATA", "CARDS", "cards.txt"), "w") as f:
                f.write("")
            for filename in os.listdir(os.path.join("DATA", "COLLECTIONS")):
                os.remove(os.path.join("DATA", "COLLECTIONS", filename))
            self.collections = []
            self.cards = []
            self.cards_ref = []
            self.master.authentifier.update_filedata()
            self.master.cards_menu.update()
            self.collection_price()
    
    def set_debug(self, debug):
        if debug:
            tk.messagebox.showinfo("Debug Mode", "Debug mode activated. You can now see the evolution of card detection.")
        else:
            tk.messagebox.showinfo("Debug Mode", "Debug mode deactivated.")
        self.debug = debug