package pt.up.fc.dcc.ssd.a.tracker;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.up.fc.dcc.ssd.a.utils.IPGetter;

import java.io.IOException;
import java.util.logging.Logger;

public class Tracker {
    final static int port = 34832;
    static String myIP;

    //private final Server server;

    public Tracker(){
        myIP = IPGetter.getIP();

        //server = ServerBuilder.addService(new TrackerService()).build();
    }

    /*public void start() throws IOException {
        server.start();
        Logger.info("Server started, listening on " + port);
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
    }*/

    public static void main(String [] args) throws Exception  {
        Tracker server = new Tracker();
        System.out.println(server.myIP);
        //server.start();
        //server.blockUntilShutdown();

    }


}