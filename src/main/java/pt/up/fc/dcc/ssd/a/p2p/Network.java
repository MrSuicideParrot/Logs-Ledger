package pt.up.fc.dcc.ssd.a.p2p;

import pt.up.fc.dcc.ssd.a.blockchain.BlockType;
import pt.up.fc.dcc.ssd.a.blockchain.LogType;

public class Network {

    ConfidenceBuckets conf;

    Network(){
        conf = new ConfidenceBuckets();
    }

    void gossipLog(LogType log){
        for(Node i : conf){
            i.newLog(log);
        }
    }

    void gossipBlock(BlockType block){
        for(Node i : conf){
            i.newBlock(block);
        }
    }

}

