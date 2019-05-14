package pt.up.fc.dcc.ssd.a.blockchain;

import com.google.protobuf.ByteString;
import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.node.Signable;
import pt.up.fc.dcc.ssd.a.utils.Challenge;
import pt.up.fc.dcc.ssd.a.utils.CriptoTools;

import java.util.Arrays;
import java.util.logging.Logger;

class BlockBuilder implements Signable {
    private BlockType.Builder blockBuilder;
    private BlockType.BlockSign.BlockData.Builder dataBuilder;
    private int indexLog;

    private static final Logger logger = Logger.getLogger(BlockBuilder.class.getName());

    BlockBuilder(int index, byte[] parent, long date){
       blockBuilder = BlockType.newBuilder();
       dataBuilder = blockBuilder.getBlockSignBuilder().getDataBuilder();

       dataBuilder.setIndex(index);
       dataBuilder.setHashParent(ByteString.copyFrom(parent));
       dataBuilder.setTimestamp(date);

       indexLog = 0;
    }

    boolean addLog(LogType newLog){
        if(indexLog < Config.maxLogs){
            dataBuilder.addLogs(newLog);
            ++indexLog;
            return true;
        }
        return false;
    }

    boolean isFull(){
        return indexLog >= Config.maxLogs;
    }

    void setNonce(long nonce){
        blockBuilder.getBlockSignBuilder().setNonce(nonce);
    }

    byte[] getBlockHash(){
        byte[] data = blockBuilder.getBlockSignBuilder().build().toByteArray();
        return CriptoTools.hash(data);
    }

    BlockType build(){
        blockBuilder.setHash(ByteString.copyFrom(getBlockHash()));
        return blockBuilder.build();
    }

    @Override
    public String toString() {
        //TODO
        return super.toString();
    }

    @Override
    public byte[] getDataToSign() {
        return dataBuilder.build().toByteArray();
    }

    @Override
    public void setSignature(byte[] signature) {
        blockBuilder.getBlockSignBuilder().setNodeID(Config.myID);
        blockBuilder.getBlockSignBuilder().setAssin(ByteString.copyFrom(signature));
    }

    static BlockType genesisBlock(){
        BlockBuilder blBuild = new BlockBuilder(0, "42".getBytes(), 0);
        blBuild.setNonce(42);
        blBuild.setSignature("GENESISBLOCKSIGN".getBytes());
        return blBuild.build();
    }

    static boolean confirmBlock(byte[] parent, BlockType candidateBlock, BlockChain blockChain){
        {
            // Parent confirm
            byte[] parentHash = candidateBlock.getBlockSign().getData().getHashParent().toByteArray();
            if(!Arrays.equals(parent, parentHash))
                return false;
        }

        {
            // BlockHash confirm
            byte[] data = candidateBlock.getBlockSign().toByteArray();
            byte[] dataHash = CriptoTools.hash(data);
            if(!Arrays.equals(dataHash,candidateBlock.getHash().toByteArray()))
                return false;
        }

            /* Confirmar proof of work ou proof of stake */
        if(Config.proof_of_stake == false)
        {
            // Nonce confirm
            //TODO deve depois haver mais merda aqui ¯\_(ツ)_/¯
            if(Challenge.countZeros(candidateBlock.getHash().toByteArray()) < Config.zeros){
                return false;
            }
        }
        else{
            if(Config.temp_proof_of_work){
                if(Challenge.countZeros(candidateBlock.getHash().toByteArray()) < Config.zeros){
                    return false;
                }
                else{
                    // proof of work temporario confirmar se esta na altura de acabar o pw
                    if(candidateBlock.getBlockSign().getData().getIndex() >= Config.initial_work){
                        blockChain.generateNextStaker();
                        Config.temp_proof_of_work = false;
                        logger.info("Proof of stake activated");
                        blockChain.setStakerTimer(candidateBlock.getBlockSign().getData().getTimestamp());
                    }
                    return true;
                }
            }
            else{
                // Puro proof of stake
                if(candidateBlock.getBlockSign().getNodeID().equals(Config.staker)){
                    blockChain.generateNextStaker();
                    blockChain.setStakerTimer(candidateBlock.getBlockSign().getData().getTimestamp());
                    return true;
                }
                return false;
            }
        }

        return true;
    }


}
