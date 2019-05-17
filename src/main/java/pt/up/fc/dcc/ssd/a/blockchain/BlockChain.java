package pt.up.fc.dcc.ssd.a.blockchain;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.p2p.Network;
import pt.up.fc.dcc.ssd.a.p2p.Node;
import pt.up.fc.dcc.ssd.a.utils.ArrayTools;
import pt.up.fc.dcc.ssd.a.utils.CriptoTools;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    HashMap<ByteString, Node> blockOwnsership;
    Lock blockChainLock;

    BlockType genBlock;
    Network network;
    SecureRandom random;

    int lastRepuCheckedBlock;

    MinerWorker miner;
    private Timer stakeTimer;

    private static final Logger logger = Logger.getLogger(BlockChain.class.getName());

    public BlockChain() {
        this.logPool = new HashSet<LogType>();
        logPoolLock = new ReentrantLock();

        blockChain = new LinkedList<>();
        blockChainLock = new ReentrantLock();
        blockOwnsership = new HashMap<>();

        genesisBlockGen();
        random = new SecureRandom();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new BlockchainUpdate(this),  Config.check_blockchain, Config.check_blockchain);

        stakeTimer = new Timer();

        logger.info("BlockChain initialized");
    }

    private void genesisBlockGen() {
        genBlock = BlockBuilder.genesisBlock();
        lastBlock = genBlock;
        blockChain.addLast(genBlock);
        blockOwnsership.put(genBlock.getHash(),null);
        System.out.println(ArrayTools.bytesToHex(genBlock.getHash()));
    }

    public boolean addLogToPool(LogType l) {
        logPoolLock.lock();
        if (logPool.add(l)) {
            logPoolLock.unlock();
            //logger.info("Log added to log pool: "+ ArrayTools.bytesToHex(CriptoTools.hash(l.toByteArray())));
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
        logPoolLock.lock();
        for (LogType i : logs) {
            logPool.remove(i);
        }
        logPoolLock.unlock();
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

    public boolean addNewBlock(BlockType newBlock, Node owner){
        ByteString hashBlock = newBlock.getHash();
        int index = newBlock.getBlockSign().getData().getIndex();

        blockChainLock.lock();
        if(index == this.getMaxIndex()+1){ /* caso o bloco seja o proximo */
            if(BlockBuilder.confirmBlock(blockChain.getLast().getHash().toByteArray(), newBlock, this)){

                lastBlock = newBlock;
                blockChain.addLast(newBlock);
                blockOwnsership.put(hashBlock, owner);

                blockChainLock.unlock();
                logger.info("New block added to blockchain: "+  ArrayTools.bytesToHex(CriptoTools.hash(newBlock.toByteArray())));

                if(owner != null)
                    owner.changeMistrust(Config.SPREAD_BLOCK_POINT);

                return true;
            }
            else {
                if(owner != null)
                    owner.changeMistrust(Config.REJECTED_BLOCK);

                blockChainLock.unlock();
                return false;
            }
        }


        blockChainLock.unlock();

        if(index < this.getMaxIndex()){
            if (BlockBuilder.confirmBlock(blockChain.get(index - 1).getHash().toByteArray(), newBlock, this)) {
                if(owner != null)
                    owner.changeMistrust(Config.CONFIRM_BLOCK);
            }
            else {
                if(owner != null)
                    owner.changeMistrust(Config.REJECTED_BLOCK);
            }

        }

        return false;
    }

    public boolean contains(ByteString hash){
        return blockOwnsership.containsKey(hash);

    }

    int checkBlockChain(){
        int initial = lastRepuCheckedBlock;

        return checkBlockChain(initial);
    }

    int checkBlockChain(int initial) {
        List<Node> nC = network.getConfidenceNodes();


        for(int i = initial; i <= this.getMaxIndex();  ++i ){
            if(!checkBlock(i, nC,3)){
                return i;
            }

        }

        return 0;
    }

    boolean checkBlock(int index){
        List<Node> nC = network.getConfidenceNodes();
        return checkBlock(index, nC, 3);
    }

    boolean checkBlock(int index, List<Node>nC, int checkNum){

        int resp = 0;

        if(checkNum > nC.size()){
            checkNum = nC.size();
        }

        BlockType candidate = blockChain.getFirst();

        for (int i = 0 ; i < checkNum ; ++i) { // Verificar

            Node valuer =  (Node) ArrayTools.pickRandom(nC);

            candidate = getBlock(index);
            try {
                ByteString hash = valuer.getHashBlockByIndex(index);
                if(hash.equals(candidate.getHash())){
                    ++resp;
                }
                else {
                    --resp;
                }
            }
            catch (StatusRuntimeException e){
                logger.warning("Failing contacting node");
            }

        }

        if(resp>0){
            lastRepuCheckedBlock = candidate.getBlockSign().getData().getIndex();
            return true;
        }
        else if(resp<0){
            return false;
        }
        else{
            lastRepuCheckedBlock = candidate.getBlockSign().getData().getIndex();
            //Todo Empate
            return true;
        }
    }

    public void findAndResolveBlockChainFork(int index) {
        int badBlock = -1;

        for (int i = index; i >= 0; --i) {
            if (checkBlock(i)) {
                badBlock = i + 1;
                break;
            }
        }

            /*
            Identificada a localização do fork
            Vamos seguir a blockchain que  tenha mais nos a seguir a ele
            Em caso de empate escolhemos a que esteja em maior numero nos nos de confiança - TODO Fazer numero de nós impares
             */

        List<Node> nC = network.getConfidenceNodes();
        LinkedList<ByteString> blocks = new LinkedList<>();

        for (Node i : nC) {
            try {
                blocks.add(i.getHashBlockByIndex(badBlock));
            }
            catch (StatusRuntimeException e){
                logger.warning("Failing contacting node");
                blocks.add(null);
            }

        }

        ByteString mostHash = (ByteString) ArrayTools.mode(blocks.toArray());
        LinkedList<Node> approvedNodes = new LinkedList<>();

        if (!mostHash.equals(blockChain.get(badBlock))) {
            for (int i = 0; i < blocks.size(); ++i) {
                if (mostHash.equals(blocks.get(i))) {
                    approvedNodes.add(nC.get(i));
                }
            }

            blockChainLock.lock();
            try {
                // TODO codigo trolha melhorar
                while (true) {
                    blocks.remove(this.removeBlock(badBlock));
                }
            } catch (IndexOutOfBoundsException e) {

            }

            lastBlock = blockChain.getLast();
            int in = lastBlock.getBlockSign().getData().getIndex();

            if(in < lastRepuCheckedBlock){
                lastRepuCheckedBlock = in;
            }

            blockChainLock.unlock();

            updateBlockChain(approvedNodes);

        }
    }


    public void updateBlockChain(){
        List<Node> n = network.getConfidenceNodes();
        if(n.size() > 0)
            updateBlockChain(n);

    }

    void updateBlockChain(List<Node> n) {

        Integer[] result = new Integer[n.size()];
        //LinkedList<Integer> result = new LinkedList<>();

        for (int i=0; i < n.size(); ++i){
            result[i] = n.get(i).getMaxBlockIndex();
        }

        Integer mode = (Integer) ArrayTools.mode(result);

        /*
         TODO Talvez retirar confiança
         */
        Set<Node> nodeC = new HashSet<>();
        for (int i = 0; i <n.size() ; i++) {
            if(result[i] == mode)
                nodeC.add(n.get(i));
        }


        Iterator<Node> nodeCI = nodeC.iterator();
        if(mode > this.getMaxIndex()){
            for(int i = this.getMaxIndex()+1; i <= mode ; ++i){
                Node contact;

                if(!nodeCI.hasNext())
                    nodeCI = nodeC.iterator();

                contact = nodeCI.next();

                BlockType candidate = contact.getBlockByIndex(i);
                if(!addNewBlock(candidate, contact)){
                    logger.severe("Falhou o update BlockChain");
                }

            }

        }
    }

    BlockType removeBlock(int index){
        BlockType b = blockChain.remove(index);
        try {
            blockOwnsership.remove(b.getHash()).changeMistrust(Config.SPREAD_FALSE_BLOCK);
        }
        catch (NullPointerException e){

        }

        return b;
    }

    public BlockType getBlock(int index) {
        return blockChain.get(index);
    }

    public void setNetwork(Network net) {
        this.network = net;
        miner = new MinerWorker(this, network);
        new Thread(miner).start();
    }

    public void generateNextStaker() {
        int max = getMaxIndex();

        MessageDigest md;
        try {
             md = MessageDigest.getInstance("SHA-256");
        } catch (
                NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        for(int i = max; i > max - Config.choice_block; --i){
            md.update(getBlock(i).getHash().toByteArray());
        }

        byte[] theHash = md.digest();

        int min = Integer.MAX_VALUE;
        ByteString min_node = null;

        for(int i = max - Config.choice_block; i > max - Config.choice_block - Config.table_block ;--i){
            for(ByteString n: getBlock(i).getBlockSign().getData().getNodesList()){
                int dist = ArrayTools.bitDistance(theHash, n.toByteArray());
                if(dist < min){
                    min = dist;
                    min_node = n;
                }
            }
        }

        Config.staker = min_node;
        logger.info("Next staker ->" +  ArrayTools.bytesToHex(min_node.toByteArray()));

        if (min_node.equals(Config.myID)){
            logger.info("It's me Mario!!!");
            Config.im_the_staker = true;
        }
    }

    public void setStakerTimer(long timestamp){
        if(timestamp + Config.stake_timer < (System.currentTimeMillis() / 1000L) ){
            stakeTimer.purge();
            Date date = new Date(timestamp*1000L + Config.stake_timer);
            stakeTimer.schedule(new TimerTask() {
                private final Logger logger = Logger.getLogger(MinerWorker.class.getName());
                @Override
                public void run() {
                    Config.temp_proof_of_work = true;
                    logger.info("Next block is with proof of work");
                }
            }, date);
        }
    }
}
