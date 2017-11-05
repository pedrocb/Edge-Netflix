package seeder;

import core.ClientList;
import core.Endpoint;
import core.SeederServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;

public class SeederService extends SeederServiceGrpc.SeederServiceImplBase{
    private ArrayList<Endpoint> clients = new ArrayList<>();

    @Override
    public void joinSwarm(Endpoint request, StreamObserver<ClientList> responseObserver) {
        ClientList.Builder builder = ClientList.newBuilder();
        for (Endpoint i : clients) {
           builder.addClients(i);
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        clients.add(request);
    }
}
