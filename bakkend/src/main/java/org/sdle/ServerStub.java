package org.sdle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.api.Request;
import org.sdle.api.Response;
import org.sdle.api.Router;

import java.io.IOException;

public class ServerStub {
    //TODO consider implementing a MQ system here
    private final Router router;
    private final String port;
    private final ObjectMapper mapper = ObjectFactory.getObjectMapper();

    public ServerStub(Router router, String port){
        this.router = router;
        this.port = port;
    }

    public void listen(){
        while(true) {
            //open port and listen
            //receive request string
            //start new thread
                //handle request string
                //send response to broker
                //close thread
        }
    }

    private String handleRequestString(String requestString) throws IOException {
        //TODO add exception handler logic
        Request request = mapper.readValue(requestString, Request.class);
        Response response = router.route(request);
        return mapper.writeValueAsString(response);
    }
}
