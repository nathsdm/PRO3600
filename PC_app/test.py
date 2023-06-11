import requests

url = "https://db.ygoprodeck.com/api/v7/cardinfo.php"

response = requests.get(url)

if response.status_code == 200:
    data = response.json()
    # Data is now stored in the 'data' variable
    print("Data retrieved successfully.")
else:
    print("Failed to retrieve data. Status code:", response.status_code)

print(data)