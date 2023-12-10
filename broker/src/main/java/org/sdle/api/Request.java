package org.sdle.api;

import java.util.HashMap;

public class Request {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final  String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public String route;
    public String method;
    public HashMap<String, String> headers;
    public Object body;
}
