package org.sdle;

import org.sdle.server.BootLoader;
import org.sdle.server.ObjectFactory;

public class Main {
    public static void main(String[] args) {
        try {
            String configFile = args[0];
            ObjectFactory.setServerConfigFile(configFile);
            BootLoader bootstrapper = new BootLoader();
            bootstrapper.bootServer();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}