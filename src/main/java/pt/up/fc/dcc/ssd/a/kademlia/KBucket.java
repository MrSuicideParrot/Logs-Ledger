package pt.up.fc.dcc.ssd.a.kademlia;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

class KBucket {
    private Queue<Node> bucket;

    KBucket(int distance){
        bucket = new ArrayBlockingQueue<Node>(Config.k);
    }


    boolean add(Node newNode){
        if(bucket.contains(newNode)){

        }
        else{
            try{
                bucket.add(newNode);
            }
            catch (IllegalStateException e){

            }
        }
        return true;

    }

    boolean isEmpty(){
        //TODO
        return false;
    }

}
