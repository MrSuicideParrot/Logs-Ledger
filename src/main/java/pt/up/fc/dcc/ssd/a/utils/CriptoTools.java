package pt.up.fc.dcc.ssd.a.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CriptoTools {
    public static byte[] hash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return md.digest();
        } catch (
                NoSuchAlgorithmException e) {
            return null;
        }
    }
}
