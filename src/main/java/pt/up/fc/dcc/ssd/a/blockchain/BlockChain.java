package pt.up.fc.dcc.ssd.a.blockchain;

import pt.up.fc.dcc.ssd.a.Config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChain {
    HashSet<LogType> logPool;
    Lock logPoolLock;

    BlockType lastBlock;
    LinkedList<byte[]> blockChain;
    HashSet<byte []> blocks;
    BlockType genBlock;

    BlockChain() {
        this.logPool = new HashSet<LogType>();
        logPoolLock = new ReentrantLock();
        blockChain = new LinkedList<byte[]>();
        blocks = new HashSet<>();
        genesisBlockGen();
    }

    private void genesisBlockGen() {
        genBlock = BlockBuilder.genesisBlock();
        lastBlock = genBlock;
        blockChain.addLast(genBlock.getHash().toByteArray());
        blocks.add(genBlock.getHash().toByteArray());
    }

    public void addLogToPool(LogType l) {
        logPoolLock.lock();
        if (logPool.add(l)) {
            logPoolLock.unlock();
            //TODO gossip
        } else {
            logPoolLock.unlock();
        }

    }

    LogType[] getLogsToMine() {
        LogType[] logs = new LogType[Config.maxLogs];
        int j = 0;

        for (LogType i : logPool) {
            logs[j] = i;

            ++j;

            if (j >= Config.maxLogs)
                break;
        }

        return logs;
    }

    void removeLogsFromPool(LogType[] logs) {
        for (LogType i : logs) {
            logPool.remove(i);
        }
    }

    public int getMaxIndex() {
        return lastBlock.getBlockSign().getData().getIndex();
    }

    public byte[] getLastBlockHash() {
        return lastBlock.getHash().toByteArray();
    }


    public byte[] getHashIndex(int index) {
        try {
            return blockChain.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

    }

    boolean addNewBlock(BlockType newBlock){
        byte[] hashBlock = newBlock.getHash().toByteArray();
        int index = newBlock.getBlockSign().getData().getIndex();

        if(index == this.getMaxIndex()+1){
            if(BlockBuilder.confirmBlock(blockChain.getLast(), newBlock)){
                lastBlock = newBlock;
                blocks.add(hashBlock);
                blockChain.addLast(hashBlock);
                return true;
            }
        }
        else if(index < this.getMaxIndex()){
            if(BlockBuilder.confirmBlock(blockChain.get(index-1), newBlock)){
                if(!Arrays.equals(blockChain.get(index),hashBlock)){
                    // TODO fork
                }
            }
        }
        return false;
    }

    public void newBlockAnnounc(BlockType newBlock) {
        byte[] hashBlock = newBlock.getHash().toByteArray();

        if(!blocks.contains(hashBlock)){
            if(!addNewBlock(newBlock)){
                updateBlockChain();
            }
            //TODO Gossip
        }
    }

    private void updateBlockChain() {
    }
}
