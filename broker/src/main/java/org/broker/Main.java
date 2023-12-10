package org.broker;

import org.broker.config.BrokerConfig;
import org.broker.service.AuthService;

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