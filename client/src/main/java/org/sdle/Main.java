package org.sdle;

import org.sdle.model.ShoppingListDataModel;
import org.sdle.services.SessionService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        try {
            ClientStub clientStub = new ClientStub();
            ShoppingListRepository repository = new ShoppingListRepository();
            SessionService sessionService = new SessionService();
            Client client = new Client(clientStub, sessionService);
            CommandHandler handler = new CommandHandler(repository, client);

            handler.boot();

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