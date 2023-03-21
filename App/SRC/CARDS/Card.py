from slugify import slugify

class Card():
    def __init__(self, set_code, name, data, leng):
        self.set_code = set_code
        self.name = name
        self.data = data
        self.card_info = None
        self.leng = leng
        self.find_card()
        self.image = self.card_info.get("card_images")[0].get("image_url")
        self.image_name = slugify(self.name)
        
        
    def find_card(self):
        for k in self.data:
            if k.get("name") == self.name:
                self.card_info = k
                break
            
    def display_infos(self):
        for k in self.card_info.keys():
            print(k, " : ", self.card_info.get(k))