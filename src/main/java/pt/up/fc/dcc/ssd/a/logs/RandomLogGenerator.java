package pt.up.fc.dcc.ssd.a.logs;

import com.google.protobuf.ByteString;
import pt.up.fc.dcc.ssd.a.blockchain.LogType;
import pt.up.fc.dcc.ssd.a.p2p.Network;

import java.security.SecureRandom;
import java.util.logging.Logger;

public class RandomLogGenerator implements Runnable{
    private String[] proper_noun = {"Jane", "Richard Nixon", "Miss America"};
    private String[] common_noun = {"man", "woman", "fish", "elephant", "unicorn"};
    private String[] determiner = {"a", "the", "every", "some"};
    private String[] adjective = {"big", "tiny", "pretty", "bald"};
    private String[] transitive_verb = {"loves", "hates", "sees", "knows", "looks for", "finds"};

    private SecureRandom rand = new SecureRandom();

    private static final Logger logger = Logger.getLogger(RandomLogGenerator.class.getName());

    public int min;
    public int max;
    private Network net;

    public RandomLogGenerator(){
        this.min = 5000;
        this.max = 30000;
    }

    public RandomLogGenerator(int min, int max, Network net){
        this.min = min*1000;
        this.max = max*1000;
        this.net = net;
    }

    @Override
    public void run(){
        int sleepTime;
        while (true){
            try {
                sleepTime = rand.nextInt(max-min+1)+min;
                String log = proper_noun[rand.nextInt(proper_noun.length)] + " " + transitive_verb[rand.nextInt(transitive_verb.length)] + " " + determiner[rand.nextInt(determiner.length)] + " " + adjective[rand.nextInt(adjective.length)] + " " + common_noun[rand.nextInt(common_noun.length)];
                LogType.Builder builder = LogType.newBuilder();
                LogType.LogData.Builder data_builder = builder.getDataBuilder();
                data_builder.setData(ByteString.copyFrom(log.getBytes()));
                data_builder.setTimestamp(System.currentTimeMillis() / 1000L);

                net.gossipLog(builder.build());

                logger.info("Created log: \"" + log + "\"");
                logger.info("Next log will be created in "+ (float)sleepTime/1000 + "seconds.");
                Thread.sleep(sleepTime);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
