package pt.up.fc.dcc.ssd.a.blockchain;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChain {
    HashSet<LogType> logPool;
    Lock logPoolLock;

    BlockType lastBlock;
    LinkedList<Byte[]> blockChain;

    BlockChain(){
        this.logPool = new HashSet<LogType>();
        logPoolLock = new ReentrantLock();
        blockChain = new LinkedList<Byte[]>();
        genesisBlockGen();
    }

    private void genesisBlockGen(){
        Block bl = new Block();

    }

    public void addLogToPool(LogType l){
        logPoolLock.lock();
        if(logPool.add(l)){
            logPoolLock.unlock();
            //TODO gossip
        }
        else{
            logPoolLock.unlock();
        }

    }

    public int getMaxIndex(){
        return lastBlock.getIndex();
    }


    public Byte[] getHashIndex(int index){
        try {
            return blockChain.get(index);
        }
        catch (IndexOutOfBoundsException e){
            return null;
        }

    }

}
