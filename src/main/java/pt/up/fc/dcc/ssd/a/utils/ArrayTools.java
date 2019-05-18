package pt.up.fc.dcc.ssd.a.utils;

import com.google.protobuf.ByteString;

import java.util.*;

public class ArrayTools {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static byte[] toPrimitives(Byte[] oBytes){
        byte[] bytes = new byte[oBytes.length];

        for(int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }

    public static Byte[] toAdvance(byte[] obytes){
        Byte[] byteObjects = new Byte[obytes.length];

        int i = 0;
        for(byte b : obytes){
            byteObjects[i++] = b;
        }
        return byteObjects;
    }
    public static String bytesToHex(ByteString bytes){
        return bytesToHex(bytes.toByteArray());
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static Object mode(final Object[] objs) {
        Map<Object, Integer> countMap = new HashMap<>();

        int max = -1;

        for (Object i: objs) {
            int count = 0;

            if(i!=null) {
                if (countMap.containsKey(i)) {
                    count = countMap.get(i) + 1;
                } else {
                    count = 1;
                }

                countMap.put(i, count);

                if (count > max) {
                    max = count;
                }
            }
        }

        for (Map.Entry<Object, Integer> tuple : countMap.entrySet()){
            if (tuple.getValue() == max) {
                return tuple.getKey();
            }
        }

        return null;
    }

    public static Object pickRandom(List<?> l){
        List<?> cl = new LinkedList<>(l);
        Collections.shuffle(cl);
        return cl.get(0);
    }

    public static Object shuffleList(List<?> l){
        List<?> cl = new LinkedList<>(l);
        Collections.shuffle(cl);
        return cl;
    }

    static byte[] xorID(byte[] id1,byte[] id2){
        int len = Math.min(id1.length, id2.length);
        byte[] result = new byte[len];

        for(int i = 0; i < len ; i++){
            result[i] = (byte)(id1[i] ^ id2[i]);
        }

        return result;
    }

    static int bitDistance(byte[] xored){
        int count = 0;

        BitSet xoredBit = BitSet.valueOf(xored);

        for(; !xoredBit.get(count); ++count);

        return xored.length - count;
    }

    public static int bitDistance(byte[] theHash, byte[] toByteArray) {
        return bitDistance(xorID(theHash, toByteArray));
    }
}
