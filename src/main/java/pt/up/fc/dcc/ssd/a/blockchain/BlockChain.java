package pt.up.fc.dcc.ssd.a.blockchain;

import com.google.protobuf.ByteString;
import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.p2p.Network;
import pt.up.fc.dcc.ssd.a.p2p.Node;
import pt.up.fc.dcc.ssd.a.utils.ArrayTools;
import pt.up.fc.dcc.ssd.a.utils.CriptoTools;


import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class BlockChain {
    HashSet<LogType> logPool;
    Lock logPoolLock;

    BlockType lastBlock;

    LinkedList<BlockType> blockChain;
    HashSet<byte []> blocks;
    Lock blockChainLock;

    BlockType genBlock;
    Network network;
    SecureRandom random;

    int lastRepuCheckedBlock;

    private static final Logger logger = Logger.getLogger(BlockChain.class.getName());

    public BlockChain(Network network) {
        this.logPool = new HashSet<LogType>();
        logPoolLock = new ReentrantLock();

        blockChain = new LinkedList<>();
        blocks = new HashSet<>();
        blockChainLock = new ReentrantLock();

        genesisBlockGen();
        this.network = network;
        random = new SecureRandom();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new BlockchainUpdate(this),  Config.check_blockchain, Config.check_blockchain);
        logger.info("BlockChain initialized");
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
            logger.info("Log added to log pool");
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

        blockChainLock.lock();
        if(index == this.getMaxIndex()+1){
            if(BlockBuilder.confirmBlock(blockChain.getLast().getHash().toByteArray(), newBlock)){
                lastBlock = newBlock;
                blocks.add(hashBlock);
                blockChain.addLast(newBlock);
                blockChainLock.unlock();
                logger.info("New block added to blockchain");
                return true;
            }
        }
        blockChainLock.unlock();
        /*else {
            blockChainLock.unlock();
            if (index < this.getMaxIndex()) {
                if (BlockBuilder.confirmBlock(blockChain.get(index - 1).getHash().toByteArray(), newBlock)) {
                    if (!Arrays.equals(blockChain.get(index).getHash().toByteArray(), hashBlock)) {
                        // TODO fork na blockchain
                    }
                }
            }
        }*/
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

    int checkBlockChain(){
        int initial = lastRepuCheckedBlock;

        return checkBlockChain(initial);
    }

    int checkBlockChain(int initial) {
        Node[] nC = network.getConfidenceNodes();


        for(int i = initial; i <= this.getMaxIndex();  ++i ){
            if(!checkBlock(i, nC,2)){
                return i;
            }

        }

        return 0;
    }

    boolean checkBlock(int index){
        Node[] nC = network.getConfidenceNodes();
        return checkBlock(index, nC, 2);
    }

    boolean checkBlock(int index, Node[] nC, int checkNum){
        Node valuer =  nC[random.nextInt(nC.length)];

        boolean resp = true;

        for (int i = 0 ; i < checkNum; ++i) {
            BlockType candidate = getBlock(index);
            ByteString hash = valuer.getHashBlockByIndex(index);
            resp = resp & hash.equals(candidate.getHash());
        }
        return resp;
    }

    void findAndResolveBlockChainFork(int index){
        if(index>1) {
            int badBlock = -1;

            for (int i = index; i > 0; --i) {
               if(checkBlock(i)){
                   badBlock = i + 1;
                   break;
               }
            }

            /*
            Identificada a localização do fork
            Vamos seguir a blockchain que  tenha mais nos a seguir a ele
            Em caso de empate escolhemos a que esteja em maior numero nos nos de confiança - TODO Fazer numero de nós impares
             */

            Node[] nC = network.getConfidenceNodes();
            LinkedList<ByteString> blocks = new LinkedList<>();

            for(Node i : nC){
                blocks.add(i.getHashBlockByIndex(badBlock));
            }

            ByteString mostHash = (ByteString)ArrayTools.mode(blocks.toArray());
            LinkedList<Node> approvedNodes = new LinkedList<>();

            if(!mostHash.equals(blockChain.get(badBlock))){
                for(int i= 0; i < blocks.size(); ++i){
                    if(mostHash.equals(blocks.get(i))){
                        approvedNodes.add(nC[i]);
                    }
                }

                blockChainLock.lock();
                try {
                    // TODO codigo trolha melhorar
                    while (true){
                        blocks.remove(blockChain.remove(badBlock));
                    }
                }
                catch (IndexOutOfBoundsException e){

                }
                blockChainLock.unlock();

                updateBlockChain((Node[]) approvedNodes.toArray());

            }
        }
        else{
            //TODO caso seja no inicio
        }

    }

    void updateBlockChain(){
        Node[] n = network.getConfidenceNodes();
        updateBlockChain(n);

    }

    void updateBlockChain(Node[] n) {

        Integer[] result = new Integer[Config.nBuckets];

        for (int i=0; i < Config.nBuckets; ++i){
            result[i] = n[i].getMaxBlockIndex();
        }

        Integer mode = (Integer) ArrayTools.mode(result);

        /*
         TODO Talvez retirar confiança
         */
        Set<Node> nodeC = new HashSet<>();
        for (int i = 0; i <Config.nBuckets ; i++) {
            if(result[i] == mode)
                nodeC.add(n[i]);
        }


        Iterator<Node> nodeCI = nodeC.iterator();
        if(mode > this.getMaxIndex()){
            for(int i = this.getMaxIndex()+1; i <= mode ; ++i){
                Node contact;

                if(!nodeCI.hasNext())
                    nodeCI = nodeC.iterator();

                contact = nodeCI.next();

                BlockType candidate = contact.getBlockByIndex(i);
                if(!addNewBlock(candidate)){
                    //Correu algo mal
                }

            }

        }
    }

    public BlockType getBlock(int index) {
        return blockChain.get(index);
    }
}
