package pt.up.fc.dcc.ssd.a;

import com.google.protobuf.ByteString;

public class Config {
    public final static int maxLogs = 5;
    public final static String pubKeyFile = "pubKey.der";
    public final static String privKeyFile = "privKey.der";
    public final static int port_node = 3423;
    public  final static int port_tracker = 3424;
    public final static int nBuckets = 6;
    public final static int k = 10; // numero de contactos por bucket
    public final static int alpha = 3;
    public final static int id_length = 30;
    public static ByteString myID;
    public static String trackerIp = "localhost";
    public final static int zeros = 20;

    /* Confiança */
    public static int SPREAD_BLOCK_POINT = -1000; /* Accepted */
    public static int SPREAD_FALSE_BLOCK = 2000; /* Accepted */

    public static int CONFIRM_BLOCK = -100;
    public static int REJECTED_BLOCK = 100;

    public static int CONFIRM_FAIL = 100; /* Grpc erro na confirmação */

    public static int CONF_TRUE = -1000;
    public static int CONF_FALSE = 1000;

    public static int confidence_update_time = 60*1000;
    public static int check_blockchain = 60*1000;

    public static String ipv4;
    public static int number_of_ips = 20;
    public static long sleep_time_miner = 20*1000;

    public static boolean proof_of_stake = true;
    public static int initial_work = 8;
    public static int table_block = 3; // blocos da tabela
    public static int choice_block =3; // blocos de onde são calculadas as hash
    public static int choice_good_nodes = 4;
    public static int choice_unkn_nodes = 5;
    public static int stake_timer = 120*1000;
    public static boolean im_the_staker = false;

    public static boolean temp_proof_of_work = true;
    public static ByteString staker = null;
}
