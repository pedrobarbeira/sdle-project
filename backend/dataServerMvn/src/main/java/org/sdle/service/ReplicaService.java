package org.sdle.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.repository.crdt.operation.CRDTOp;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;

public class ReplicaService<T> extends ApiComponent implements IReplicaService<T> {
    private final ZContext ctx;
    private boolean tmpListenerFlag;
    private final List<ZMQ.Socket> replicatedOnSockets;
    private final ObjectMapper mapper = new ObjectMapper();

    public ReplicaService(ZContext ctx){
        this.ctx = ctx;
        this.tmpListenerFlag = false;
        this.replicatedOnSockets = new ArrayList<>();
    }

    public void publish(CRDTOp<T> crdtOp){
        //sends CRDTOp to replica nodes
    }

    public void registerReplicatedOn(List<String> addresses){
        for(String address : addresses){
            ZMQ.Socket socket = createSocket(address, SocketType.REQ);
            this.replicatedOnSockets.add(socket);
        }
    }

    public void subscribe(String address, CRDTExecutionService<T> executionService){
        ZMQ.Socket socket = createSocket(address, SocketType.REP);
        while(!Thread.currentThread().isInterrupted()){
            receiveReplicaData(socket, executionService);
        }
    }

    public void startTmpListener(String address, CRDTExecutionService<T> executionService){
        this.tmpListenerFlag = true;
        ZMQ.Socket socket = createSocket(address, SocketType.REP);
        while(tmpListenerFlag){
            receiveReplicaData(socket, executionService);
        }
    }


    public void stopTmpListeners(){
        this.tmpListenerFlag = false;
    }

    private ZMQ.Socket createSocket(String address, SocketType socketType){
        ZMQ.Socket socket = this.ctx.createSocket(socketType);
        socket.bind(address);
        return socket;
    }

    private void receiveReplicaData(ZMQ.Socket socket, CRDTExecutionService<T> executionService){
        String responseStr = "";
        try{
            String requestStr = socket.recvStr();
            Request request = this.mapper.readValue(requestStr, Request.class);
            CRDTOp<T> operation = (CRDTOp<T>) request.getBody();
            executionService.addOperation(operation);
            Response response = ok(operation);
            responseStr = this.mapper.writeValueAsString(response);
        }catch(Exception e){
            e.printStackTrace();
        }
        socket.send(responseStr);
    }

}
