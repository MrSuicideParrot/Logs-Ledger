package pt.up.fc.dcc.ssd.a.p2p;

import pt.up.fc.dcc.ssd.a.blockchain.BlockType;
import pt.up.fc.dcc.ssd.a.blockchain.LogType;

public class Gossip implements Runnable {
    private final Network n;
    private final LogType log;
    private final BlockType block;

    public Gossip(Network n, LogType log){
        this.n = n;
        this.log = log;
        this.block = null;
    }
    
    public Gossip(Network n, BlockType block){
        this.n = n;
        this.log = null;
        this.block = block;
    }
    
    
    @Override
    public void run() {
        if(block == null)
            n.gossipLog(log);
        else    
            n.gossipBlock(block);
    }
}
