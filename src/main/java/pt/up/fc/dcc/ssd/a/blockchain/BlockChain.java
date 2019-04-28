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
    LinkedList<BlockType> blockChain;
    HashSet<byte []> blocks;
    BlockType genBlock;

    public BlockChain() {
        this.logPool = new HashSet<LogType>();
        logPoolLock = new ReentrantLock();
        blockChain = new LinkedList<>();
        blocks = new HashSet<>();
        genesisBlockGen();
    }

    private void genesisBlockGen() {
        genBlock = BlockBuilder.genesisBlock();
        lastBlock = genBlock;
        blockChain.addLast(genBlock);
        blocks.add(genBlock.getHash().toByteArray());
    }

    public boolean addLogToPool(LogType l) {
        logPoolLock.lock();
        if (logPool.add(l)) {
            logPoolLock.unlock();
            return true;
        } else {
            logPoolLock.unlock();
            return false;
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
            return blockChain.get(index).getHash().toByteArray();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

    }

    public boolean addNewBlock(BlockType newBlock){
        byte[] hashBlock = newBlock.getHash().toByteArray();
        int index = newBlock.getBlockSign().getData().getIndex();

        if(index == this.getMaxIndex()+1){
            if(BlockBuilder.confirmBlock(blockChain.getLast().getHash().toByteArray(), newBlock)){
                lastBlock = newBlock;
                blocks.add(hashBlock);
                blockChain.addLast(newBlock);
                return true;
            }
        }
        else if(index < this.getMaxIndex()){
            if(BlockBuilder.confirmBlock(blockChain.get(index-1).getHash().toByteArray(), newBlock)){
                if(!Arrays.equals(blockChain.get(index).getHash().toByteArray(),hashBlock)){
                    // TODO fork na blockchain
                }
            }
        }
        return false;
    }

    /*
    public boolean newBlockAnnounc(BlockType newBlock) {
        byte[] hashBlock = newBlock.getHash().toByteArray();

        if(!blocks.contains(hashBlock)){
            if(!addNewBlock(newBlock)){
                updateBlockChain();
            }
            //TODO Gossip
        }
    }
*/
    public boolean contains(byte[] hash){
        return blocks.contains(hash);

    }

    void updateBlockChain() {

    }

    public BlockType getBlock(int index) {
        return blockChain.get(index);
    }
}