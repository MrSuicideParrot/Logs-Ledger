package pt.up.fc.dcc.ssd.a.blockchain;

import pt.up.fc.dcc.ssd.a.Config;

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
        BlockBuilder bl = new BlockBuilder();

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

    LogType[] getLogsToMine(){
        LogType[] logs = new LogType[Config.maxLogs];
        int j = 0;

        for(LogType i : logPool){
            logs[j] = i;

            ++j;

            if(j>= Config.maxLogs)
                break;
        }

        return logs;
    }

    void removeLogsFromPool(LogType[] logs){
        for(LogType i : logs){
            logPool.remove(i);
        }
    }

    public int getMaxIndex(){
        return lastBlock.getBlockSign().getData().getIndex();
    }

    public byte[] getLastBlockHash(){
        return lastBlock.getHash().toByteArray();
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
