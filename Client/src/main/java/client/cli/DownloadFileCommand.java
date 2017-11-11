package client.cli;

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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.Random;

public class DownloadFileCommand implements Command {
    private String file;

    public DownloadFileCommand(String file) {
       this.file = file;
    }
    public void run(WebTarget target) {
        JsonObject body = Json.createObjectBuilder().add("file", file).build();

        Response response = target.path("file/download")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(body.toString()));

        if(response.getStatus() == 200) {
            ManagedChannel clientChannel = ManagedChannelBuilder.forTarget("localhost:9000").usePlaintext(true).build();
            SendChunkServiceGrpc.SendChunkServiceBlockingStub clientStub = SendChunkServiceGrpc.newBlockingStub(clientChannel);
            Request request = Request.newBuilder().setFilename("tl_512kb.mp4").setIndex(3).build();
            Chunk recievedChunk = clientStub.requestChunk(request);
            ByteString chunk = recievedChunk.getData();
            String message = chunk.toStringUtf8();
            System.out.println(message);

            FileBean fileBean = response.readEntity(FileBean.class);
            SeederBean seeder = fileBean.getSeeder();
            System.out.println("Connecting to seeder " + seeder);
            System.out.println(fileBean);
            System.out.println(fileBean.getChunkSize());

            ManagedChannel channel = ManagedChannelBuilder.forTarget(seeder.getEndpoint()).usePlaintext(true).build();
            SeederServiceGrpc.SeederServiceBlockingStub stub = SeederServiceGrpc.newBlockingStub(channel);
            int random = new Random().nextInt();
            System.out.println("Port number " + random + "joined.");
            Endpoint endpoint = Endpoint.newBuilder().setAddress("localhost").setPort(random).build();
            JoinResponse joinResponse = stub.joinSwarm(endpoint);
            System.out.println(joinResponse);
            System.out.println("Got clients:");
            for (Endpoint i : joinResponse.getClientsList()) {
                System.out.println(i);
            }
            System.out.println("Got hashes:");
            for (ByteString i : joinResponse.getHashesList()) {
                System.out.println(Base64.getEncoder().encodeToString(i.toByteArray()));
            }
        }
    }
}
