package pt.up.fc.dcc.ssd.a.kademlia;

import java.util.concurrent.locks.ReentrantLock;

class TrakerAsync {

    private  int numberOfRequests;
    private ReentrantLock l;

    TrakerAsync(){
        this.numberOfRequests = 0;
        l = new ReentrantLock();
    }

    void decrement() {
        l.lock();
        --numberOfRequests;
        l.unlock();
    }

    boolean completed(){
        return numberOfRequests == 0;
    }

    void increment() {
        l.lock();
        ++numberOfRequests;
        l.unlock();
    }
}
