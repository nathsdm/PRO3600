import os
import tkinter as tk
from PIL import ImageTk, Image

class ScrollableImageList(tk.Canvas):
    def __init__(self, master=None, image_list=[], num_columns=3, mode = 0, **kwargs):
        super().__init__(master, **kwargs)

        self.mode = mode
        self.image_list = [image[0] for image in image_list]
        self.command_list = [image[1] for image in image_list]
        self.button_width = int(self.image_list[0].width/1.95)
        self.button_height = int(self.image_list[0].height/1.95)
        self.num_columns = num_columns

        # Create scrollbar
        self.scrollbar = tk.Scrollbar(self, orient="vertical", command=self.yview, width=20)
        self.scrollbar.pack(side="right", fill="y")

        # Connect scrollbar to canvas
        self.configure(yscrollcommand=self.scrollbar.set)

        # Add buttons with images to canvas
        self.add_buttons(self.mode)

        # Bind mousewheel event to scrollbar
        self.bind("<Configure>", self.configure_canvas)
        self.bind_all("<MouseWheel>", self.mousewheel)

    def add_buttons(self, mode):
        if mode == 0:
            idx = 0
            num_images = len(self.image_list)

            while idx < num_images:
                row = idx // self.num_columns
                col = idx % self.num_columns
                image = self.image_list[idx]
                image = image.resize((int(image.width/1.95), int(image.height/1.95)), Image.ANTIALIAS)
                image = ImageTk.PhotoImage(image)
                command = self.command_list[idx]
                button = tk.Button(self, image=image, width=self.button_width, height=self.button_height, command=command, border=0, cursor="hand2")
                button.image = image  # keep reference to prevent garbage collection
                self.create_window((col*self.button_width, row*self.button_height), window=button, anchor="nw")
                idx += 1
        elif mode == 1:
            row = 0
            num_images = len(self.image_list)

            while row < num_images:
                image = self.image_list[row]
                image = image.resize((int(image.width/4), int(image.height/4)), Image.ANTIALIAS)
                image = ImageTk.PhotoImage(image)
                command = self.command_list[row]
                button = tk.Button(self, image=image, width=image.width(), height=image.height(), command=command, border=0, cursor="hand2")
                button.image = image
                self.create_window((0, row*image.height()), window=button, anchor="nw")
                label = tk.Label(self, text=os.path.basename(self.image_list[row].filename).removesuffix(".jpg"), font=("Matrix-Bold", 20))
                label.bind("<Enter>", lambda event, label=label: label.configure(fg="blue", cursor="hand2", font=("Matrix-Bold", 20, "underline")))
                label.bind("<Leave>", lambda event, label=label: label.configure(fg="black", cursor="hand1", font=("Matrix-Bold", 20)))
                label.bind("<Button-1>", lambda event, command=command: command())
                self.create_window((image.width()+20, (row+0.5)*image.height()), window=label, anchor="nw", width = self.winfo_width() - image.width() - 40)
                row += 1
                
    def change_view(self, mode):
        self.mode = mode
        self.delete("all")
        self.scrollbar.destroy()
        # Create scrollbar
        self.scrollbar = tk.Scrollbar(self, orient="vertical", command=self.yview)
        self.scrollbar.pack(side="right", fill="y")

        # Connect scrollbar to canvas
        self.configure(yscrollcommand=self.scrollbar.set)

        # Add buttons with images to canvas
        self.add_buttons(self.mode)

        # Configure canvas
        self.configure_canvas(None)

        # Bind mousewheel event to scrollbar
        self.bind("<Configure>", self.configure_canvas)
        self.bind_all("<MouseWheel>", self.mousewheel)


    def configure_canvas(self, event):
        self.configure(scrollregion=self.bbox("all"))

    def mousewheel(self, event):
        self.yview_scroll(int(-1*(event.delta/120)), "units")