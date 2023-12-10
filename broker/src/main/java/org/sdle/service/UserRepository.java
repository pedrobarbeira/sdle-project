package org.sdle.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.ObjectFactory;
import org.sdle.model.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

public class UserRepository {
    public static final String USERS_FILE = "users.json";
    private String dataRoot;
    private HashMap<String, User> userMap;
    private final ObjectMapper mapper = ObjectFactory.getMapper();


    public void initializeRepository(String dataRoot) throws IOException {
        this.dataRoot = dataRoot;
        ClassLoader loader = UserRepository.class.getClassLoader();
        String dirPath = Objects.requireNonNull(loader.getResource(dataRoot)).getPath();
        String path = String.format("%s/%s", dirPath, USERS_FILE);
        File dataFile = new File(path);
        userMap = mapper.readValue(dataFile, new TypeReference<>(){});
    }

    public User getUser(String username){
        return userMap.get(username);
    }

    public void createUser(String username, String password) throws IOException {
        User user = new User(username, password);
        userMap.put(username, user);
        ClassLoader loader = UserRepository.class.getClassLoader();
        String dirPath = Objects.requireNonNull(loader.getResource(dataRoot)).getPath();
        String path = String.format("%s/%s", dirPath, USERS_FILE);
        File file = new File(path);
        OutputStream os = new FileOutputStream(file);
        String jsonData = mapper.writeValueAsString(userMap);
        os.write(jsonData.getBytes(StandardCharsets.UTF_8));
    }

}
