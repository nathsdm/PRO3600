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
import cv2
import numpy as np
import jellyfish


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
                self.recognize_card(k)
            
            index+=1
        
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
    
    def recognize_card(self, text, image_path=None):
        text = ''.join([char for char in text if char.isupper() or char.isdigit()])
        if "FR" in text:
            self.leng = "FR"
            self.info = self.info_fr
            text = text.replace("FR", "")
        else:
            self.leng = "EN"
            self.info = self.info_en  
            text = text.replace("EN", "")
        
        def get_closest_match(word, candidates):
            # Compute the Damerau-Levenshtein and Jaro-Winkler distances for the candidates
            dl_distances = [jellyfish.damerau_levenshtein_distance(word, candidate) for candidate in candidates]
            jw_distances = [jellyfish.jaro_winkler(word, candidate) for candidate in candidates]
            
            # Combine the distances into a single score using a weighted sum
            scores = [0.5 * dl + 0.5 * jw for dl, jw in zip(dl_distances, jw_distances)]
            
            # Return the match with the smallest score
            min_score_index = scores.index(min(scores))
            return list(candidates)[min_score_index]
        probas = [get_closest_match(text, self.refs.keys())]
        
        if len(probas) > 0:
            finding = self.refs.get(probas[0])
            card_name = self.names.get(finding)[0 if self.leng == "EN" else 1]
            for card in self.cards:
                if card.name == card_name:
                    card.add_quantity(1)
                    return finding if self.leng == "EN" else finding.replace("EN", "FR")
            self.cards.append(Card(self, finding, card_name, self.info, self.leng))
            self.download_card(self.cards[-1])
            # Check if the card is correctly recognized
            def mse(img1, img2):
                h, w, z = img1.shape
                img2 = cv2.resize(img2, (w, h))
                img1 = cv2.cvtColor(img1, cv2.COLOR_BGR2GRAY)
                img2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)
                diff = cv2.subtract(img1, img2)
                err = np.sum(diff**2)
                mse = err/(float(h*w))
                return mse
            if image_path != None:
                print(mse(cv2.imread(self.cards[-1].image_path), cv2.imread(image_path)))
                if mse(cv2.imread(self.cards[-1].image_path), cv2.imread(image_path)) > 55:
                    tk.messagebox.showerror("Erreur", "Je n'arrive pas à reconnaître la carte...")
                    return "UNKNOWN"
                
            return finding if self.leng == "EN" else finding.replace("EN", "FR")
        else:
            tk.messagebox.showerror("Erreur", "Je n'arrive pas à reconnaître la carte...")
            return "UNKNOWN"
    
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
                    image = self.download_card(card)
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
        ref = self.recognize_card(analyser.result, image_path)
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
            self.master.cards_menu.update()