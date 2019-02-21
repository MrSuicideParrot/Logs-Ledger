package pt.up.fc.dcc.sds.group.a.consensus;

import pt.up.fc.dcc.sds.group.a.tools.Sha256;

import java.nio.ByteBuffer;
import java.time.Instant;

public abstract class Block {
    private Block parent;
    private long timeSinceEpoch;
    private byte[] data;
    private int index;

    Block(Block parent, byte[] data){
        timeSinceEpoch = Instant.now().getEpochSecond();
        this.parent = parent;
        this.index = this.parent.getIndex() + 1;
        this.data = data;

    }

    int getIndex(){
        return index;
    }

    byte[] getParentHash(){
        return parent.getHash();
    }

    byte[] getData(){
        return data;
    }

    byte[] getHash(){
        return Sha256.hash(this.toString().getBytes());
    }

    byte[] packBlock(){
        //TODO: https://stackoverflow.com/questions/3209898/java-equivalent-of-pythons-struct-pack
        //ByteBuffer block = ByteBuffer.allocate();
        return null;

    }

    static Block unpackBlock(byte[] array){
        //TODO: https://stackoverflow.com/questions/3209898/java-equivalent-of-pythons-struct-pack
        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
