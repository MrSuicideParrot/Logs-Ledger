package pt.up.fc.dcc.ssd.a.p2p;

import pt.up.fc.dcc.ssd.a.Config;

import java.security.SecureRandom;
import java.util.Iterator;

public class ConfidenceBuckets implements Iterable<Node>{
    Bucket[] buckets;
    SecureRandom random;

    ConfidenceBuckets(){
        buckets = new Bucket[Config.nBuckets];

       random = new SecureRandom((new Long(System.currentTimeMillis())).toString().getBytes());

        for(int i = 0; i < Config.nBuckets; ++i){
            buckets[i] = new Bucket();
        }
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
        return null;
    }
}