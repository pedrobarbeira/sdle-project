import tkinter as tk
from tkinter import ttk

class ItemFrame(tk.Frame):
    def __init__(self, parent, id, name, item_type, initial_value):
        super().__init__(parent)

        self.id = id
        self.name = tk.StringVar(value=name)
        self.item_type = tk.StringVar(value=item_type)
        self.value = tk.StringVar(value=initial_value)

        self.name_label = ttk.Label(self, text="Name:")
        self.item_type_label = ttk.Label(self, text="Type:")

        self.name_entry = ttk.Entry(self, textvariable=self.name)
        self.item_type_combobox = ttk.Combobox(self, values=["boolean", "numeric"], textvariable=self.item_type)

        self.value_frame = tk.Frame(self)

        self.value_entry = None
        self.value_entry_label = None

        self.item_type_combobox.bind("<<ComboboxSelected>>", self.update_value_field)
        self.update_value_field()

    def get_values(self):
        
        return {"id": self.id, "name": self.name.get(), "type": self.item_type.get(), "quantity": self.value.get()}

    def update_value_field(self, event=None):
        if self.value_entry:
            self.value_entry.grid_forget()
            self.value_entry_label.grid_forget()

        self.name_label.grid(row=0, column=0, padx=5, pady=5, sticky="w")
        self.name_entry.grid(row=0, column=1, padx=5, pady=5, sticky="w")
        self.item_type_label.grid(row=0, column=2, padx=5, pady=5, sticky="w")
        self.item_type_combobox.grid(row=0, column=3, padx=5, pady=5, sticky="w")

        if self.item_type.get() == "boolean":
            self.value_entry_label = ttk.Label(self.value_frame, text="Checked:")
            self.value_entry = ttk.Checkbutton(self.value_frame, variable=self.value, onvalue="1", offvalue="0", padding=(0, 0, 107, 0))
        elif self.item_type.get() == "numeric":
            self.value_entry_label = ttk.Label(self.value_frame, text="Quantity:")
            self.value_entry = ttk.Entry(self.value_frame, textvariable=self.value)

        if self.value_entry_label and self.value_entry:
            self.value_entry_label.grid(row=0, column=0, padx=5, pady=5, sticky="w")
            self.value_entry.grid(row=0, column=1, padx=5, pady=5, sticky="w")

        self.value_frame.grid(row=0, column=4, padx=5, pady=5, sticky="w")
        self.columnconfigure(4, weight=1)  # Configure the column to expand and fill available width
