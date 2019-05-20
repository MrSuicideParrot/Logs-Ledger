package pt.up.fc.dcc.ssd.a.blockchain;

import pt.up.fc.dcc.ssd.a.utils.ArrayTools;
import pt.up.fc.dcc.ssd.a.utils.CriptoTools;

import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class BlockchainUpdate extends TimerTask {
    BlockChain blockChain;

    //private Lock lock;

    private static final Logger logger = Logger.getLogger(BlockchainUpdate.class.getName());

    BlockchainUpdate(BlockChain blockChain){
        this.blockChain = blockChain;
        //lock = new ReentrantLock();
    }

    @Override
    public void run() {
        blockCheck2();
    }

    public void blockCheck2(){
       // blockChain.miner.lock.lock();
        //lock.lock();

        logger.info("Starting blockchain check");

        printBlockchain();

        blockChain.findAndResolveBlockForkMaxLength();

        logger.info("Success");
        printBlockchain();

        //lock.unlock();
      //  blockChain.miner.lock.unlock();
    }

    public void blockCheck1() {
       // lock.lock();
        logger.info("Starting blockchain check");

        printBlockchain();

        int check = blockChain.checkBlockChain();

        if(check > 0){
            logger.warning("Fork detected - index: "+check);
            int status = -3;

            try {
                status = blockChain.findAndResolveBlockChainFork(check);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            if(status == 0){
                logger.info("Fork resolved");
                printBlockchain();
            }
            else{
                logger.severe("Fork resolv failed with code: "+status);
            }

        }
        else {
            if(check == 0) {
                blockChain.updateBlockChain(); // Caso fique enpancado num caminho certoz
                logger.info("It's all ok");
            }
            else{
                logger.warning("Possible fail");
            }
        }
       // lock.unlock();
    }

    public void printBlockchain() {
        for (BlockType i:
             this.blockChain.blockChain) {
            System.out.println("Block "+i.getBlockSign().getData().getIndex() +" "+ ArrayTools.bytesToHex(CriptoTools.hash(i.toByteArray())));

        }
    }
}
