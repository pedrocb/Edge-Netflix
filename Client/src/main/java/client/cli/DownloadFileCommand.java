package client.cli;

import datamodels.SeederBean;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
            System.out.println("Connecting to seeder " + response.readEntity(SeederBean.class));
        }
    }
}
