package org.sdle.api.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.ObjectFactory;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.model.ItemOperationDataModel;
import org.sdle.model.ListOperationDataModel;
import org.sdle.model.ShareOperationDataModel;
import org.sdle.service.AuthService;
import org.sdle.service.NodeService;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class OperationRequestHandler extends ApiComponent implements RequestHandler {
    public static final String API_SHOPPINGLIST = "api/shoppinglist";
    public static final String API_SHARED = "api/shared";
    public static final String API_ITEMS = "api/items";
    private final ObjectMapper mapper = ObjectFactory.getMapper();
    private final NodeService nodeService;
    private final AuthService authService;
    private final ZContext ctx;

    public OperationRequestHandler(NodeService nodeService, AuthService authService, ZContext ctx) {
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
                    case API_SHOPPINGLIST -> {
                        return handShoppintListRequest(request);
                    }
                    case API_SHARED -> {
                        return handleShareRequest(request);
                    }
                    case API_ITEMS -> {
                        return handleItemsRequest(request);
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

    private Response handShoppintListRequest(Request request){
        String method = request.method;
        switch(method){
            case Request.POST -> {
                return handleCreateListRequest(request);
            }
            default -> {
                return handleListOperationsRequest(request);
            }
        }
    }

    private Response handleCreateListRequest(Request request){
        String listUuid = UUID.randomUUID().toString();
        String prefix = nodeService.getNextPrefix();
        String listId = String.join("-", prefix, listUuid);
        ListOperationDataModel dataModel = new ListOperationDataModel(listId);
        return ok("Creating list");
    }

    private Response handleListOperationsRequest(Request request){
        ListOperationDataModel dataModel = (ListOperationDataModel) request.body;
        String prefix = dataModel.targetId.split("-")[0];
        String address = nodeService.getPrefixMainAddress(prefix);
        return ok("Handling list operations");
    }

    private Response handleShareRequest(Request request){
        ShareOperationDataModel dataModel = (ShareOperationDataModel) request.body;
        String prefix = dataModel.targetId.split("-")[0];
        String address = nodeService.getPrefixMainAddress(prefix);
        return ok("Handling share request");
    }

    private Response handleItemsRequest(Request request){
        ItemOperationDataModel dataModel = (ItemOperationDataModel) request.body;
        String prefix = dataModel.targetId.split("-")[0];
        String address = nodeService.getPrefixMainAddress(prefix);
        return ok("Handling share request");
    }


    private boolean validate(Request request){
        HashMap<String, String> headers = request.headers;
        String token = headers.get(Headers.TOKEN);
        String key = headers.get(Headers.KEY);
        return authService.validateToken(key, token);
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
