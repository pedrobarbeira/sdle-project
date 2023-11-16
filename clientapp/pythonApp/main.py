import tkinter as tk
from tkinter import ttk
from tkinter import messagebox
from tkinter import simpledialog

import json
import helpers

from components.scrollableFrame import ScrollableFrame
from components.itemFrame import ItemFrame

import settings
settings.init()

class tkinterApp(tk.Tk):
    def __init__(self, *args, **kwargs):
        tk.Tk.__init__(self, *args, **kwargs)
        self.geometry("800x600")
        self.title("SDLE")
        self.configure(bg="#e6e6e6")
        self.minsize(width=450, height=300)

        self.frames = {}
        
        login_page = LoginPage(self, self)
        self.frames[LoginPage] = login_page

        shopping_lists_page = ShoppingListsPage(self, self)
        self.frames[ShoppingListsPage] = shopping_lists_page

        items_page = ItemsPage(self, self)
        self.frames[ItemsPage] = items_page

        self.loadUserToken()


    def logout(self):
        self.show_frame(LoginPage)

        settings.userToken = ""
        settings.userName = ""
        settings.shoppingLists = ""

        helpers.update_userToken("")

    def loadUserToken(self):
        settings.userName, settings.userToken = helpers.load_userToken()

        if(settings.userName != ""):

            # ToDo: deal with joinning stored data with server data
            # local stored version of remote shopping lists may differ from server versions

            settings.shoppingLists = helpers.read_local_data(settings.userName)

            #removing remote shopping lists stored locally for safety
            settings.shoppingLists = [elem for elem in settings.shoppingLists if elem['origin'] != "remote"]

            status, body = helpers.get_remote_shoppingList(settings.userToken)

            if(status != 200):
                print("GET api/shoppinglist error")
                exit(1)

            for l in body:
                l['origin'] = "remote"
                settings.shoppingLists.append(l)

            self.frames[ShoppingListsPage].update_shopping_lists()

            self.show_frame(ShoppingListsPage)
        
        else:
            settings.userToken = ""
            self.show_frame(LoginPage)

    def show_frame(self, cont):
        
        for frame in self.frames.values():
            frame.pack_forget()

        frame = self.frames[cont]
        if cont == LoginPage:
            frame.place(in_=self, anchor="center", relx=0.5, rely=0.5)
        elif cont == ShoppingListsPage or cont == ItemsPage:
            frame.pack(fill="both", expand=True) 
        else:
            app.destroy()

        frame.tkraise()

class ShoppingListsPage(tk.Frame):
    def __init__(self, parent, controller):
        self.controller = controller

        tk.Frame.__init__(self, parent, padx=10, pady=10, bg=parent["bg"])  # Increase the vertical padding

        # Header
        header_frame = tk.Frame(self, bg="#8c8c8c", pady=10)
        header_frame.pack(fill="x")

        # Label on the left side of the header
        left_label = tk.Label(header_frame, text="SDLE", bg=header_frame['bg'])
        left_label.pack(side="left", padx=10)

        # Username label and logout button on the right side of the header
        logout_button = tk.Button(header_frame, text="Logout", command=self.controller.logout)
        logout_button.pack(side="right", padx=10)

        username_label = tk.Label(header_frame, text="Username")
        username_label.pack(side="right", padx=10)

        header_frame2 = tk.Frame(self, pady=10)
        header_frame2.pack(fill="x")
        header_frame2.grid_rowconfigure(0, weight=1)
        header_frame2.grid_columnconfigure(0, weight=1)

        # Shopping Lists Label
        shopping_lists_label = tk.Label(header_frame2, text="Shopping Lists", font=("Arial", 10))
        shopping_lists_label.grid(row=0, column=0, columnspan=2, sticky="nsew")

        # New Shopping List button
        new_list_button = tk.Button(header_frame2, text="New Shopping List", command=self.create_new_list)
        new_list_button.grid(row=0, column=1, sticky="e", padx=10)

        new_list_button = tk.Button(header_frame2, text="Import Shopping List", command=self.import_shopping_list)
        new_list_button.grid(row=0, column=2, sticky="e", padx=10)
    
    def label_click(self, event, id):
        self.controller.frames[ItemsPage].update_items(id)
        self.controller.show_frame(ItemsPage)

    def update_shopping_lists(self):
        if hasattr(self, "scrollable_frame"):
            self.scrollable_frame.destroy()

        self.scrollable_frame = ScrollableFrame(self, bg="#f0f0f0")
        self.scrollable_frame.pack(fill="both", expand=True)

        if(settings.shoppingLists == ""): return
        
        for elem in settings.shoppingLists:
            frame = tk.Frame(self.scrollable_frame.frame, pady=2)
            frame.pack(fill="x", padx=10, pady=4)
            frame.configure(bg="#e6e6e6")
            frame.grid_rowconfigure(0, weight=1)
            frame.grid_columnconfigure(1, weight=1)  # Adjust the column weight for centering

            origin = tk.Label(frame, text=f"{elem['origin']}")
            origin.grid(row=0, column=0, padx=5, sticky="w")

            label = tk.Label(frame, text=f"{elem['name']}")
            label.configure(background="#e6e6e6")
            label.grid(row=0, column=1, padx=20, sticky="nswe")  # Centered

            label.bind("<Button-1>", lambda event, id=elem['id']: self.label_click(event, id))

            deleteBtn = tk.Button(frame, text="Delete", command=lambda id=elem['id']: self.delete_shopping_list(id))
            deleteBtn.grid(row=0, column=2, padx=10, sticky="e")

            shareBtn = tk.Button(frame, text="Share", command=lambda id=elem['id']: self.share_shoppinglist(id))
            shareBtn.grid(row=0, column=3, padx=10, sticky="e")

    def share_shoppinglist(self, id):
        result = simpledialog.askstring("Input", "Enter the username to share the shopping list:")
        if result is not None:
            for index, elem in enumerate(settings.shoppingLists):
                if elem['id'] == id:
                    shoppingList = elem
                    shoppingListIndex = index
                    break

            if(shoppingList['origin'] == 'local'):
                status, body = helpers.post_remote_shoppingList(settings.userToken, shoppingList)

                if(status != 200):
                    print("POST api/shoppinglist error")
                    print(body)
                    return
                
                body['origin'] = "remote"
                settings.shoppingLists[shoppingListIndex] = body
                shoppingList = body

                print("new id ", settings.shoppingLists[shoppingListIndex]['id'])
                self.update_shopping_lists()

            status, body = helpers.post_share_shoppingList(settings.userToken, shoppingList, result)

            if(status != 200):
                print("POST api/shoppinglist error")
                print(body)
                return

            body['origin'] = 'remote'
            settings.shoppingLists[shoppingListIndex] = body

    def delete_shopping_list(self, id): 
        for index, elem in enumerate(settings.shoppingLists):
            if elem['id'] == id:
                shoppingList = settings.shoppingLists.pop(index)
                break
        
        if(shoppingList['origin'] == 'remote'):
            status, body = helpers.del_remote_shoppingList(settings.userToken, shoppingList)

            if(status != 200):
                print("DEL api/shoppinglist error")
                settings.shoppingLists += [shoppingList]
                print(body)
                return
        
        helpers.update_local_data(settings.userName, settings.shoppingLists)

        self.update_shopping_lists()

    def create_new_list(self):
        ids = [elem['id'] for elem in settings.shoppingLists if elem['origin'] == "local"]
        newId = 1 + max(ids) if ids else 0

        settings.shoppingLists.append({'id': newId, 'name':"New List", 'items':[], 'origin':"local"})

        helpers.update_local_data(settings.userName, settings.shoppingLists)

        self.update_shopping_lists()

    def import_shopping_list(self):
        result = simpledialog.askstring("Input", "Enter the shopping list sharing code:")
    
        if result is not None:
            # Ask the server for data
            # update shoppingLists global var
            # update json

            helpers.update_local_data(settings.userName, settings.shoppingLists)
            
            
            self.update_shopping_lists()

class LoginPage(tk.Frame):
    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        self.configure(bg="#8c8c8c")

        # Add row and column configuration for expansion
        self.grid_rowconfigure(0, weight=1)
        self.grid_columnconfigure(0, weight=1)

        # Create a label for the login page
        label = ttk.Label(self, text="Login Page", font=("Arial", 35), foreground="white", background=self["bg"])
        label.grid(row=0, column=0, columnspan=2, padx=10, pady=10)

        # Username label and entry
        username_label = ttk.Label(self, text="Username:", foreground="white", background=self["bg"], font=("Arial", 10))
        username_label.grid(row=1, column=0, padx=10, pady=10)
        username_entry = ttk.Entry(self)
        username_entry.grid(row=1, column=1, padx=10, pady=10)

        # Password label and entry
        password_label = ttk.Label(self, text="Password:", foreground="white", background=self["bg"], font=("Arial", 10))
        password_label.grid(row=2, column=0, padx=10, pady=10)
        password_entry = ttk.Entry(self, show="*")
        password_entry.grid(row=2, column=1, padx=10, pady=10)

        # Login button
        login_button = ttk.Button(self, text="Login", command=lambda: self.validate_login(controller, username_entry, password_entry))
        login_button.grid(row=3, column=0, columnspan=2, padx=10, pady=10)


    def validate_login(self, controller, username_entry, password_entry):
        username = username_entry.get()
        password = password_entry.get()

        status, body = helpers.login(username, password)

        if(status != 200):
            print(body)
            messagebox.showerror("Login Failed", "Invalid Username or Password")
        else:
            try:
                settings.userToken = body['token']
            except:
                messagebox.showerror("Login Failed", "Bad response format from server")
                return
            
            settings.userName = username
            
            username_entry.delete(0, tk.END)
            password_entry.delete(0, tk.END)

            # ToDo: deal with joinning local stored data with remote data

            #settings.shoppingLists = helpers.read_local_data(settings.userName)
            settings.shoppingLists = []

            status, body = helpers.get_remote_shoppingList(settings.userToken)

            if(status != 200):
                print(body)
                print("GET api/shoppinglist error")
                exit(1)

            for l in body:
                l['origin'] = "remote"

            settings.shoppingLists = body

            controller.frames[ShoppingListsPage].update_shopping_lists()
            controller.show_frame(ShoppingListsPage)

class ItemsPage(tk.Frame):
    def __init__(self, parent, controller):
        self.controller = controller

        tk.Frame.__init__(self, parent, padx=10, pady=10, bg=parent["bg"])  # Increase the vertical padding

        # Header
        header_frame = tk.Frame(self, bg="#8c8c8c", pady=10)
        header_frame.pack(fill="x")

        # Label on the left side of the header
        left_label = tk.Label(header_frame, text="SDLE", bg=header_frame['bg'])
        left_label.pack(side="left", padx=10)

        # Username label and logout button on the right side of the header
        logout_button = tk.Button(header_frame, text="Logout", command=self.controller.logout)
        logout_button.pack(side="right", padx=10)

        username_label = tk.Label(header_frame, text="Username")
        username_label.pack(side="right", padx=10)

        header_frame2 = tk.Frame(self, pady=10)
        header_frame2.pack(fill="x")
        header_frame2.grid_rowconfigure(0, weight=1)
        header_frame2.grid_columnconfigure(1, weight=1)
        
        self.export_button = tk.Button(header_frame2, text="Export", command=self.export)
        self.export_button.grid(row=0, column=0, sticky="e", padx=10)
        
        shopping_lists_label = tk.Label(header_frame2, text="Shopping Items", font=("Arial", 10))
        shopping_lists_label.grid(row=0, column=1, columnspan=2, sticky="nsew")

        new_item_button = tk.Button(header_frame2, text="New Item", command=self.create_new_item)
        new_item_button.grid(row=0, column=1, sticky="e", padx=10)

        return_button = tk.Button(header_frame2, text="Return", command=self.return_shopping_lists_page)
        return_button.grid(row=0, column=2, sticky="e", padx=10)

        save_button = tk.Button(header_frame2, text="Save", command=self.save)
        save_button.grid(row=0, column=3, sticky="e", padx=10)

        # Create a frame for the list and add a scrollbar
        self.scrollable_frame = ScrollableFrame(self, bg=header_frame2['bg'])
        self.scrollable_frame.pack(fill="both", expand=True)

    def export(self):
        self.export_button.grid_remove()
        self.data['origin'] = "remote"

        self.save()

    def update_items(self, id):
        if hasattr(self, "scrollable_frame"):
            self.scrollable_frame.destroy()

        self.scrollable_frame = ScrollableFrame(self, bg="#f0f0f0")
        self.scrollable_frame.pack(fill="both", expand=True)

        self.data = None
        for element in settings.shoppingLists:
            if element['id'] == id:
                self.data = element
                break
        
        if(self.data == None):
            print("id not found")
            return

        if(self.data['origin'] == 'local'):
            self.export_button.grid()
        else:
            self.export_button.grid_remove()

        items = [(elem['id'], elem['name'], elem['type'], elem['quantity']) for elem in self.data['items']]
        for (idd, name, type, quantity) in items:
            frame = tk.Frame(self.scrollable_frame.frame)
            frame.pack(fill="x", padx=10, pady=10)

            itemframe = ItemFrame(frame, idd, name, type, quantity)
            itemframe.pack()

    def save(self):
        self.data['items'] = []
        for frame in self.scrollable_frame.winfo_children()[0].winfo_children()[0].winfo_children():
            if isinstance(frame, tk.Frame):
                for itemFrame in frame.winfo_children():
                    if isinstance(itemFrame, ItemFrame):
                        self.data['items'].append(itemFrame.get_values())
       
        if(self.data['origin'] == "remote"):
            status, body = helpers.post_remote_shoppingList(settings.userToken, self.data)

            if(status != 200):
                print("POST api/shoppinglist error")
                print(body)
            else:
                for index, elem in enumerate(settings.shoppingLists):
                    if(elem['id'] == self.data['id']):
                        body['origin'] = "remote"
                        settings.shoppingLists[index] = body

            self.update_items(body['id'])

        helpers.update_local_data(settings.userName, settings.shoppingLists)



    def create_new_item(self):
        ids = [item["id"] for item in self.data['items'] if isinstance(item, int)]
        newId = 1 + max(ids) if ids else 0
        frame = tk.Frame(self.scrollable_frame.frame)
        frame.pack(fill="x", padx=10, pady=10)

        itemframe = ItemFrame(frame, newId, "item", "numeric", 0)
        itemframe.pack()

    def return_shopping_lists_page(self):
        self.controller.frames[ShoppingListsPage].update_shopping_lists()
        self.controller.show_frame(ShoppingListsPage)

app = tkinterApp()

def on_closing():
    helpers.update_userToken(settings.userToken)
    app.destroy() 

app.protocol("WM_DELETE_WINDOW", on_closing)

app.mainloop()
