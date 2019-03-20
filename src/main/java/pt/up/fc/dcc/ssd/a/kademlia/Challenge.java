package pt.up.fc.dcc.ssd.a.kademlia;

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
        byte[] hash = new byte[160 / 8];
        while (countZeros(hash) != this.zeros) {
            random.nextBytes(id);
            hash = genHash(id);
        }
        return id;
    }

    private byte[] genHash(byte[] id){
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
    
    private int countZeros(byte[] hash){
        int count = 0;
        for(int i = 0; i < hash.length; i++){
            for(int j = 7; j >= 0; j--) {
                if ((hash[i] & (1 << j)) != 0){
                    count++;
                } else {
                    return count;
                }
            }
        }
        return count;
    }

    private String bytesToHex(byte[] hashInBytes) {

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
