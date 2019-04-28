package pt.up.fc.dcc.ssd.a.tracker;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import pt.up.fc.dcc.ssd.a.utils.Challenge;
import pt.up.fc.dcc.ssd.a.utils.ChallengeResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

public class TrackerService extends pt.up.fc.dcc.ssd.a.tracker.TrackerServerGrpc.TrackerServerImplBase {
    private int zeros;

    TrackerService(int zeros){
        this.zeros = zeros;
    }

    @Override
    public void getAnswer(challengeAnswer request, StreamObserver<challengeValidation> responseObserver) {
        ChallengeResponse r = Tracker.isIdValid(request.getId().toByteArray(),request.getIpv4());
        challengeValidation reply = null;
        if(r.ans) {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            try {
                ObjectOutputStream out = new ObjectOutputStream(byteOut);
                out.writeObject(r.nodes);
            } catch(IOException e){
                e.printStackTrace();
            }
            reply = challengeValidation.newBuilder().setAnswer(r.ans).setNodeMap(ByteString.copyFrom(byteOut.toByteArray())).build();
        } else {
            reply = challengeValidation.newBuilder().setAnswer(r.ans).build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void idRequest(empty request, StreamObserver<challenge> responseObserver){
        challenge reply = challenge.newBuilder().setZeros(this.zeros).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}