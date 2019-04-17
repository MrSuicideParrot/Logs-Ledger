package pt.up.fc.dcc.ssd.a.blockchain;

import com.google.protobuf.ByteString;
import pt.up.fc.dcc.ssd.a.Config;
import pt.up.fc.dcc.ssd.a.node.Signable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Block implements Signable {
    private BlockType.Builder blockBuilder;
    private BlockType.BlockSign.BlockData.Builder dataBuilder;
    private int indexLog;

    Block(int index, byte[] parent, long date){
       blockBuilder = BlockType.newBuilder();
       dataBuilder = blockBuilder.getBlockSignBuilder().getDataBuilder();

       dataBuilder.setIndex(index);
       dataBuilder.setHashParent(ByteString.copyFrom(parent));
       dataBuilder.setTimestamp(date);

       indexLog = 0;
    }

    Block(){
        this(0, "42".getBytes(), 0);

    }

    boolean addLog(Log newLog){
        if(indexLog < Config.maxLogs){
            dataBuilder.addLogs(newLog.getProtoType());
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
        try {
            byte[] data = blockBuilder.getBlockSignBuilder().build().toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return md.digest();
        }
        catch (NoSuchAlgorithmException e){
            return null;
        }
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

}
