package pt.up.fc.dcc.ssd.a.blockchain;

import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.p2p.Network;
import pt.up.fc.dcc.ssd.a.utils.Challenge;

import java.util.Random;

public class MinerWorker implements Runnable{
    BlockChain bl;
    Network net;
    boolean reset;

    MinerWorker(BlockChain bl, Network net){
        this.bl = bl;
        this.net = net;
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
                    byte[] hash = blockBuilder.getBlockHash();
                    int zeros = Challenge.countZeros(hash);
                    if(zeros >= Config.zeros){
                        break;
                    }
                }

                if(!reset){
                    bl.addNewBlock(blockBuilder.build());
                    net.gossipBlock(blockBuilder.build());
                }
            }


        }
    }
}
