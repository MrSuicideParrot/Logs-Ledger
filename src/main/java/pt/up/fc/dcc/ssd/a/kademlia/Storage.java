package pt.up.fc.dcc.ssd.a.kademlia;

import java.util.HashSet;

class Storage {
    private HashSet<StorageBucket> buckets;

    Storage(){
        buckets = new HashSet<StorageBucket>();
    }

    void add(byte[] key, byte[] data){
        StorageBucket newData = new StorageBucket(key,data);
        if(!buckets.add(newData)){
            buckets.remove(key);
            buckets.add(newData);
        }
    }

    void remove(byte[] key){
        buckets.remove(key);
    }
}
