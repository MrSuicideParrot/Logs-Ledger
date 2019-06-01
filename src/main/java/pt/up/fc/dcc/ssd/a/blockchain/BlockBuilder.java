package pt.up.fc.dcc.ssd.a.blockchain;

import com.google.protobuf.ByteString;
import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.node.Signable;
import pt.up.fc.dcc.ssd.a.utils.ArrayTools;
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
        this(index,parent,date, Config.myID);
    }

    BlockBuilder(int index, byte[] parent, long date, ByteString nodeID){
        blockBuilder = BlockType.newBuilder();
        dataBuilder = blockBuilder.getBlockSignBuilder().getDataBuilder();

        dataBuilder.setIndex(index);
        dataBuilder.setHashParent(ByteString.copyFrom(parent));
        dataBuilder.setTimestamp(date);
        dataBuilder.setNodeID(nodeID);
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
       blockBuilder.getBlockSignBuilder().setAssin(ByteString.copyFrom(signature));
    }

    public void addNodeID(ByteString nodeI){
        dataBuilder.addNodes(nodeI);
    }

    public BlockType.BlockSign.BlockData.Builder getBlockData(){
        return dataBuilder;
    }

    static BlockType genesisBlock(){
        BlockBuilder blBuild = new BlockBuilder(0, "42".getBytes(), 0, ByteString.copyFrom("GOD".getBytes()));
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
            if(Challenge.countZeros(candidateBlock.getHash().toByteArray()) < Config.block_zeros){
                return false;
            }
        }
        else{
            int index = candidateBlock.getBlockSign().getData().getIndex();

            if( Config.temp_proof_of_work && index >= Config.initial_work && isProofOfStake(blockChain.getBlock(index-1), candidateBlock)){
                if(candidateBlock.getBlockSign().getData().getNodeID().equals(Config.staker)){
                    //blockChain.generateNextStaker();
                    blockChain.setStakerTimer(candidateBlock.getBlockSign().getData().getTimestamp(), index );
                    return true;
                }
            }

            if(Config.temp_proof_of_work ){ //TODO verificar
                if(Challenge.countZeros(candidateBlock.getHash().toByteArray()) < Config.block_zeros){
                    return false;
                }
                else{
                    // proof of work temporario confirmar se esta na altura de acabar o pw
                    if(candidateBlock.getBlockSign().getData().getIndex() >= Config.initial_work){
                        //blockChain.generateNextStaker();
                        Config.temp_proof_of_work = false;
                        logger.info("Proof of stake activated");
                        blockChain.setStakerTimer(candidateBlock.getBlockSign().getData().getTimestamp(), index);
                    }
                    return true;
                }
            }
            else{
                //Caso haja reset
                if(Config.proof_of_stake && candidateBlock.getBlockSign().getData().getIndex() < Config.initial_work){
                    if(Challenge.countZeros(candidateBlock.getHash().toByteArray()) < Config.block_zeros){
                        return false;
                    }
                    //blockChain.generateNextStaker();
                    return true;
                }
                // Puro proof of stake
                logger.info("Index: "+ candidateBlock.getBlockSign().getData().getIndex() );
                logger.info("Node validated -> "+ArrayTools.bytesToHex(candidateBlock.getBlockSign().getData().getNodeID())+"=="+ArrayTools.bytesToHex(Config.staker));
                if(candidateBlock.getBlockSign().getData().getNodeID().equals(Config.staker)){
                    //blockChain.generateNextStaker();
                    blockChain.setStakerTimer(candidateBlock.getBlockSign().getData().getTimestamp(),index);
                    return true;
                }
                return false;
            }
        }

        return true;
    }

    private static boolean isProofOfStake(BlockType block, BlockType candidateBlock) {
        long parentTimestamp = block.getBlockSign().getData().getTimestamp();
        long candidateTimestamp = candidateBlock.getBlockSign().getData().getTimestamp();

        return parentTimestamp + Config.stake_timer  >  candidateTimestamp;
    }


}
