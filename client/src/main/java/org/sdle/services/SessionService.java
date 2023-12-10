package org.sdle.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdle.Client;
import org.sdle.ObjectFactory;
import org.sdle.api.ApiComponent;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class SessionService {
    private static final String HEARDERS_FILE = "headers.json";
    public final HashMap<String, String> headers;
    private ObjectMapper mapper = ObjectFactory.getMapper();

    public SessionService() {
        this.headers = new HashMap<>();
    }

    public void persistHeaders(String username, String token, String key) {
        headers.put(ApiComponent.Headers.TOKEN, token);
        headers.put(ApiComponent.Headers.USER, username);
        headers.put(ApiComponent.Headers.KEY, key);

        ClassLoader loader = Client.class.getClassLoader();

        String path = Objects.requireNonNull(loader.getResource(HEARDERS_FILE)).getPath();
        File file = new File(path);

        try {
            OutputStream os = new FileOutputStream(file);
            String jsonData = mapper.writeValueAsString(this.headers);
            os.write(jsonData.getBytes());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearHeaders() {
        this.headers.clear();

        ClassLoader loader = Client.class.getClassLoader();

        String path = Objects.requireNonNull(loader.getResource(HEARDERS_FILE)).getPath();
        File file = new File(path);

        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");

            raf.setLength(0);

            raf.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, String> parseHeadersFile() throws IOException {
        ClassLoader loader = Client.class.getClassLoader();
        String path = Objects.requireNonNull(loader.getResource(HEARDERS_FILE)).getPath();
        File file = new File(path);

        return mapper.readValue(file, HashMap.class);
    }
}
