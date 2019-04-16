package pt.up.fc.dcc.ssd.a.blockchain;

import com.google.protobuf.ByteString;
import pt.up.fc.dcc.ssd.a.Config;

class Block {
    private BlockType.Builder blockBuilder;
    private int indexLog;

    Block(long index, byte[] parent, long date){
       blockBuilder = BlockType.newBuilder();
       blockBuilder.setIndex(index);
       blockBuilder.setHashParent(ByteString.copyFrom(parent));
       blockBuilder.setTimestamp(date);
       indexLog = 0;
    }

    boolean addLog(Log newLog){
        if(indexLog < Config.maxLogs){
            blockBuilder.addLogs(newLog.getProtoType());
            ++indexLog;
            return true;
        }
        return false;
    }

    boolean isFull(){
        return indexLog >= Config.maxLogs;
    }

    @Override
    public String toString() {
        //TODO
        return super.toString();
    }
}
