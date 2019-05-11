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

        for (BlockType i:
             this.blockChain.blockChain) {
            logger.info("Block "+i.getBlockSign().getData().getIndex() +" "+ ArrayTools.bytesToHex(CriptoTools.hash(i.toByteArray())));

        }

        int check = blockChain.checkBlockChain();

        if(check != 0){
            logger.warning("Fork detected");
            blockChain.findAndResolveBlockChainFork(check);
        }
        logger.info("It's all ok");

    }
}
