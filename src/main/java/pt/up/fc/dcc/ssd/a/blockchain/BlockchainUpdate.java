package pt.up.fc.dcc.ssd.a.blockchain;

import pt.up.fc.dcc.ssd.a.utils.ArrayTools;
import pt.up.fc.dcc.ssd.a.utils.CriptoTools;

import java.util.TimerTask;
import java.util.logging.Logger;

public class BlockchainUpdate extends TimerTask {
    BlockChain blockChain;

    private static final Logger logger = Logger.getLogger(BlockchainUpdate.class.getName());

    BlockchainUpdate(BlockChain blockChain){
        this.blockChain = blockChain;
    }

    @Override
    public void run() {
        logger.info("Starting blockchain check");

        printBlockchain();

        int check = blockChain.checkBlockChain();

        if(check != 0){
            logger.warning("Fork detected");
            try {
                blockChain.findAndResolveBlockChainFork(check);
            }
            catch (Exception e){
                logger.severe("Fork resolve failed");
                e.printStackTrace();
            }
            logger.info("Fork resolved");
            printBlockchain();
        }
        logger.info("It's all ok");

    }

    public void printBlockchain() {
        for (BlockType i:
             this.blockChain.blockChain) {
            System.out.println("Block "+i.getBlockSign().getData().getIndex() +" "+ ArrayTools.bytesToHex(CriptoTools.hash(i.toByteArray())));

        }
    }
}
