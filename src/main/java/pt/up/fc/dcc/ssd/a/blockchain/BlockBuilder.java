package pt.up.fc.dcc.ssd.a.blockchain;

import com.google.protobuf.ByteString;
import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.node.Signable;
import pt.up.fc.dcc.ssd.a.utils.Challenge;
import pt.up.fc.dcc.ssd.a.utils.CriptoTools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

class BlockBuilder implements Signable {
    private BlockType.Builder blockBuilder;
    private BlockType.BlockSign.BlockData.Builder dataBuilder;
    private int indexLog;

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
        blockBuilder.getBlockSignBuilder().setAssin(ByteString.copyFrom(signature));
    }

    static BlockType genesisBlock(){
        BlockBuilder blBuild = new BlockBuilder(0, "42".getBytes(), 0);
        blBuild.setNonce(42);
        blBuild.setSignature("GENESISBLOCKSIGN".getBytes());
        return blBuild.build();
    }

    static boolean confirmBlock(byte[] parent, BlockType candidateBlock){
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

        {
            // Nonce confirm
            //TODO deve depois haver mais merda aqui ¯\_(ツ)_/¯
            if(Challenge.countZeros(candidateBlock.getHash().toByteArray()) < Config.zeros){
                return false;
            }
        }

        return true;
    }

}
