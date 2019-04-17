package pt.up.fc.dcc.ssd.a.blockchain;

import jdk.nashorn.internal.ir.WhileNode;
import pt.up.fc.dcc.ssd.a.Config;

import java.util.Random;

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
               BlockBuilder blockBuilder = new BlockBuilder(
                       this.bl.getMaxIndex()+1,
                       this.bl.getLastBlockHash(),
                       System.currentTimeMillis() / 1000L);

                LogType[] logs = this.bl.getLogsToMine();

                for (LogType i : logs){
                    blockBuilder.addLog(i);
                }

                //TODO assinar

                Random rand = new Random(System.currentTimeMillis());

                while (!reset) {
                    blockBuilder.setNonce(rand.nextLong());

                }

                if(!reset){

                }
            }


        }
    }
}
