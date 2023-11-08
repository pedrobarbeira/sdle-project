package org.sdle.api;

public class Request {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final  String PUT = "PUT";
    public static final String DELETE = "DELETE";
    private String route;
    private String method;
    private Object headers;
    private Object body;

    Request(){}

    public String getRoute(){
        return this.route;
    }

    public String getMethod(){
        return this.method;
    }

    public Object getHeaders() {
        return  this.headers;
    }

    public Object getBody(){
        return this.body;
    }

}
