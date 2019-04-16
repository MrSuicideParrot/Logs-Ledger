package pt.up.fc.dcc.ssd.a.tracker;

import io.grpc.stub.StreamObserver;

import java.lang.reflect.Field;

public class TrackerService extends pt.up.fc.dcc.ssd.a.tracker.TrackerServerGrpc.TrackerServerImplBase {
    private int zeros;

    TrackerService(int zeros){
        this.zeros = zeros;
    }

    @Override
    public void getAnswer(challengeAnswer request, StreamObserver<challengeValidation> responseObserver) {
        challengeValidation reply = challengeValidation.newBuilder().setAnswer(Tracker.isIdValid(request.getId().toByteArray(),request.getIpv4())).build();
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