package pt.up.fc.dcc.ssd.a.tracker;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.up.fc.dcc.ssd.a.utils.Challenge;

public class NodeService {
    private final String ip;
    private final String tracker;
    private final int port;

    public NodeService(String ip, String tracker, int port){
        this.ip = ip;
        this.tracker = tracker;
        this.port = port;
    }

    public byte[] getNodeId(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress(this.tracker,this.port).usePlaintext().build();
        TrackerServerGrpc.TrackerServerBlockingStub blockingStub = TrackerServerGrpc.newBlockingStub(channel);

        challenge zeros = blockingStub.idRequest(empty.newBuilder().build());

        byte[] id = new Challenge(zeros.getZeros()).findID();

        challengeValidation answer = blockingStub.getAnswer(challengeAnswer.newBuilder().setIpv4(ip).setId(ByteString.copyFrom(id)).build());

        while(!answer.getAnswer()){
            id = new Challenge(zeros.getZeros()).findID();

            answer = blockingStub.getAnswer(challengeAnswer.newBuilder().setIpv4(ip).setId(ByteString.copyFrom(id)).build());
        }

        return id;
    }
}
