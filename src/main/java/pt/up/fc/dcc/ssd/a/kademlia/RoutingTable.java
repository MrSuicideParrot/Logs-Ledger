package pt.up.fc.dcc.ssd.a.kademlia;

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

}
