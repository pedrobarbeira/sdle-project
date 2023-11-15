package org.sdle.api;

public class Request {
    public static final java.lang.String GET = "GET";
    public static final java.lang.String POST = "POST";
    public static final java.lang.String PUT = "PUT";
    public static final java.lang.String DELETE = "DELETE";
    private java.lang.String route;
    private java.lang.String method;
    private Object headers;
    private Object body;

    Request(){}

    public java.lang.String getRoute(){
        return this.route;
    }

    public java.lang.String getMethod(){
        return this.method;
    }

    public Object getHeaders() {
        return  this.headers;
    }

    public Object getBody(){
        return this.body;
    }

}
