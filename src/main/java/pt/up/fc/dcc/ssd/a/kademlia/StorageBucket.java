package pt.up.fc.dcc.ssd.a.kademlia;

import java.nio.ByteBuffer;
import java.util.Arrays;

class StorageBucket {
    private byte[] key;
    private byte[] data;

    StorageBucket(byte[] key, byte[] data){
        this.key = key;
        this.data = data;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        return Arrays.equals(this.key,((StorageBucket)o).key);
    }

    @Override
    public int hashCode() {
        return ByteBuffer.wrap(this.key).getInt();
    }
}
