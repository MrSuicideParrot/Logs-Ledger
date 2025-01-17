package pt.up.fc.dcc.ssd.a.p2p;

import java.util.*;

import com.google.protobuf.ByteString;
import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.utils.ArrayTools;

import java.security.SecureRandom;

public class ConfidenceBuckets implements Iterable<Node>{
    Bucket[] buckets;
    SecureRandom random;

    ConfidenceBuckets(){
        buckets = new Bucket[Config.nBuckets];

       random = new SecureRandom((new Long(System.currentTimeMillis())).toString().getBytes());

        for(int i = 0; i < Config.nBuckets; ++i){
            buckets[i] = new Bucket();
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new ConfidenceUpdate(this),  Config.confidence_update_time, Config.confidence_update_time);
    }

    void addP2PNode(Node newNode){
        int bucketIndex;

        do {
            bucketIndex = random.nextInt(Config.nBuckets);
        }while (!buckets[bucketIndex].tryLock());
        buckets[bucketIndex].add(newNode);
        newNode.setBucketIndex(bucketIndex);
        buckets[bucketIndex].unlock();
    }

    @Override
    public Iterator<Node> iterator() {
        Set<Node> allNodes = new HashSet<>();

        for (Bucket i : buckets){
            allNodes.addAll(i.getAllNodes());
        }
        return allNodes.iterator();
    }

    List<Node> getConfidenceNodes() {
        return getConfidenceNodes(1);
    }

    List<Node> getConfidenceNodes(int n) {
        List<Node> nodesConf = new LinkedList<>();

        for (int i=0; i <Config.nBuckets; ++i){
            if(!buckets[i].isEmpty()) {
                nodesConf.addAll(buckets[i].getBestNode(n));
            }
        }

        return (List<Node>) ArrayTools.shuffleList(nodesConf);
    }

    public void remove(Node node) {
        buckets[node.getBucketIndex()].lock();
        buckets[node.getBucketIndex()].remove(node);
        buckets[node.getBucketIndex()].unlock();
    }


}
