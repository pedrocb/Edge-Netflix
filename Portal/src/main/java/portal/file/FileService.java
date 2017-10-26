package portal.file;

import datamodels.Seeder;

import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;

@Path("file")
public class FileService {
    @POST
    @Path("download")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Seeder downloadFile(JsonObject file) {
        System.out.println(file.getString("file"));
        return new Seeder("video1", "tcp://localhost:9000", 900, 23,new ArrayList<String>(Arrays.asList("HAHA", "HEHE")));
    }
}
