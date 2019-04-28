package pt.up.fc.dcc.ssd.a.p2p;

import pt.up.fc.dcc.ssd.a.blockchain.BlockType;
import pt.up.fc.dcc.ssd.a.blockchain.LogType;

import java.util.HashMap;

public class Network {

    ConfidenceBuckets conf;

    public Network(){
        conf = new ConfidenceBuckets();
    }

    public void gossipLog(LogType log){
        for(Node i : conf){
            i.newLog(log);
        }
    }

    public void gossipBlock(BlockType block){
        for(Node i : conf){
            i.newBlock(block);
        }
    }

    public boolean verifyNewBlock(BlockType newBlock){
        // TODO create verify block
        return false;
    }

    public void initializeNodes(HashMap<byte[], String> peers){
        for (byte[] i:
             peers.keySet()) {
            conf.addP2PNode(new Node(i, peers.get(i)));
        }
    }
}

