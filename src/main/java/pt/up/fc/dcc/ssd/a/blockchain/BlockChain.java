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
    private Timer updateTimer;

    private BlockchainUpdate blockUpdater;

    private static final Logger logger = Logger.getLogger(BlockChain.class.getName());

    public BlockChain() {
        this.logPool = new HashSet<LogType>();
        logPoolLock = new ReentrantLock();

        blockChain = new LinkedList<>();
        blockChainLock = new ReentrantLock();
        blockOwnsership = new HashMap<>();

        genesisBlockGen();
        random = new SecureRandom();

        Timer updateTimer = new Timer();

        blockUpdater = new BlockchainUpdate(this);
        updateTimer.scheduleAtFixedRate( blockUpdater,  Config.check_blockchain, Config.check_blockchain);

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

                if(getMaxIndex() >= Config.initial_work && Config.proof_of_stake)
                    generateNextStaker();

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
            int resp = checkBlock(i, nC,3);

            if(resp == 0)
                return i;
            else
                if(resp == -1) // Caso falhe
                    return -1;
        }

        return 0;
    }

    int checkBlock(int index){
        List<Node> nC = network.getConfidenceNodes();
        return checkBlock(index, nC, 3);
    }

    int checkBlock(int index, List<Node>nC, int checkNum){
        /**
         * 1 tudo ok
         * 0 falhou checke
         * -1 não houve nos
         */

        int resp = 0;
        int fail = 0;

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
                valuer.changeMistrust(Config.CONFIRM_FAIL);
                fail++;
            }

        }
        if(fail == checkNum){
            lastRepuCheckedBlock -= 5;

            if(lastRepuCheckedBlock < 0)
                lastRepuCheckedBlock = 0;

            return -1;
        }

        if(resp>0){
            lastRepuCheckedBlock = candidate.getBlockSign().getData().getIndex();
            return 1;
        }
        else if(resp<0){
            return 0;
        }
        else{
            // Empate
            return 1;
        }
    }

    public int findAndResolveBlockForkMaxLength(){
        List<Node> nC = network.getConfidenceNodes(false);

        int maxValue = this.getMaxIndex();
        List<Node> maxList = new LinkedList<>();

        // Obter o maximo fork da blockchain
        for(Node i : nC){
            try {
                int val = i.getMaxBlockIndex();
                if(val >= maxValue){
                    if(val == maxValue) {
                        maxList.add(i);
                    }
                    else{
                        maxValue = val;
                        maxList.clear();
                        maxList.add(i);
                    }
                }

            }
            catch (StatusRuntimeException e){
                logger.warning("Failing contacting node");
            }
        }

        logger.info("Max finded: " + maxValue);
        // Teste de confirmação
        if(maxList.size() == 0){
            logger.severe("Failing creating max");
            return 0;
        }

        LinkedList<ByteString> blocks = new LinkedList<>();

        //Verificar se todos tem o mesmo fork ou diferentes
        for (Node i : maxList) {
            try {
                blocks.add(i.getHashBlockByIndex(maxValue));
            }
            catch (StatusRuntimeException e){
                logger.warning("Failing contacting node");
                blocks.add(null);
            }
        }

        ByteString mostHash = (ByteString) ArrayTools.mode(blocks.toArray());
        LinkedList<Node> approvedNodes = new LinkedList<>();

        for (int i = 0; i < blocks.size(); ++i) {
            if (mostHash.equals(blocks.get(i))) {
                approvedNodes.add(maxList.get(i));
            }
        }

        int index = lastRepuCheckedBlock;

        if(checkBlock(index, approvedNodes, 3) == 1){
            // aumenta o index ate acabar ou descobrir index
            ++index;
            while (index <= getMaxIndex() && checkBlock(index, approvedNodes, 3) == 1){
                ++index;
            }
        }
        else{
            --index;
            while (index > 0){
                int resul = checkBlock(index, approvedNodes, 3);
                if(resul == 1)
                    break;

                if(resul != 0){
                    logger.severe("Failing sync");
                    return 0;
                }
                --index;
            }
            index++;
        }


        blockChainLock.lock();
        try {
            logger.info("Removing block "+index);
            // TODO codigo trolha melhorar
            while (true) {
                this.removeBlock(index);
            }
        } catch (IndexOutOfBoundsException e) {

        }

        lastBlock = blockChain.getLast();
        int in = lastBlock.getBlockSign().getData().getIndex();

        if(in < lastRepuCheckedBlock){
            lastRepuCheckedBlock = in;
        }

        //Verificar se saimos da proof of stake
        if(in < Config.initial_work){
            Config.temp_proof_of_work = true;
        }

        if(in >= Config.initial_work && Config.proof_of_stake){
            generateNextStaker();
        }

        blockChainLock.unlock();

        return updateBlockChain(approvedNodes);

    }

    public int findAndResolveBlockChainFork(int index) {
        /*
        Error code
        0 tudo ok
        -1 badblock  = -1
        -2 afinal o checkblock deu bem
        -3 erro la de cima
         */
        int badBlock = -1;

        for (int i = index; i >= 0; --i) {
            if (checkBlock(i) == 1) {
                badBlock = i + 1;
                break;
            }
        }

        if(badBlock == -1){
            return -1;
        }

        if(badBlock > index){
            return -2;
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
                i.changeMistrust(Config.CONFIRM_FAIL);
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

            //Verificar se saimos da proof of stake
            if(in < Config.initial_work){
                Config.temp_proof_of_work = true;
            }

            blockChainLock.unlock();

           return updateBlockChain(approvedNodes);

        }

        return 0;
    }


    public void updateBlockChain(){
        List<Node> n = network.getConfidenceNodes();

        if(n.size() > 0)
            updateBlockChain(n);

    }

    int updateBlockChain(List<Node> n) {
        /* code -4 - Block rejeitado
            code -5 - A rede morreu
         */

        if(getMaxIndex()>=Config.initial_work && Config.proof_of_stake){
            generateNextStaker();
        }

        if(n.size() == 0){
            return 0;
        }

        Integer[] result = new Integer[n.size()];
        //LinkedList<Integer> result = new LinkedList<>();

        for (int i=0; i < n.size(); ++i){
            try {
                result[i] = n.get(i).getMaxBlockIndex();
            }
            catch (StatusRuntimeException e){
                result[i] = null;
                n.get(i).changeMistrust(Config.CONFIRM_FAIL);
            }
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
        if(mode > this.getMaxIndex()) {
            for (int i = this.getMaxIndex() + 1; i <= mode; ++i) {

                int failCount = 0;
                do {
                    Node contact;

                    if (!nodeCI.hasNext())
                        nodeCI = nodeC.iterator();

                    contact = nodeCI.next();

                    BlockType candidate = null;

                    try {
                        candidate = contact.getBlockByIndex(i);
                    } catch (StatusRuntimeException e) {
                        logger.warning("Failing contacting node");
                        logger.warning(ArrayTools.bytesToHex(contact.getId()));
                        logger.warning(i +" " + e.toString());
                    }

                    if (candidate != null) {
                        if (addNewBlock(candidate, contact)) {
                            break;
                        }
                        else {
                            logger.warning(ArrayTools.bytesToHex(contact.getId()));
                            logger.warning("Index " +i );
                            logger.severe("BlockChain update failed");
                        }
                    }
                    /*
                    Falhou no contacto do bloco vamos tentar com outro nos se nao conseguirmos  falha
                     */
                    ++failCount;
                }
                while (failCount < 3);

                if(failCount >= 3){
                    // Falhou no contacto do nó
                    return -5;
                }
            }
        }

        return 0;
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
        try {
            return blockChain.get(index);
        }
        catch (IndexOutOfBoundsException e){
            return null;
        }

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
            logger.info("I'm the staker");
            Config.im_the_staker = true;
        }
        else{
            Config.im_the_staker = false;
        }
    }

    public void setStakerTimer(long timestamp, final int index){
        try {
            stakeTimer.cancel();
            stakeTimer = new Timer();
        }
        catch (IllegalStateException e){
            // Caso nao exista nenhum timer na queue
        }

        if(timestamp + Config.stake_timer > (System.currentTimeMillis() / 1000L) ){

            Date date = new Date(timestamp*1000L + Config.stake_timer);
            stakeTimer.schedule(new TimerTask() {
                private final Logger logger = Logger.getLogger(MinerWorker.class.getName());
                @Override
                public void run() {
                    Config.temp_proof_of_work = true;
                    Config.im_the_staker = false;
                    logger.info("Next block is with proof of work -> "+index);
                }
            }, date);
        }
        else{
            Config.temp_proof_of_work = true;
        }
    }
}
