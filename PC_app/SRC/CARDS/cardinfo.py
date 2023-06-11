import requests
from tkinter import messagebox

url = "https://db.ygoprodeck.com/api/v7/cardinfo.php"
url_fr = "https://db.ygoprodeck.com/api/v7/cardinfo.php?language=fr"

try:
    response = requests.get(url)
    response_fr = requests.get(url_fr)

    if response.status_code == 200:
        info_en = response.json()
        info_fr = response_fr.json()
        # Data is now stored in the 'info_en' and 'info_fr' variables
        print("Data retrieved successfully.")
    else:
        messagebox.showerror("Error", f"Failed to retrieve data. Status code: {response.status_code}")

except requests.exceptions.RequestException as e:
    messagebox.showerror("Error", f"Failed to retrieve data. Error: {str(e)}")

except Exception as e:
    messagebox.showerror("Error", f"An error occurred: {str(e)}")