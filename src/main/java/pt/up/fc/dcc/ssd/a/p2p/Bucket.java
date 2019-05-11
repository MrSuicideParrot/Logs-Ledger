package pt.up.fc.dcc.ssd.a.p2p;

import pt.up.fc.dcc.ssd.a.utils.ArrayTools;

import java.util.*;
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
        try {
            return bucket.first();
        }
        catch (NoSuchElementException e){
            return null;
        }

    }


    void updateMistrust(){
        lock.lock();
        bucket = Collections.synchronizedSortedSet(new TreeSet(bucket));
        lock.unlock();
    }

    Set<Node> getAllNodes(){
        return bucket;
    }


    public void printBucket() {
        for (Node i : bucket){
            System.out.println(ArrayTools.bytesToHex(i.getId()) + " -> " + i.getMistrust());
        }
    }
}
