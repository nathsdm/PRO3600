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
                if ref != "UNKNOWN" and ref != "" and ref != " " and ref != '\n':
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
        for ref in self.names.keys():
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
            ref = ref.replace("FR", "EN")
        else:
            self.leng = "EN"
            self.info = self.info_en
        print(ref)
        if self.leng == "EN":
            card_name = [k for k in self.names.keys() if ref in self.names.get(k)][0]
        else:
            card_name = [k for k in self.names.keys() if ref in self.names.get(k)][1]
        self.cards.append(Card(self, ref, card_name, self.info, self.leng))
        self.download_card(self.cards[-1])
    
    def recognize_card(self, ref, name=None, image_path=None):
        
        probas = difflib.get_close_matches(ref, self.refs.keys(), n=1, cutoff=0.6)
        if name != None:
            print('name:')
            print(name)
            print('closest:')
            closest = difflib.get_close_matches(name, self.cards_names.keys(), n=1, cutoff=0.5)[0]
            closest = self.cards_names.get(closest)
            print(closest)
            print(self.names.get(closest))
        
        if len(probas) == 0:
            probas = [""]
        finding = self.refs.get(probas[0], None)
            
        if finding in self.names.get(closest):
            print("Nice")
        else:
            print("Not nice")
            finding = self.names.get(closest)[0]
        
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
    
    def get_buttons(self, select="All", sort="Name", race="All"):
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
                
        for card in self.cards:
            if select in card.type or select == "All":
                if race in card.race or race == "All":
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
            
    
    
        
    def add_card(self, card):
        with open(self.cards_path, 'a') as f:
            f.write(card)
            f.write("\n")
        self.master.cards_menu.update()
        
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
        
    def update_quantity(self, card, quantity):
        count = 0
        for card_ref in self.cards:
            if card_ref.set_code == card.set_code:
                card_ref.quantity_update(quantity)
                break
    
    def reset(self):
        message = "Êtes-vous sûr de vouloir réinitialiser votre collection ?"
        if tk.messagebox.askyesno("Réinitialiser", message):
            with open(os.path.join("App", "DATA", "CARDS", "cards.txt"), "w") as f:
                f.write("")
            self.cards = []
            self.cards_ref = []
            self.master.authentifier.update_filedata()
            self.master.cards_menu.update()