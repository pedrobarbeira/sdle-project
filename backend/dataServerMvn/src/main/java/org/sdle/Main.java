package org.sdle;

import org.sdle.server.Bootstrapper;
import org.sdle.server.ObjectFactory;

public class Main {
    public static void main(String[] args) {
        try {
            String configFile = args[0];
            ObjectFactory.setServerConfigFile(configFile);
            Bootstrapper bootstrapper = new Bootstrapper();
            bootstrapper.bootServer();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}