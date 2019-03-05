package pt.up.fc.dcc.ssd.a.kademlia;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

class KBucket extends RoutingNode{
    private Queue<NodeP2P> bucket;

    KBucket(int depth, int bit){
        super(depth, bit);
        bucket = new ArrayBlockingQueue<NodeP2P>(Config.k);
    }


    boolean add(NodeP2P newNodeP2P){
        //TODO
        return false;
    }

    boolean isEmpty(){
        //TODO
        return false;
    }

    RoutingNode addP2PNode(NodeP2P newNode) {
        try {
            bucket.add(newNode);
            return this;
        }
        catch (IllegalStateException e){
            RoutingNode newRNode = new MiddleNode();

            for (NodeP2P i : bucket){
                newRNode = newRNode.addP2PNode(i);
            }

            return newRNode;
        }
    }
}
