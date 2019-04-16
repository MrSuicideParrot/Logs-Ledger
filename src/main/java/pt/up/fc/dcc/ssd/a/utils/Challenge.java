package pt.up.fc.dcc.ssd.a.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class Challenge {
    private int zeros;

    public Challenge(int zeros){
        this.zeros = zeros;
    }

    public byte[] findID(){
        SecureRandom random = new SecureRandom();
        byte[] id = new byte[160 / 8];
        byte[] hash;
        random.nextBytes(id);
        hash = genHash(id);
        while (countZeros(hash) != this.zeros) {
            random.nextBytes(id);
            hash = genHash(id);
        }
        return id;
    }

    public static byte[] genHash(byte[] id){
        byte[] hash = new byte[160/8];

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(id);
            hash = digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hash;
    }
    
    public static int countZeros(byte[] hash){
        int count = 0;
        for(int i = 0; i < hash.length; i++){
            for(int j = 7; j >= 0; j--) {
                if ((hash[i] & (1 << j)) != 0)
                    return count;
                count++;
            }
        }
        return count;
    }

    public static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }

    public void debug(){
        byte[] id = findID();
        byte[] hash = genHash(id);

        System.out.println(bytesToHex(id));
        System.out.println(bytesToHex(hash));
    }

}
