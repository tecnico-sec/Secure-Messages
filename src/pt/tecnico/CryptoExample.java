package pt.tecnico;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Example of how to some of the cryptographic functions available in the JDK.
 * 
 * We read a secret key from a file and use it to cipher data.
 * We also compute a digest of the same data.
 * 
 * Reference: https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html#Introduction
 */
public class CryptoExample {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 1) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s key%n", CryptoExample.class.getName());
            return;
        }
        final String keyPath = args[0];

        // read key
        System.out.println("Reading key from file " + keyPath + "...");
        Key key = readSecretKey(keyPath);

        // plain text
        String plainText = "This is the data that we want to protect from unauthorized parties";
        byte[] plainBytes = plainText.getBytes();
        System.out.println("Plain text: " + plainText);
        System.out.println(plainBytes.length + " bytes");

        // cipher data
        final String CIPHER_ALGO = "AES/ECB/PKCS5Padding";
        System.out.println("Ciphering with " + CIPHER_ALGO + "...");
        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherBytes = cipher.doFinal(plainBytes);
        System.out.println("Result: " + cipherBytes.length + " bytes");

        String cipherB64dString = Base64.getEncoder().encodeToString(cipherBytes);
        System.out.println("Cipher result, encoded as base 64 string: " + cipherB64dString);

        // digest data
        final String DIGEST_ALGO = "SHA-256";
        System.out.println("Digesting with " + DIGEST_ALGO + "...");
        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
        messageDigest.update(plainBytes);
        byte[] digestBytes = messageDigest.digest();
        System.out.println("Result: " + digestBytes.length + " bytes");

        String digestB64dString = Base64.getEncoder().encodeToString(digestBytes);
        System.out.println("Digest result, encoded as base 64 string: " + digestB64dString);

    }

    private static byte[] readFile(String path) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(path);
        byte[] content = new byte[fis.available()];
        fis.read(content);
        fis.close();
        return content;
    }

    public static Key readSecretKey(String secretKeyPath) throws Exception {
        byte[] encoded = readFile(secretKeyPath);
        SecretKeySpec keySpec = new SecretKeySpec(encoded, "AES");
        return keySpec;
    }

    public static PublicKey readPublicKey(String publicKeyPath) throws Exception {
        System.out.println("Reading public key from file " + publicKeyPath + " ...");
        byte[] pubEncoded = readFile(publicKeyPath);
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubEncoded);
        KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
        PublicKey pub = keyFacPub.generatePublic(pubSpec);
        return pub;
    }

    public static PrivateKey readPrivateKey(String privateKeyPath) throws Exception {
        byte[] privEncoded = readFile(privateKeyPath);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
        KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");
        PrivateKey priv = keyFacPriv.generatePrivate(privSpec);
        return priv;
    }

}
