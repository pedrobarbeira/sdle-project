import json
import zmq
import time
import socket

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

def receive_response_zmq(socket: zmq.Socket) -> str:
    try:
        return socket.recv_string()
    except zmq.error.Again:
        return None 

def send_request_zmq(address: str, port: int, route: str, method: str, headers: dict, body: dict, timeout: int = 5) -> tuple:

    context = zmq.Context()
    socket = context.socket(zmq.REQ)
    socket.connect(f"tcp://{address}:{port}")

    try:
        request = {
            "route": route,
            "method": method,
            "headers": json.dumps(headers),
            "body": json.dumps(body)
        }
        socket.send_string(json.dumps(request))

        poller = zmq.Poller()
        poller.register(socket, zmq.POLLIN)

        start_time = time.time()
        while True:
            if poller.poll(1000):  # Poll every 1 second
                response = receive_response_zmq(socket)
                if response:
                    break

            if time.time() - start_time > timeout:
                print(f"Request timed out. {route} {method} {body}")
                break

        if response:
            response_parsed = json.loads(response)
            return response_parsed['status'], response_parsed['body']
        else:
            return 500, ""
    except Exception as e:
        print(f"An error occurred: {str(e)}")
        return 500, ""


def parseRequestSize(header: str) -> int:
    split = header.split("\r\n")
    split = [s for s in split if s != ""]

    if(len(split) != 1):
        return -1
    
    return int(split[0])

def send_request(address: str, port: int, route: str, method: str, headers: dict, body: dict, timeout: int = 5) -> tuple:
    
    try: 
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
    except socket.error as err: 
        print ("socket creation failed with error %s" %(err))
        return 500, ""
        
    try: 
        host_ip = socket.gethostbyname(address) 
    except socket.gaierror: 
        print ("there was an error resolving the host")
        return 500, ""
 
    sock.connect((host_ip, port)) 

    request = {
                "route": route,
                "method": method,
                "headers": json.dumps(headers),
                "body": json.dumps(body)
            }

    message = "\r\n" + str(len(json.dumps(request))) + "\r\n\r\n" + json.dumps(request) + "\r\n"

    sock.send(bytes(message, 'utf-8'))

    bufSize = 8

    headerStr = ""
    requestStr = ""

    isHeader = True
    requestLen = -1

    while(True):
        a = sock.recv(bufSize).decode()

        for c in str(a):
            if(isHeader):
                headerStr += c
            else:
                requestStr += c
                requestLen -= 1;
                if(requestLen == 0): break;
            
            if(isHeader and len(headerStr) > 3 and headerStr[len(headerStr) - 4:] == "\r\n\r\n"):
                requestLen = parseRequestSize(headerStr)
                isHeader = False

        if(requestLen <= 0): break

    response_parsed = json.loads(requestStr)
    return response_parsed['status'], response_parsed['body']

def login(username: str, password: str) -> tuple:

    body = {
        "username": username,
        "password": password
    }

    return send_request("localhost", 5555, "api/login", "POST", {}, body)

def validate_userToken(token: str) -> tuple:

    body = {
        "token": token
    }

    return send_request("localhost", 5555, "api/verify-token", "GET", {}, body)

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
                username = body['username']

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
    headers = {
        "token": token
    }

    return send_request("localhost", 5556, "api/shoppinglist", "GET", headers, {})

def del_remote_shoppingList(token: str, shoppingList: dict) -> tuple:
    body = {
        "id": shoppingList['id'],
        "authorizedUsers": shoppingList['authorizedUsers']
    }

    headers = {
        "token": token
    }
    
    return send_request("localhost", 5556, "api/shoppinglist", "DELETE", headers, body)

def post_remote_shoppingList(token: str, shoppingList: dict) -> tuple:
    headers = {
        "token": token
    }
    
    body = {
    }

    for key, value in shoppingList.items():

        match key:
            case "id":
                if(isinstance(value, str)):
                    body['id'] = value
            case "name":
                body['name'] = value
            case "items":
                body['items'] = []
                for item in value:
                    if(isinstance(item['id'], str)):
                        body['items'].append(item)
                    else:
                        body['items'].append({ 'name': item['name'], 'type': item['type'], 'quantity': item['quantity']})
            case "authorizedUsers":
                body['authorizedUsers'] = shoppingList['authorizedUsers']
            case _:
                continue

    return send_request("localhost", 5556, "api/shoppinglist", "POST", headers, body)

def post_share_shoppingList(token: str, shoppingList: dict, user :str) -> tuple:
    headers = {
        "token": token
    }
    
    body = {
            "shoppingListId": shoppingList['id'],
            "sharingWith": user
        }

    return send_request("localhost", 5556, "api/shoppinglist-share", "POST", headers, body)

