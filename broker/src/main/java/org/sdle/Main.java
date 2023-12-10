package org.sdle;

public class Main {
    public static void main(String[] args) {
        try{
            ObjectFactory.getBrokerConfig();
            Broker broker = new Broker();
            broker.boot();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}