package pt.up.fc.dcc.ssd.a.blockchain;

import com.google.protobuf.ByteString;
import pt.up.fc.dcc.ssd.a.node.Signable;

public class LogBuilder implements Signable {
    private LogType.Builder log_builder;

    LogBuilder(byte[] data){
        log_builder = LogType.newBuilder();

        LogType.LogData.Builder data_builder = log_builder.getDataBuilder();

        data_builder.setData(ByteString.copyFrom(data));
        data_builder.setTimestamp(System.currentTimeMillis() / 1000L);
    }

    public LogType build(){
        return  log_builder.build();
    }

    @Override
    public byte[] getDataToSign() {
        return log_builder.getDataBuilder().build().toByteArray();
    }

    @Override
    public void setSignature(byte[] signature) {
        log_builder.setAssin(ByteString.copyFrom(signature));
    }
}
