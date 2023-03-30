from slugify import slugify

class Card():
    def __init__(self, set_code, name, data, leng):
        self.name = name
        self.data = data
        self.card_info = None
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
            if info != "card_images" and info != "card_prices" and info != "card_sets" :
                self.infos.append(str(info) + " : " + str(self.card_info.get(info)))
            elif info == "card_sets":
                for card_set in self.card_info.get(info):
                    set_info = ""
                    for k in card_set.keys():
                        set_info += str(k) + " : " + str(card_set.get(k)) + "\n"
                    self.infos.append(set_info)
            
    def display_infos(self):
        for k in self.card_info.keys():
            print(k, " : ", self.card_info.get(k))