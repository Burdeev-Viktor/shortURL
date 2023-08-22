package org.example.service.business;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomGenerator {
    private static final long UPPER_RANGE = 3579345993194L;
    private static final long LOWER_RANGE = 56800235584L;
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE62_BASE = BASE62_ALPHABET.length();

    public  String getRandomKey(){
        StringBuilder result = new StringBuilder();
        long number = getRandomLong();
        while (number >= BASE62_BASE) {
            int i = (int)(number % BASE62_BASE);
            result.append(BASE62_ALPHABET.charAt(i));
            number = number / BASE62_BASE;
        }
        result.append(BASE62_ALPHABET.charAt((int) number));
        return result.reverse().toString();
    }
    private static long getRandomLong(){
        Random random = new Random();
        return random.nextLong(LOWER_RANGE,UPPER_RANGE);
    }
}
