package client.cli;

import client.File;
import client.chunk.SendChunkService;
import com.google.protobuf.ByteString;
import core.*;
import datamodels.FileBean;
import datamodels.SeederBean;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

public class DownloadFileCommand implements Command {
    private String filename;
    private int port;

    public DownloadFileCommand(String filename, int port) {
       this.filename = filename;
       this.port = port;
    }
    public void run(WebTarget target) {
        JsonObject body = Json.createObjectBuilder().add("file", filename).build();

        Response response = target.path("file/download")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(body.toString()));

        if(response.getStatus() == 200) {
            FileBean fileBean = response.readEntity(FileBean.class);
            SeederBean seeder = fileBean.getSeeder();
            System.out.println("Connecting to seeder " + seeder);
            System.out.println(fileBean);
            System.out.println(fileBean.getChunkSize());

            ManagedChannel channel = ManagedChannelBuilder.forTarget(seeder.getEndpoint()).usePlaintext(true).build();
            SeederServiceGrpc.SeederServiceBlockingStub stub = SeederServiceGrpc.newBlockingStub(channel);
            Endpoint endpoint = Endpoint.newBuilder().setAddress("localhost").setPort(port).build();
            JoinResponse joinResponse = stub.joinSwarm(endpoint);
            File file = new File(filename);
            downloadChunks(joinResponse);
        }
    }

    void downloadChunks(JoinResponse joinResponse, File file){
        List<Endpoint> neighbours = joinResponse.getClientsList();
        System.out.println("Got clients:");
        for (Endpoint i : joinResponse.getClientsList()) {
            System.out.println(i);
        }
        System.out.println("Got hashes:");
        for (ByteString i : joinResponse.getHashesList()) {
            System.out.println(Base64.getEncoder().encodeToString(i.toByteArray()));
        }

        while (!)



    }

    void downloadChunk(List<Endpoint> neighbours, int chunkIndex){
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
