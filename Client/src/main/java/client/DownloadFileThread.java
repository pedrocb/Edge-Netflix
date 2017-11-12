package client;

import com.google.protobuf.ByteString;
import core.Chunk;
import core.Endpoint;
import core.Request;
import core.SendChunkServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.List;

public class DownloadFileThread extends Thread {
    private File file;

    public DownloadFileThread(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        System.out.println("Starting " + file.getFilename() + " download!");


    }

    void downloadChunk(List<Endpoint> neighbours, int chunkIndex) {
        ManagedChannel clientChannel = ManagedChannelBuilder.forTarget("localhost:9000").usePlaintext(true).build();
        SendChunkServiceGrpc.SendChunkServiceBlockingStub clientStub = SendChunkServiceGrpc.newBlockingStub(clientChannel);
        Request request = Request.newBuilder().setFilename("tl_512kb.mp4").setIndex(3).build();
        Chunk recievedChunk;
        try {
            recievedChunk = clientStub.requestChunk(request);
            ByteString chunk = recievedChunk.getData();
            String message = chunk.toStringUtf8();
            System.out.println(message);
        } catch (Exception e) {
            System.out.println("Could not get Chunk");
            //e.printStackTrace();
        }

    }
}
