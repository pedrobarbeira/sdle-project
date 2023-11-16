package org.sdle.configuration;

public class ServerConfig {
    private String port;
    private  String dataRoot;

    public String getDataRoot(){
        return this.dataRoot;
    }

    public String getPort(){ return this.port; }
}
