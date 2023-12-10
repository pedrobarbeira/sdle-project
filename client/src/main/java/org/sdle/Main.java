package org.sdle;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        try {
            ClientStub clientStub = new ClientStub();
            ShoppingListRepository repository = new ShoppingListRepository();
            Client client = new Client(repository, clientStub);
            CommandHandler handler = new CommandHandler(client);

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