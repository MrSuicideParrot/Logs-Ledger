package pt.up.fc.dcc.ssd.a.blockchain;

import jdk.nashorn.internal.ir.WhileNode;
import pt.up.fc.dcc.ssd.a.Config;

public class MinerWorker implements Runnable{
    BlockChain bl;
    boolean reset;

    MinerWorker(BlockChain bl){
        this.bl = bl;
        this.reset = false;
    }

    @Override
    public void run() {
        while (true){

            if(bl.logPool.size() >= Config.maxLogs) {
               // BlockBuilder bl = new BlockBuilder();

                while (!reset) {

                }
            }
            //Sleep
        }
    }
}
