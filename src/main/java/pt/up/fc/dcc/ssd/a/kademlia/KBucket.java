package pt.up.fc.dcc.ssd.a.kademlia;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

class KBucket {
    private LinkedList<Node> bucket;

    KBucket(int distance){
        bucket = new LinkedList<Node>();
    }


    boolean add(Node newNode){
        int i;
        if((i = bucket.indexOf(newNode)) != -1){
            Node node = bucket.remove(i);
            node.seenNow();
            bucket.addLast(node);
        }
        else{
            if(bucket.size() < Config.k) { //TODO Confirmar se é isto
                newNode.seenNow();
                bucket.addLast(newNode);
            }
            else{
                // TODO pingar primeiro e se não mete  lo fora
            }
        }
        return true;

    }

    LinkedList<Node> getBucket(){
        return bucket;
    }

    boolean isEmpty(){
        //TODO
        return false;
    }

}
