import os
import tkinter as tk
from PIL import ImageTk, Image

class ScrollableImageList(tk.Frame):
    def __init__(self, master=None, image_list=[], button_width=100, button_height=100, num_columns=3):
        super().__init__(master)

        self.image_list = image_list
        self.button_width = button_width
        self.button_height = button_height
        self.num_columns = num_columns

        # Create canvas to hold buttons
        self.canvas = tk.Canvas(self)
        self.canvas.pack(side="left", fill="both", expand=True)

        # Create scrollbar
        self.scrollbar = tk.Scrollbar(self, orient="vertical", command=self.canvas.yview)
        self.scrollbar.pack(side="right", fill="y")

        # Connect scrollbar to canvas
        self.canvas.configure(yscrollcommand=self.scrollbar.set)

        # Configure canvas weights to expand with window
        self.rowconfigure(0, weight=1)
        self.columnconfigure(0, weight=1)

        # Add buttons with images to canvas
        self.add_buttons()

        # Bind mousewheel event to scrollbar
        self.canvas.bind("<Configure>", self.configure_canvas)
        self.canvas.bind_all("<MouseWheel>", self.mousewheel)

    def add_buttons(self):
        idx = 0
        num_images = len(self.image_list)

        while idx < num_images:
            row = idx // self.num_columns
            col = idx % self.num_columns
            image = self.image_list[idx]
            button = tk.Button(self.canvas, image=image, width=self.button_width, height=self.button_height)
            button.image = image  # keep reference to prevent garbage collection
            self.canvas.create_window((col*self.button_width, row*self.button_height), window=button, anchor="nw")
            idx += 1

    def configure_canvas(self, event):
        self.canvas.configure(scrollregion=self.canvas.bbox("all"))

    def mousewheel(self, event):
        self.canvas.yview_scroll(int(-1*(event.delta/120)), "units")


# Create root window
root = tk.Tk()

# Create a list of image paths from a directory
image_dir = "App/DATA/CARDS/IMAGES"
image_paths = [os.path.join(image_dir, f) for f in os.listdir(image_dir) if f.endswith(".jpg")]

# Load images into PIL Image objects
images = [Image.open(path) for path in image_paths]

# Resize images to fit buttons
resized_images = [img.resize((100, 100), Image.ANTIALIAS) for img in images]

# Convert PIL Images to Tkinter PhotoImages
tk_images = [ImageTk.PhotoImage(img) for img in resized_images]

# Create scrollable image list
image_list = ScrollableImageList(root, tk_images, button_width=100, button_height=100, num_columns=5)
image_list.pack(side="top", fill="both", expand=True)

# Start main loop
root.mainloop()
