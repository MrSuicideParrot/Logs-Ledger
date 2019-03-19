package pt.up.fc.dcc.ssd.a.kademlia;

import java.util.HashSet;
import java.util.LinkedList;

public class RoutingTable {
    KBucket[] buckets;

    RoutingTable(){
        buckets = new KBucket[Config.id_length];

        for(int i = 0; i < Config.id_length; ++i){
            buckets[i] = new KBucket(i);
        }
    }

    void addP2PNode(Node newNode){
        int bucketIndex = getBucketIndex(newNode.getId());
        buckets[bucketIndex].add(newNode);
    }

    int getBucketIndex(byte[] insertID){
        return Node.getDistanceID(Config.myID, insertID);
    }

    LinkedList<Node> getClosestContact(byte[] target, HashSet<Node> known){
        int index = Node.getDistanceID(target);

        /* Lista por distancia do target */
        LinkedList<Integer> closerOrder = new LinkedList<Integer>();
        {
            int i = index - 1;
            int j = index + 1;

            while (closerOrder.size() <= Config.id_length) {
                if (j < Config.id_length) {
                    closerOrder.addLast(j);    /*TODO Confirmar se não estou sempre a copiar apontador */
                }

                if (i >= 0) {
                    closerOrder.addLast(i);
                }

                --i;
                ++j;
            }
        }

        int leftToAdd = Config.alpha;

        LinkedList<Node> nos = new LinkedList<Node>();

        for(Integer i : closerOrder){
            LinkedList<Node> bucketX = buckets[i.intValue()].getBucket();
            for(Node n : bucketX){
                if(!known.contains(n)){
                    nos.addLast(n);
                    --leftToAdd;
                    if(leftToAdd == 0)
                        return nos;
                }
            }
        }

        return nos;
    }



}
