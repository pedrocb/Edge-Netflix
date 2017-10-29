package seeder;

import io.grpc.stub.StreamObserver;
import portal_seeder.SeederInputP;
import portal_seeder.SeederOutputP;
import portal_seeder.SeederServicePGrpc;

public class SeederServiceP extends SeederServicePGrpc.SeederServicePImplBase {

    public void start(SeederInputP request,
                      StreamObserver<SeederOutputP> responseObserver) {
        System.out.println("Starting seeder for file " + request.getFilename());
        SeederOutputP output = SeederOutputP.newBuilder().setEndpoint("tcp://localhost:8080").build();
        responseObserver.onNext(output);
        responseObserver.onCompleted();
    }

}
