package pt.up.fc.dcc.ssd.a.p2p;

import java.util.Collections;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Bucket {
    private SortedSet<Node> bucket;
    private HashMap<Node, Long> updateMap;

    Lock lock;

    Bucket() {
        bucket = Collections.synchronizedSortedSet(new TreeSet());
        updateMap = new HashMap<>();
        lock = new ReentrantLock();
    }


    void add(Node newNode) {
        bucket.add(newNode);
    }

    boolean remove(Node node){
        return bucket.remove(node);
    }

    SortedSet<Node> getBucket() {
        return bucket;
    }

    boolean isEmpty() {
      return bucket.isEmpty();
    }

    void lock() {
        lock.lock();
    }

    boolean tryLock() {
        return lock.tryLock();
    }

    void unlock() {
        lock.unlock();
    }

    Node getBestNode(){
        return bucket.first();
    }


    void updateMistrust(){
        lock.lock();
        bucket = Collections.synchronizedSortedSet(new TreeSet(bucket));
        lock.unlock();
    }



}
