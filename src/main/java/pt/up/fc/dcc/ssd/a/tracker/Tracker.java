package pt.up.fc.dcc.ssd.a.tracker;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.up.fc.dcc.ssd.a.utils.Challenge;
import pt.up.fc.dcc.ssd.a.utils.ChallengeResponse;
import pt.up.fc.dcc.ssd.a.utils.IPGetter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

public class Tracker {
    final static int zeros = 20;
    final static int port = 34833;
    private String myIP;
    private ServerBuilder serverBuilder;
    private Server server;

    private static HashMap nodeIdMap = new HashMap<byte[],String>();

    private static final Logger logger = Logger.getLogger(Tracker.class.getName());

    Tracker(){
        myIP = IPGetter.getIP();

        serverBuilder = ServerBuilder.forPort(port);
    }

    public static ChallengeResponse isIdValid(byte[] id, String ip){
        if((Challenge.countZeros(Challenge.genHash(id)) == zeros) && (!nodeIdMap.containsKey(id))){
            ArrayList<byte[]> keys = new ArrayList<byte[]>(nodeIdMap.keySet());
            HashMap<byte[],String> nodes = new HashMap<>();
            SecureRandom r = new SecureRandom();
            byte[] next = null;
            while(nodes.size() < 20 && nodes.size() < nodeIdMap.size()){
                next = keys.get(r.nextInt(keys.size()));
                nodes.put(next, (String)nodeIdMap.get(next));
            }
            nodeIdMap.put(id,ip);
            System.out.println(nodeIdMap);
            return new ChallengeResponse(true, nodes);
        }
        return new ChallengeResponse(false);
    }

    public void start() throws IOException {
        server = serverBuilder.addService(new TrackerService(zeros)).build();
        server.start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may has been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                Tracker.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String [] args) throws Exception  {
        Tracker server = new Tracker();
        System.out.println(server.myIP);
        server.start();
        server.blockUntilShutdown();

    }


}