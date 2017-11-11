package seeder;

import com.google.protobuf.ByteString;
import core.Endpoint;
import core.JoinResponse;
import core.SeederServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;

public class SeederService extends SeederServiceGrpc.SeederServiceImplBase{
    private ArrayList<Endpoint> clients = new ArrayList<>();
    private ArrayList<byte[]> chunkHashes;
    private int videoSize;
    private int chunkSize;

    public SeederService(ArrayList<byte[]> chunkHashes, int videoSize, int chunkSize) {
        this.chunkHashes = chunkHashes;
        this.videoSize = videoSize;
        this.chunkSize = chunkSize;
    }

    @Override
    public void joinSwarm(Endpoint request, StreamObserver<JoinResponse> responseObserver) {
        JoinResponse.Builder builder = JoinResponse.newBuilder();
        for (Endpoint i : clients) {
            builder.addClients(i);
        }
        int numberOfChunks = (int) Math.ceil((float)videoSize/chunkSize);
        System.out.println("Sending chunks...");
        for(int i = 0; i < numberOfChunks; i++) {
            builder.addHashes(ByteString.copyFrom(chunkHashes.get(i)));
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        clients.add(request);
    }
}
