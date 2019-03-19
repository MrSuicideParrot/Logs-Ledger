package pt.up.fc.dcc.ssd.a.kademlia;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public class Challenge {
    private int zeros;

    public Challenge(int zeros){
        this.zeros = zeros;
    }

    public idHashPair findID(){
        SecureRandom random = new SecureRandom();
        boolean found = false;
        byte id[] = new byte[160 / 8];
        byte hash[] = new byte[160 / 8];
        while (!found) {
            random.nextBytes(id);
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.reset();
                digest.update(id);
                hash = digest.digest();
            } catch (Exception e) {
                e.printStackTrace();
            }
            found = true;
            for (byte b : Arrays.copyOfRange(hash, 0, this.zeros)) {
                if (b != 0) {
                    found = false;
                }
            }
        }
        return new idHashPair(id, hash);
    }

    private String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }

    public void debug(){
        idHashPair test = findID();
        System.out.println(bytesToHex(test.id));
        System.out.println(bytesToHex(test.hash));
    }

}
