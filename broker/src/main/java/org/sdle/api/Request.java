package org.sdle.api;

import java.util.HashMap;

public class Request {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final  String PUT = "PUT";
    public static final String DELETE = "DELETE";
    private final String route;
    private final String method;
    private final HashMap<String, String> headers;
    private final Object body;

    public Request(String route, String method, HashMap<String, String> headers, Object body) {
        this.route = route;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public String getRoute(){
        return this.route;
    }

    public String getMethod(){
        return this.method;
    }

    public HashMap<String, String> getHeaders() {
        return  this.headers;
    }

    public Object getBody(){
        return this.body;
    }

}
