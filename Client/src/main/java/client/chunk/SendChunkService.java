package client.chunk;

import com.google.protobuf.ByteString;
import core.SendChunkServiceGrpc;
import core.Chunk;
import core.Request;
import io.grpc.stub.StreamObserver;

import java.util.stream.Stream;

public class SendChunkService extends SendChunkServiceGrpc.SendChunkServiceImplBase {
    private ByteString chunk;
    public SendChunkService() {
        super();
        chunk = ByteString.copyFromUtf8("Hello World");
    }

    @Override
    public void requestChunk(Request request, StreamObserver responseObserver){
        String filename = request.getFilename();
        int startIndex = request.getStartIndex();
        Chunk response = Chunk.newBuilder().setData(chunk).build();
        System.out.println("Filename: "+filename+" Chunk start: "+startIndex);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
