import os
import tkinter as tk
from PIL import ImageTk, Image

class ScrollableImageList(tk.Canvas):
    def __init__(self, master=None, image_list=[], num_columns=3, **kwargs):
        super().__init__(master, **kwargs)

        self.image_list = image_list
        self.button_width = image_list[0].width()
        self.button_height = image_list[0].height()
        self.num_columns = num_columns

        # Create scrollbar
        self.scrollbar = tk.Scrollbar(self, orient="vertical", command=self.yview)
        self.scrollbar.pack(side="right", fill="y")

        # Connect scrollbar to canvas
        self.configure(yscrollcommand=self.scrollbar.set)

        # Add buttons with images to canvas
        self.add_buttons()

        # Bind mousewheel event to scrollbar
        self.bind("<Configure>", self.configure_canvas)
        self.bind_all("<MouseWheel>", self.mousewheel)

    def add_buttons(self):
        idx = 0
        num_images = len(self.image_list)

        while idx < num_images:
            row = idx // self.num_columns
            col = idx % self.num_columns
            image = self.image_list[idx]
            button = tk.Button(self, image=image, width=self.button_width, height=self.button_height)
            button.image = image  # keep reference to prevent garbage collection
            self.create_window((col*self.button_width, row*self.button_height), window=button, anchor="nw")
            idx += 1

    def configure_canvas(self, event):
        self.configure(scrollregion=self.bbox("all"))

    def mousewheel(self, event):
        self.yview_scroll(int(-1*(event.delta/120)), "units")