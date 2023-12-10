package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.ApiComponent;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.handler.AuthRequestHandler;
import org.sdle.api.handler.OperationRequestHandler;
import org.sdle.api.Router;
import org.sdle.config.BrokerConfig;
import org.sdle.service.AuthService;
import org.sdle.service.NodeService;
import org.sdle.service.UserRepository;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;

public class Broker extends ApiComponent {
    public static final String API_WORKERS = "inproc://workers";
    private final ZContext ctx;
    private final ObjectMapper mapper = ObjectFactory.getMapper();
    private BrokerConfig brokerConfig;
    private Router router;

    public Broker() {
        this.ctx = new ZContext();
    }

    public void boot() throws IOException {
        System.out.println("Initializing broker");
        initializeDependencies();
        String brokerAddress = String.format("%s:%d", brokerConfig.apiBase, brokerConfig.port);
        System.out.printf("Opening listeners on %s%n", brokerAddress);
        ZMQ.Socket clients = ctx.createSocket(SocketType.ROUTER);
        clients.bind(brokerAddress);
        ZMQ.Socket workers = ctx.createSocket(SocketType.DEALER);
        workers.bind(API_WORKERS);
        for (int i = 0; i < brokerConfig.maxActiveThreads; i++) {
            Thread worker = new BrokerStub(ctx, router);
            worker.start();
        }
        System.out.printf("Listening on %d threads%n", brokerConfig.maxActiveThreads);
        ZMQ.proxy(clients, workers, null);
    }

    public void initializeDependencies() throws IOException {
        brokerConfig = ObjectFactory.getBrokerConfig();
        String dataRoot = brokerConfig.dataRoot;

        UserRepository repository = new UserRepository();
        repository.initializeRepository(dataRoot);
        AuthService authService = new AuthService(repository);
        AuthRequestHandler authRequestHandler = new AuthRequestHandler(authService);

        NodeService nodeService = new NodeService(brokerConfig);
        OperationRequestHandler shoppingListRequestHandler = new OperationRequestHandler(nodeService, authService, ctx);

        router = new Router(shoppingListRequestHandler, authRequestHandler);
    }

    class BrokerStub extends Thread
    {
        private final ZContext context;
        private final Router router;

        private BrokerStub(ZContext context, Router router) {
            this.context = context;
            this.router = router;
        }

        @Override
        public void run() {
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.connect(API_WORKERS);
            while (true) {
                try {
                    String requestStr = socket.recvStr(0);
                    System.out.println("Received request");
                    Request request = mapper.readValue(requestStr, Request.class);
                    Response response = router.handle(request);
                    String responseStr = mapper.writeValueAsString(response);
                    socket.send(responseStr);;
                }catch(Exception e){
                    e.printStackTrace();
                    socket.send("500 - Internal Server Error");
                }
            }
        }
    }
}
