package org.sdle.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class UtilsTcp {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static byte[] buildMessage(java.lang.String response) {
        java.lang.String message = "\r\n" +
                response.length() +
                "\r\n" +
                "\r\n" +
                response +
                "\r\n";

        return message.getBytes();
    }
    private static int parseRequestSize(java.lang.String header) {
        List<java.lang.String> split = List.of(header.split("\r\n"));
        if(split.size() != 2 || !Objects.equals(split.get(0), "")) return -1;
        return Integer.parseInt(split.get(1));
    }
    public static void sendTcpMessage(Socket socket, java.lang.String body) {
        try {
            OutputStream out = socket.getOutputStream();
            byte[] message = buildMessage(body);
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String receiveTcpMessage(Socket socket) {
        StringBuilder headerString = new StringBuilder();
        StringBuilder bodyString = new StringBuilder();

        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            char[] buffer = new char[1024];
            int n;
            boolean header = true;
            int requestLen = -1;
            while((n = reader.read(buffer)) != -1) {
                for (int i = 0; i < n; i++) {
                    if(header) {
                        headerString.append(buffer[i]);
                    } else {
                        bodyString.append(buffer[i]);
                        requestLen--;
                        if (requestLen == 0) break;
                    }

                    if(header && headerString.length() > 3 && headerString.substring(headerString.length() - 4).equals("\r\n\r\n")) {
                        requestLen = parseRequestSize(headerString.toString());
                        if(requestLen < 0) return null;
                        header = false;
                    }
                }

                if(requestLen == 0) break;
            }
            return bodyString.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
