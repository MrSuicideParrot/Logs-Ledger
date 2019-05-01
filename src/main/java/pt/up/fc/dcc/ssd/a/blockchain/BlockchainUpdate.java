package pt.up.fc.dcc.ssd.a.blockchain;

import java.util.TimerTask;

public class BlockchainUpdate extends TimerTask {
    BlockChain blockChain;

    BlockchainUpdate(BlockChain blockChain){
        this.blockChain = blockChain;
    }

    @Override
    public void run() {
        int check = blockChain.checkBlockChain();

        if(check != 0){
            blockChain.findAndResolveBlockChainFork(check);
        }

    }
}
