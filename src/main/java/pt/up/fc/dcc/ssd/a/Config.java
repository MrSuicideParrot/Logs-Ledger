package pt.up.fc.dcc.ssd.a;

public class Config {
    public final static int maxLogs = 10;
    public final static String pubKeyFile = "pubKey.der";
    public final static String privKeyFile = "privKey.der";
    public final static int port = 3423;
    public final static int nBuckets = 6;
    public final static int k = 10; // numero de contactos por bucket
    public final static int alpha = 3;
    public final static int id_length = 30;
    public static byte[] myID;
    public static String trackerIp = "localhost";

    public static int SPREAD_BLOCK_POINT = -1000;
    public static int SPREAD_FALSE_BLOCK = 2000;

    public static int confidence_update_time = 60*1000;
    public static int check_blockchain = 60*1000;

    public static String ipv4;
}
