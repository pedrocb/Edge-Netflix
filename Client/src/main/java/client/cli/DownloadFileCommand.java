package client.cli;

import core.ClientList;
import core.Endpoint;
import core.SeederServiceGrpc;
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
            SeederBean seeder = response.readEntity(SeederBean.class);
            System.out.println("Connecting to seeder " + seeder);

            ManagedChannel channel = ManagedChannelBuilder.forTarget(seeder.getEndpoint()).usePlaintext(true).build();
            SeederServiceGrpc.SeederServiceBlockingStub stub = SeederServiceGrpc.newBlockingStub(channel);
            int random = new Random().nextInt();
            System.out.println("Port number " + random + "joined.");
            Endpoint endpoint = Endpoint.newBuilder().setAddress("localhost").setPort(random).build();
            ClientList clients = stub.joinSwarm(endpoint);
            System.out.println("Got clients:");
            for (Endpoint i : clients.getClientsList()) {
                    System.out.println(i);
            }
        }
    }
}
