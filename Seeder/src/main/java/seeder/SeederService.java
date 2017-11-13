package seeder;

import com.google.protobuf.ByteString;
import core.*;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;

public class SeederService extends SeederServiceGrpc.SeederServiceImplBase{
    private ArrayList<Endpoint> clients;
    private ArrayList<byte[]> chunkHashes;

    public SeederService(ArrayList<byte[]> chunkHashes, int videoSize, int chunkSize, int port) {
        this.chunkHashes = chunkHashes;
        clients = new ArrayList<>();
        String address = MasterSeeder.config.getProperty("address", "localhost");
        clients.add(Endpoint.newBuilder().setAddress(address).setPort(port).build());
    }

    @Override
    public void healthCheck(Empty request, StreamObserver<HealthResponse> responseObserver) {
        responseObserver.onNext(HealthResponse.newBuilder().setStatus(HealthResponse.Status.OK).build());
        responseObserver.onCompleted();
    }

    @Override
    public void joinSwarm(Endpoint request, StreamObserver<JoinResponse> responseObserver) {
        JoinResponse.Builder builder = JoinResponse.newBuilder();
        for (Endpoint i : clients) {
            builder.addClients(i);
        }
        int numberOfChunks = chunkHashes.size();
        System.out.println("Sending chunks...");
        for(int i = 0; i < numberOfChunks; i++) {
            builder.addHashes(ByteString.copyFrom(chunkHashes.get(i)));
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        clients.add(request);
    }
}
