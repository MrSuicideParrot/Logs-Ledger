package pt.up.fc.dcc.ssd.a.p2p;

import java.util.*;

import pt.up.fc.dcc.ssd.a.Config;

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

    Node[] getConfidenceNodes() {
        Node[] nodesConf = new Node[Config.nBuckets];

        for (int i=0; i <Config.nBuckets; ++i){
            nodesConf[i] = buckets[i].getBestNode();
        }

        return nodesConf;
    }
}
