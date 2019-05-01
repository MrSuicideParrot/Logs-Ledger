package pt.up.fc.dcc.ssd.a.blockchain;

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
        int check = blockChain.checkBlockChain();

        if(check != 0){
            logger.warning("Fork detected");
            blockChain.findAndResolveBlockChainFork(check);
        }
        logger.info("It's all ok");

    }
}
