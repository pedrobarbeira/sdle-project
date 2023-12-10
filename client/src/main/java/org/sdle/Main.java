package org.sdle;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        ClientStub clientStub = new ClientStub();
        Client client = new Client(clientStub);
        CommandHandler handler = new CommandHandler(client);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String command;
            System.out.printf("For a list of available commands type 'help'%n");
            while (true) {
                System.out.print(">");
                command = reader.readLine();
                handler.handle(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}