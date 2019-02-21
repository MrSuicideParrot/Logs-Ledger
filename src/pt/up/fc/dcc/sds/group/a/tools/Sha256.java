package pt.up.fc.dcc.sds.group.a.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Sha256 {
    /**
     *
     * @param input content to hash
     * @return Sha256 of the input
     */
    public static byte[] hash(byte[] input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param hashInput hash to test
     * @param input input to test
     * @return if the input hash corresponds to the hashInput returns true
     */
    public static boolean verifyHash(byte[] hashInput, byte[] input){
        byte[] testingInput = hash(input);

        return Arrays.equals(testingInput, hashInput);
    }
}
