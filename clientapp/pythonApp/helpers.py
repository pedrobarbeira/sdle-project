import json
import zmq

def read_local_data(username: str) -> None:
    try:
        with open('data/shoppingLists.json', 'r') as file:
            data = json.load(file)
            for elem in data:
                if(elem["userName"] == username):
                    for list in elem['data']['local']:
                        list['origin'] = 'local'
                    for list in elem['data']['remote']:
                        list['origin'] = 'remote'
                    return elem['data']['local'] + elem['data']['remote']
        return []  
    except FileNotFoundError:
        return []
    
def update_local_data(username: str, shoppingLists: list) -> None:
    try:
        with open('data/shoppingLists.json', 'r') as file:
            data = json.load(file)

        matchedEntry = False

        for entry in data:
            if entry["userName"] == username:
                entry["data"]["local"] = [s for s in shoppingLists if s['origin'] == "local"]
                entry["data"]["remote"] = [s for s in shoppingLists if s['origin'] == "remote"]
                matchedEntry = True
                break
        
        if(not matchedEntry):
            entry = {"userName": username, "data": {"local": [], "remote": []}}
            entry["data"]["local"] = [s for s in shoppingLists if s['origin'] == "local"]
            entry["data"]["remote"] = [s for s in shoppingLists if s['origin'] == "remote"]
            data.append(entry)

        with open('data/shoppingLists.json', 'w') as file:
            json.dump(data, file, indent=4)

        print(f"Updated {'data/shoppingLists.json'} for userName: {username}")
    except FileNotFoundError:
        print(f"File {'data/shoppingLists.json'} not found.")

def send_request(address: str, port: int, route: str, method: str, body: dict) -> tuple:
    context = zmq.Context()
    socket = context.socket(zmq.REQ)
    socket.connect(f"tcp://{address}:{port}")

    request = {
    "route": route,
    "method": method,
    "body": json.dumps(body)
    }

    socket.send_string(json.dumps(request))

    response = socket.recv_string()
    response_parsed = json.loads(response)

    return response_parsed['status'], response_parsed['body']

def login(username: str, password: str) -> tuple:

    body = {
        "username": username,
        "password": password
    }

    return send_request("localhost", 5555, "api/login", "POST", body)

def validate_userToken(token: str) -> tuple:

    body = {
        "token": token
    }

    return send_request("localhost", 5555, "api/verify-token", "GET", body)

def load_userToken() -> tuple:
    try:
        with open('data/userToken.json', 'r') as file:
            data = json.load(file)

            try:
                token = data['userToken']
            except:
                print("userToken not present in json")
                return "", ""
            
            if(token == ""): return "", ""

            status, body = validate_userToken(token)

            if(status != 200): return "", ""

            try:
                username = json.loads(body)['username']

                return username, token
            except:
                print(f'malformed response body {body}')

            return "", ""

    except FileNotFoundError:
        print(f"File {'data/userToken.json'} not found.")
        return ""
    
def update_userToken(token: str) -> None:
    try:
        data = {"userToken": token}
        with open('data/userToken.json', 'w') as file:
            json.dump(data, file, indent=4)

    except FileNotFoundError:
        print(f"File {'data/userToken.json'} not found.")

def get_remote_shoppingList(token: str) -> tuple:
    body = {
        "token": token
    }

    return send_request("localhost", 5556, "api/shoppinglist", "GET", body)

def del_remote_shoppingList(token: str, shoppingList: dict) -> tuple:
    body = {
            "token": {
            "token": token
        },
        "shoppingList": {
            "id": shoppingList['id'],
            "authorizedUsers": shoppingList['authorizedUsers']
        }
    }
    
    return send_request("localhost", 5556, "api/shoppinglist", "DELETE", body)

def post_remote_shoppingList(token: str, shoppingList: dict) -> tuple:
    body = {
            "token": {
            "token": token
        },
        "shoppingList": {
        }
    }

    for key, value in shoppingList.items():

        match key:
            case "id":
                if(isinstance(value, str)):
                    body['shoppingList']['id'] = value
            case "name":
                body['shoppingList']['name'] = value
            case "items":
                body['shoppingList']['items'] = []
                for item in value:
                    if(isinstance(item['id'], str)):
                        body['shoppingList']['items'].append(item)
                    else:
                        body['shoppingList']['items'].append({ 'name': item['name'], 'type': item['type'], 'quantity': item['quantity']})
            case "authorizedUsers":
                body['shoppingList']['authorizedUsers'] = shoppingList['authorizedUsers']
            case _:
                continue
    
    return send_request("localhost", 5556, "api/shoppinglist", "POST", body)

def post_share_shoppingList(token: str, shoppingList: dict, user :str) -> tuple:
    body = {
            "token": {
                "token": token
            },
            "shoppingList": {},
            "sharingWith": user
        }

    for key, value in shoppingList.items():

        match key:
            case "id":
                if(isinstance(value, str)):
                    body['shoppingList']['id'] = value
            case "name":
                body['shoppingList']['name'] = value
            case "items":
                body['shoppingList']['items'] = []
                for item in value:
                    if(isinstance(item['id'], str)):
                        body['shoppingList']['items'].append(item)
                    else:
                        body['shoppingList']['items'].append({ 'name': item['name'], 'type': item['type'], 'quantity': item['quantity']})
            case "authorizedUsers":
                body['shoppingList']['authorizedUsers'] = shoppingList['authorizedUsers']
            case _:
                continue

    return send_request("localhost", 5556, "api/shoppinglist-share", "POST", body)

