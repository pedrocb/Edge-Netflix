package client.cli;

import client.Client;
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
        //TODO: Prevent downloading existing file
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


                File file = new File(filename, fileBean.getSize(), fileBean.getChunkSize(), new ArrayList<>());
                DownloadFileThread thread = new DownloadFileThread(file, seeder.getEndpoint(), port);
                files.add(file);
                thread.start();
            } else if(response.getStatus() == 404) {
                System.out.println("That file does not exist!");
            } else {
                System.out.println("[Error] There was an error in the portal.");
            }
        } catch (ProcessingException e) {
            System.out.println("[Error]: Can't connect to Portal");
        }
    }


}
