package org.sdle.api.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.ObjectFactory;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.service.NodeService;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ShoppingListRequestHandler extends ApiComponent implements RequestHandler {
    public static final String SHOPPINGLIST = "api/shoppinglist";
    public static final String SHARE = "api/shoppingList/share";
    private final ObjectMapper mapper = ObjectFactory.getMapper();
    private final NodeService nodeService;
    private final ZContext ctx;

    public ShoppingListRequestHandler(NodeService nodeService, ZContext ctx) {
        this.nodeService = nodeService;
        this.ctx = ctx;
    }

    @Override
    public void handle(Request request, ZMQ.Socket socket) {
        String method = request.getMethod();
        try {
            switch (method) {
                case SHOPPINGLIST -> handleRequest(request, socket);
                case SHARE -> handleShareRequest(request, socket);
                default -> handleBadRequest(request, socket);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void handleRequest(Request request, ZMQ.Socket socket){

    }

    private void handleShareRequest(Request request, ZMQ.Socket socket){

    }

    private Response sendRequestToServer(Request request, String address) throws JsonProcessingException {
        ZMQ.Socket socket = ctx.createSocket(SocketType.REQ);
        socket.connect(address);

        String requestStr = mapper.writeValueAsString(request);
        socket.send(requestStr);
        
        String responseStr = socket.recvStr();
        return mapper.readValue(responseStr, Response.class);
    }

    private void handleBadRequest(Request request, ZMQ.Socket socket) throws JsonProcessingException {
        Response response = badRequest();
        String responseStr = mapper.writeValueAsString(response);
        socket.send(responseStr);
    }
}
