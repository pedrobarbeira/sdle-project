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
    public Response handle(Request request) {
        String route = request.getRoute();
        try {
            switch (route) {
                case SHOPPINGLIST -> {
                    return handleRequest(request);
                }
                case SHARE -> {
                    return handleShareRequest(request);
                }
                default -> {
                    return badRequest();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return badRequest();
        }
    }

    private Response handleRequest(Request request){
        return error();
    }

    private Response handleShareRequest(Request request){
        return error();
    }

    private Response sendRequestToServer(Request request, String address) throws JsonProcessingException {
        ZMQ.Socket socket = ctx.createSocket(SocketType.REQ);
        socket.connect(address);

        String requestStr = mapper.writeValueAsString(request);
        socket.send(requestStr);

        String responseStr = socket.recvStr();
        return mapper.readValue(responseStr, Response.class);
    }
}
