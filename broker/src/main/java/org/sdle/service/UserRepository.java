package org.sdle.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.ObjectFactory;
import org.sdle.model.User;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class UserRepository {
    public static final String USERS_FILE = "users.json";

    private HashMap<String, User> userMap;
    private final ObjectMapper mapper = ObjectFactory.getMapper();


    public void initializeRepository(String dataRoot) throws IOException {
        ClassLoader loader = UserRepository.class.getClassLoader();
        String dirPath = Objects.requireNonNull(loader.getResource(dataRoot)).getPath();
        String path = String.format("%s/%s", dirPath, USERS_FILE);
        File dataFile = new File(path);
        userMap = mapper.readValue(dataFile, new TypeReference<>(){});
    }

    public User getUser(String username){
        return userMap.get(username);
    }


}
