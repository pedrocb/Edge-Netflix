package seeder;

import com.google.protobuf.ByteString;
import core.*;
import datamodels.File;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;

public class SeederService extends SeederServiceGrpc.SeederServiceImplBase{
    private ArrayList<Endpoint> clients;
    private ArrayList<byte[]> chunkHashes;

    public SeederService(ArrayList<byte[]> hashes) {
        clients = new ArrayList<>();
        chunkHashes = hashes;
    }

    public void addSeederToClients(int port) {
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
        clients.add(request);
        System.out.println("Waiting for hashes");
        synchronized (chunkHashes) {
        }
        System.out.println("Here they are");
        int numberOfChunks = chunkHashes.size();
        System.out.println("Sending chunks...");
        for (int i = 0; i < numberOfChunks; i++) {
                builder.addHashes(ByteString.copyFrom(chunkHashes.get(i)));
            }
        for (Endpoint i : clients) {
            builder.addClients(i);
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
