package pt.up.fc.dcc.ssd.a.blockchain;

import com.google.protobuf.ByteString;

public class Log {
    private LogType pLog;

    Log(byte[] data, byte[] assin){
        LogType.Builder log_builder = LogType.newBuilder();
        log_builder.setData(ByteString.copyFrom(data));
        log_builder.setAssin(ByteString.copyFrom(assin));
        pLog = log_builder.build();
    }

    public LogType getProtoType(){
        return pLog;
    }

    @Override
    public String toString() {
        return pLog.toString();
    }
}
