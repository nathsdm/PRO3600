from tkinter.filedialog import askopenfilenames

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
from tkinter import filedialog as fd


class CardsManager:
    def __init__(self, master=None):
        self.master = master
        self.cards = []
        self.names = {}
        self.cards_path = os.path.join("App", "DATA", "CARDS", "cards.txt")
        self.info_fr = cardinfo.info_fr["data"]
        self.info_en = cardinfo.info_en["data"]
        self.refs = {}
        path = os.path.join("App", "DATA", "CARDS", "IMAGES")        
        if os.path.isdir(path):
            print("Images folder found")
        else:
            os.makedirs(name=path)
        self.setup_names()
        self.setup_refs()
        self.start()

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
                if ref != "UNKNOWN" and ref != "":
                    self.cards_ref.append(ref.rstrip('\n'))
        else:
            print("No cards found")
    
    def start(self):
        self.setup_cards()
        for k in self.cards_ref:
            self.recognize_card(k)
        
    def setup_refs(self):
        for ref in self.names.keys():
            self.refs[''.join([char for char in ref if char.isupper() or char.isdigit()]).replace("EN" or "FR", "")] = ref
    
    def setup_names(self):
        for k in self.info_en:
            if k.get("card_sets") != None:
                for num in range(len(k.get("card_sets"))):
                    self.names[k.get("card_sets")[num]["set_code"]] = [k.get("name")]
            else:
                pass
            
        for k in self.info_fr:
            if k.get("card_sets") != None:
                for num in range(len(k.get("card_sets"))):
                    if k.get("card_sets")[num]["set_code"] in self.names.keys():
                        self.names[k.get("card_sets")[num]["set_code"]] += [k.get("name")]
                    else:
                        self.names[k.get("card_sets")[num]["set_code"]] = [k.get("name")]
            else:
                pass
    
    def recognize_card(self, text):
        text = ''.join([char for char in text if char.isupper() or char.isdigit()])
        if "FR" in text:
            self.leng = "FR"
            self.info = self.info_fr
            text = text.replace("FR", "")
        else:
            self.leng = "EN"
            self.info = self.info_en  
            text = text.replace("EN", "")        
        probas = difflib.get_close_matches(text, self.refs.keys(), cutoff=0.4)
        if len(probas) > 0:
            finding = self.refs.get(probas[0])
            card_name = self.names.get(finding)[0 if self.leng == "EN" else 1]
            self.cards.append(Card(finding, card_name, self.info, self.leng))
            return finding if self.leng == "EN" else finding.replace("EN", "FR")
        else:
            tk.messagebox.showerror("Erreur", "Je n'arrive pas à reconnaître la carte...")
            print("Je n'arrive pas à reconnaître la carte...")
            return "UNKNOWN"
    
    def get_buttons(self, text, select, sort):
        def display_card(card):
            self.master.son.card = card
            self.master.son.change_menu(self.master.son.card_desc_window)
        buttons = []
        match sort:
            case "Name":
                self.cards.sort(key=lambda x: x.name)
            case "Atk":
                self.cards.sort(key=lambda x: x.atk if x.atk != None else 0, reverse=True)
            case "Def":
                self.cards.sort(key=lambda x: x.defense if x.defense != None else 0, reverse=True)
        
        for card in self.cards:
            if select in card.type or select == "All":
                image = self.download_card(card)
                image = ImageTk.PhotoImage(Image.open(image))
                image = image._PhotoImage__photo.subsample(2)
                buttons.append(tk.Button(text, image=image, command=partial(display_card, card)))
                buttons[-1].image = image
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
        ref = self.recognize_card(analyser.result)
        self.add_card(ref)
        
    def add_card(self, card):
        with open(self.cards_path, 'a') as f:
            f.write('\n')
            f.write(card)
        self.master.son.cards_menu.update()
        
    def delete_card(self, card):
        with open(os.path.join("App", "DATA", "CARDS", "cards.txt"), "r") as f:
            lines = f.readlines()
        with open(os.path.join("App", "DATA", "CARDS", "cards.txt"), "w") as f:
            count = 0
            for line in lines:
                if line.strip("\n") == card.set_code and count == 0:
                    count += 1
                else:
                    f.write(line)
        for k in self.cards:
            if k.set_code == card.set_code:
                self.cards.remove(k)
                break
        self.master.son.cards_menu.update()