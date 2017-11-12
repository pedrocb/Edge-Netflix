package client.cli;

import datamodels.File;

import java.util.ArrayList;
import client.DownloadFileThread;
import core.*;
import datamodels.FileBean;
import datamodels.SeederBean;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

public class DownloadFileCommand implements Command {
    private String filename;
    private int port;

    private ArrayList<File> files;

    public DownloadFileCommand(String filename, ArrayList<File> files, int port) {
       this.filename = filename;
       this.files = files;
       this.port = port;
    }

    public void run(WebTarget target) {
        JsonObject body = Json.createObjectBuilder().add("file", filename).build();

        try {
            Response response = target.path("file/download")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body.toString()));

            if (response.getStatus() == 200) {
                FileBean fileBean = response.readEntity(FileBean.class);
                SeederBean seeder = fileBean.getSeeder();
                System.out.println("Connecting to seeder " + seeder);
                System.out.println(fileBean);
                System.out.println(fileBean.getChunkSize());


                ManagedChannel channel = ManagedChannelBuilder.forTarget(seeder.getEndpoint()).usePlaintext(true).build();
                SeederServiceGrpc.SeederServiceBlockingStub stub = SeederServiceGrpc.newBlockingStub(channel);
                Endpoint endpoint = Endpoint.newBuilder().setAddress("localhost").setPort(port).build();
                JoinResponse joinResponse = stub.joinSwarm(endpoint);


                File file = new File(filename, fileBean.getSize(), fileBean.getChunkSize(), new ArrayList<>(joinResponse.getClientsList()));
                String[] hashes = new String[file.getNumChunks()];
                for (int i = 0; i < joinResponse.getHashesList().size(); i++) {
                    hashes[i] = Base64.getEncoder().encodeToString(joinResponse.getHashesList().get(i).toByteArray());
                }
                file.setHashes(hashes);

                DownloadFileThread thread = new DownloadFileThread(file);
                files.add(file);
                thread.start();
            } else {
                System.out.println("[Error] There was an error in the portal.");
            }
        } catch (ProcessingException e) {
            System.out.println("[Error]: Can't connect to Portal");
        }
    }


}
