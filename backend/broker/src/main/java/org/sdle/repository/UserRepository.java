package org.sdle.repository;

import org.sdle.model.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
public class UserRepository {
    HashMap<String, User> userHashMap = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();
    public void loadFromMemory() {
        String path = "users.json";

        InputStream stream = ClassLoader.getSystemResourceAsStream(path);
        if(stream != null){
            try{
                User[] users = mapper.readValue(stream, User[].class);
                for (User user : users) {
                    userHashMap.put(user.getUsername(), user);
                }
            }catch(IOException e){
                System.err.println(e.getMessage());
            }
        }else{
            System.err.printf("File [%s] was not found\n", path);
        }
    }

    public User getUserByUsername(String username) {
        return userHashMap.get(username);
    }
}
