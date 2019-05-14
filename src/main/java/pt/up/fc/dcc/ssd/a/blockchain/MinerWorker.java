package pt.up.fc.dcc.ssd.a.blockchain;

import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.p2p.Network;
import pt.up.fc.dcc.ssd.a.utils.Challenge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class MinerWorker implements Runnable{
    BlockChain bl;
    Network net;
    boolean reset;

    private static final Logger logger = Logger.getLogger(MinerWorker.class.getName());

    MinerWorker(BlockChain bl, Network net){
        this.bl = bl;
        this.net = net;
        this.reset = false;
        logger.info("Miner created");
    }

    @Override
    public void run() {
        logger.info("Miner initialized");
        while (true){
            if(Config.im_the_staker || Config.temp_proof_of_work) {
                if (bl.logPool.size() >= Config.maxLogs) {
                    bl.logPoolLock.lock();
                    bl.blockChainLock.lock();
                    HashSet<LogType> l = new HashSet<>();
                    for (BlockType b : bl.blockChain) {
                        l.addAll(b.getBlockSign().getData().getLogsList());
                    }

                    LogType[] logs = this.bl.getLogsToMine();
                    ArrayList<LogType> dups = new ArrayList<>();

                    for (LogType i : logs) {
                        if (l.contains(i)) {
                            dups.add(i);
                        }
                    }
                    LogType[] temp = new LogType[dups.size()];
                    bl.removeLogsFromPool(dups.toArray(temp));
                    if (bl.logPool.size() < Config.maxLogs) {
                        bl.logPoolLock.unlock();
                        bl.blockChainLock.unlock();
                        logger.info("Contained already mined logs. Restarting mining process.");
                        continue;
                    }

                    logger.info("Mining a block");
                    BlockBuilder blockBuilder = new BlockBuilder(
                            this.bl.getMaxIndex() + 1,
                            this.bl.getLastBlockHash(),
                            System.currentTimeMillis() / 1000L);

                    for (LogType i : logs) {
                        blockBuilder.addLog(i);
                    }

                    if(Config.proof_of_stake){
                        //Adicionar nos escolhidos
                       /* TODO int good = Config.choice_good_nodes;
                        if(good>)

                        for(int i=0; i < Config.choice_good_nodes)*/
                    }

                    //TODO assinar
                    if(Config.temp_proof_of_work){
                        Random rand = new Random(System.currentTimeMillis());

                        while (!reset) {
                            blockBuilder.setNonce(rand.nextLong());
                            byte[] hash = blockBuilder.getBlockHash();
                            int zeros = Challenge.countZeros(hash);
                            if (zeros >= Config.zeros) {
                                break;
                            }
                        }
                    }

                    if (!reset) {
                        bl.addNewBlock(blockBuilder.build(), null);
                        net.gossipBlock(blockBuilder.build());
                        logger.info("Blocked was mined with success");

                        bl.removeLogsFromPool(logs);
                    }

                    bl.logPoolLock.unlock();
                    bl.blockChainLock.unlock();
                }
            }

            try {
             sleep(Config.sleep_time_miner);
            }
            catch (InterruptedException e){
                logger.warning(e.getMessage());
            }
        }
    }
}
