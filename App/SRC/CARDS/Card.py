from slugify import slugify

class Card():
    def __init__(self, master, set_code, name, data, leng, quantity=1):
        self.master = master
        self.name = name
        self.data = data
        self.sets = []
        self.quantity = quantity
        self.card_info = None
        self.atk = None
        self.defense = None
        self.level = None
        self.leng = leng
        self.set_code = set_code if self.leng == "EN" else set_code.replace("EN", "FR")
        self.find_card()
        self.image = self.card_info.get("card_images")[0].get("image_url")
        self.image_name = slugify(self.name)
        self.infos = []
        self.setup_infos()
        
        
    def find_card(self):
        for k in self.data:
            if k.get("name") == self.name:
                self.card_info = k
                break
    
    def setup_infos(self):
        for info in self.card_info.keys():
            if info not in ["card_images", "card_sets", "card_prices"] :
                self.infos.append(str(info) + " : " + str(self.card_info.get(info)))
                match info:
                    case "type":
                        self.type = self.card_info.get(info)
                    case "atk":
                        self.atk = self.card_info.get(info)
                    case "def":
                        self.defense = self.card_info.get(info)
                    case "race":
                        self.race = self.card_info.get(info)
                    case "id":
                        self.id = self.card_info.get(info)
                    case "desc":
                        self.desc = self.card_info.get(info)
                    case "level":
                        self.level = self.card_info.get(info)
                    case "attribute":
                        self.attribute = self.card_info.get(info)
            elif info == "card_sets":
                self.infos.append("Sets")
                for card_set in self.card_info.get(info):
                    self.sets.append(card_set.get("set_code"))
                    set_info = ""
                    for k in card_set.keys():
                        set_info += str(k) + " : " + str(card_set.get(k)) + "\n"
                    self.infos.append(set_info)
            
    def display_infos(self):
        for k in self.card_info.keys():
            print(k, " : ", self.card_info.get(k))
            
    def add_quantity(self, quantity):
        self.quantity += quantity
    
    def quantity_update(self, quantity):
        self.quantity = quantity
        if self.quantity == 0:
            self.master.delete_card(self, mode=0)
            return
        self.master.delete_card(self, mode=1)