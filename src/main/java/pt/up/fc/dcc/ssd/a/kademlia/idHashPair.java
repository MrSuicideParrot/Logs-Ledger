package pt.up.fc.dcc.ssd.a.kademlia;

public class idHashPair {
    byte id[] = new byte[160];
    byte hash[] = new byte[160];

    idHashPair(byte[] id, byte[] hash){
        this.id = id;
        this.hash = hash;
    }
}
