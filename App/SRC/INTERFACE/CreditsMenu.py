"""
CreditsMenu.py

Configure the BT credits page.
"""

#-------------------------------------------------------------------#

import tkinter as tk

#-------------------------------------------------------------------#

class CreditsMenu(tk.Frame):
    def __init__(self, master=None):
        super().__init__(master)
        self.master = master
        self.configure(bg="orange")
        self.master.master.loggers.log.info("(Credits menu)")
        
        self.lorem_ipsum = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
            Sed iaculis ut elit ut maximus. Phasellus sem enim, tempor
            a semper vel, efficitur in tellus. Fusce sed leo non est 
            egestas efficitur sit amet eget nisi. Integer sagittis sem
            vel gravida finibus. Curabitur non justo cursus, rutrum quam
            sit amet, interdum nisi. Aliquam tincidunt aliquam ornare. 
            Etiam non erat ac magna semper condimentum ac ac sapien. 
            Vestibulum vestibulum interdum convallis.

            Sed ligula ipsum, suscipit id sollicitudin vel, molestie quis
            tortor. Ut facilisis augue sed malesuada porta. Suspendisse et
            condimentum metus. Donec facilisis nunc sed fermentum fringilla.
            Fusce tincidunt purus non leo aliquet accumsan ac sed lectus.
            Praesent arcu metus, gravida eu lobortis pharetra, auctor ac massa.
            Sed et mi non metus consectetur tempor. Vestibulum aliquam elit vel
            lectus efficitur, quis auctor est convallis. Vivamus odio tortor,
            auctor eget lacus nec, vestibulum tincidunt libero.
        """
        
        self.lorem_label = tk.Label(self, text=self.lorem_ipsum, bg="orange")
        self.lorem_label.pack()
        
        self.back_button = tk.Button(self, text="Back", command=lambda: master.change_menu(master.main_menu))
        self.back_button.pack()