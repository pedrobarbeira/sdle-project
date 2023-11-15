import socket # for socket 
import sys 
import json
import threading

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

id = "2"

t1 = threading.Thread(target= lambda : print("1 ", send_request("localhost", 5555, "api/register-node" ,"POST", {}, {"id":id, "port":123})))
t2 = threading.Thread(target= lambda : print("2 ",send_request("localhost", 5555, "api/register-node" ,"POST", {}, {"id":id, "port":234})))
t3 = threading.Thread(target= lambda : print("3 ",send_request("localhost", 5555, "api/register-node" ,"POST", {}, {"id":id, "port":456})))

t1.start()
t2.start()
t3.start()

t1.join()
t2.join()
t3.join()

