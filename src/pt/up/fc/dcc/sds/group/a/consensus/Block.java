package pt.up.fc.dcc.sds.group.a.consensus;

import java.time.Instant;

public abstract class Block {
    private Block parent;
    private long timeSinceEpoch;
    private byte[] data;

    Block(Block parent, byte[] data){
        timeSinceEpoch = Instant.now().getEpochSecond();
        this.parent = parent;
        this.data = data;

    }

    byte[] getParentHash(){
        return parent.getHash();
    }

    byte[] getData(){
        return data;
    }

    byte[] getHash(){
        return null;
    }

}
