package pt.up.fc.dcc.ssd.a.blockchain;

import com.google.protobuf.ByteString;
import pt.up.fc.dcc.ssd.a.node.Signable;

public class Log implements Signable {
    private LogType pLog;
    private LogType.Builder log_builder;

    Log(byte[] data){
        log_builder = LogType.newBuilder();

        LogType.LogData.Builder data_builder = log_builder.getDataBuilder();

        data_builder.setData(ByteString.copyFrom(data));
        data_builder.setTimestamp(System.currentTimeMillis() / 1000L);

        pLog = null;
    }

    public LogType getProtoType(){
        return pLog;
    }

    @Override
    public byte[] getDataToSign() {
        return log_builder.getDataBuilder().build().toByteArray();
    }

    @Override
    public void setSignature(byte[] signature) {
        log_builder.setAssin(ByteString.copyFrom(signature));
        pLog = log_builder.build();
    }
}
