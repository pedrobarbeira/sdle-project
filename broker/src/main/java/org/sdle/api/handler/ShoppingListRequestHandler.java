package org.sdle.api.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.ObjectFactory;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.service.AuthService;
import org.sdle.service.NodeService;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.List;

public class ShoppingListRequestHandler extends ApiComponent implements RequestHandler {
    public static final String SHOPPINGLIST = "api/shoppinglist";
    public static final String SHARE = "api/shoppinglist/share";
    private final ObjectMapper mapper = ObjectFactory.getMapper();
    private final NodeService nodeService;
    private final AuthService authService;
    private final ZContext ctx;

    public ShoppingListRequestHandler(NodeService nodeService, AuthService authService, ZContext ctx) {
        this.nodeService = nodeService;
        this.authService = authService;
        this.ctx = ctx;
    }

    @Override
    public Response handle(Request request) {
        if (validate(request)) {
            String route = request.route;
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return badRequest();
    }

    private Response handleRequest(Request request){
        String method = request.method;
        switch(method){
            case Request.GET -> {
                return handleGetListsRequest(request);
            }
            case Request.POST -> {
                return handleCreateListRequest(request);
            }
            case Request.DELETE -> {
                return handleDeleteListRequest(request);
            }
            default -> {
                return badRequest();
            }
        }
    }

    private Response handleGetListsRequest(Request request){
        List<String> dummyList = List.of("Hello", "there");
        return ok(dummyList);
    }

    private Response handleCreateListRequest(Request request){
        return ok("Create list");
    }

    private Response handleDeleteListRequest(Request request){
        return ok("Delete list");
    }

    private Response handleShareRequest(Request request){
        String method = request.method;
        switch(method){
            case Request.POST -> {
                return handleListShareAddRequest(request);
            }
            case Request.DELETE -> {
                return handleListShareRemoveRequest(request);
            }
            default -> {
                return badRequest();
            }
        }
    }

    private Response handleListShareAddRequest(Request request){
        return ok("Adding list share");
    }

    private Response handleListShareRemoveRequest(Request request){
        return ok("Removing list share");
    }

    private boolean validate(Request request){
        HashMap<String, String> headers = request.headers;
        String token = headers.get(Headers.TOKEN);
        String user = headers.get(Headers.USER);
        return authService.validateToken(user, token);
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
