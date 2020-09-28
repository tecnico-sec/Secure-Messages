package pt.tecnico;

import java.util.*;

/**
 * Example of how to use the Base 64 encoder and decoder included with the JDK.
 * 
 * Base 64 is a way of encoding arbitrary binary data in ASCII text. Sockets can
 * send and receive binary data directly, but using a text format for data, like
 * XML or JSON, is easier to read and debug.
 * 
 * Base 64 takes 4 characters per 3 bytes of data, plus padding at the end. Each
 * 6 bits of the input is encoded in a 64-character alphabet with A-Z, a-z, 0-9
 * and + and /, with = as a padding character.
 * 
 * credits: https://www.baeldung.com/java-base64-encode-and-decode
 */
public class Base64Example {

    public static void main(String[] args) throws Exception {
        String originalInput = "Hello World";
        System.out.println("Original input: " + originalInput);
        byte[] originalBytes = originalInput.getBytes();
        System.out.printf("%d bytes%n", originalBytes.length);

        String encodedString = Base64.getEncoder().encodeToString(originalBytes);
        System.out.println("Data encoded as base 64: " + encodedString);
        byte[] encodedBytes = encodedString.getBytes();
        System.out.printf("%d bytes%n", encodedBytes.length);

        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);
        System.out.println("Decoded string: " + decodedString);
    }

}
