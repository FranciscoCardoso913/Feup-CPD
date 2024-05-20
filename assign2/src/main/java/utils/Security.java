package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {

    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * Hashes a message using SHA-256
     *
     * @param msg Message to hash
     * @return The hashed message as a hexadecimal string.
     */
    public static String hash(String msg) throws NoSuchAlgorithmException {
        MessageDigest algorithm = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] messageDigest = algorithm.digest(msg.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            hexString.append(String.format("%02X", 0xFF & b));
        }
        return hexString.toString();
    }
}
