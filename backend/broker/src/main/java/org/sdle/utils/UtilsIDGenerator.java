package org.sdle.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class UtilsIDGenerator {
    private static final int MAX_VALUE = 62 * 62; // Maximum possible IDs (62*62 for alphanumeric)
    private static final AtomicInteger idCounter = new AtomicInteger(0);

    public static String generateSequentialID() {
        int currentId = idCounter.getAndIncrement();
        if (currentId >= MAX_VALUE) {
            return null;
        }

        int firstCharIndex = currentId / 62;
        int secondCharIndex = currentId % 62;

        char firstChar = getCharFromIndex(firstCharIndex);
        char secondChar = getCharFromIndex(secondCharIndex);

        return firstChar + String.valueOf(secondChar);
    }

    private static char getCharFromIndex(int index) {
        if (index < 10) {
            return (char) ('0' + index); // Digits '0' to '9'
        } else if (index < 36) {
            return (char) ('A' + index - 10); // Uppercase letters 'A' to 'Z'
        } else {
            return (char) ('a' + index - 36); // Lowercase letters 'a' to 'z'
        }
    }


}

