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
        self.cards_ref = []
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
        self.start()

    def setup_cards(self):
        """
        Defines the cards used in the cards menu.
        """
        if os.path.isdir(os.path.join("App", "DATA", "CARDS")):
            print("Cards folder found")
        else:
            os.makedirs(name=os.path.join("App", "DATA", "CARDS"))
        if os.path.isfile(self.cards_path):
            for ref in open(file="App\DATA\CARDS\cards.txt", mode="r"):
                if ref != "UNKNOWN":
                    self.cards_ref.append(ref.rstrip('\n'))
        else:
            print("No cards found")
    
    def start(self):
        self.setup_cards()
        self.setup_names()
        self.setup_refs()
        for k in self.cards_ref:
            self.recognize_card(k)
        
    def setup_refs(self):
        for ref in self.names.keys():
            self.refs[''.join([char for char in ref if char.isupper() or char.isdigit()]).replace("EN" or "FR", "")] = ref
    
    def setup_names(self):
        for k in self.info_en:
            if k.get("card_sets") != None:
                self.names[k.get("card_sets")[0]["set_code"]] = [k.get("name")]
            else:
                pass
            
        for k in self.info_fr:
            if k.get("card_sets") != None:
                if k.get("card_sets")[0]["set_code"] in self.names.keys():
                    self.names[k.get("card_sets")[0]["set_code"]] += [k.get("name")]
                else:
                    self.names[k.get("card_sets")[0]["set_code"]] = [k.get("name")]
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
        finding = self.refs.get(probas[0])
        print(finding)
        if len(finding) > 0:
            card_name = self.names.get(finding)[0 if self.leng == "EN" else 1]
            self.cards.append(Card(finding, card_name, self.info, self.leng))
            print("Félicitation, vous avez un {} !".format(card_name))
            return finding if self.leng == "EN" else finding.replace("EN", "FR")
        else:
            print("Je n'arrive pas à reconnaître la carte...")
            return "UNKNOWN"
    
    def get_buttons(self, text):
        def display_card(card):
            self.master.son.card = card
            self.master.son.change_menu(self.master.son.card_desc_window)
        buttons = []
        for card in self.cards:
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
        
        if os.path.isfile(image_path):
            print("Image found")
            return image_path
        else:
            urlretrieve(card.image, image_path)
            return image_path
        
    def analyse_card(self):
        image_path = fd.askopenfilename(title = "Choose an image", filetypes=[("Image files", ".jpg .png")])
        analyser = Analyser(image_path)
        analyser.analyse()
        ref = self.recognize_card(analyser.result)
        self.add_card(ref)
        
    def add_card(self, card):
        with open(self.cards_path, 'a') as f:
            f.write('\n')
            f.write(card)
        self.master.update()