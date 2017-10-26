package portal.seeder;

import datamodels.Seeder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Path("seeder")
public class SeederService {
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Seeder> listServices() {
        ArrayList<Seeder> result = new ArrayList<Seeder>();
        result.add(new Seeder("video1", "tcp://localhost:9000", 900, 23, new ArrayList<String>(Arrays.asList("HAHA", "HEHE"))));
        result.add(new Seeder("video2", "tcp://localhost:9001", 900, 23, new ArrayList<String>(Arrays.asList("HAHA", "HEHE"))));
        return result;
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Seeder> searchServices(@QueryParam("keyword") final String keyword) {
        ArrayList<Seeder> seeders = new ArrayList<Seeder>();
        seeders.add(new Seeder("video1", "tcp://localhost:9000", 900, 23,new ArrayList<String>(Arrays.asList("HAHA", "HEHE"))));
        seeders.add(new Seeder("video2", "tcp://localhost:9001", 900, 23,new ArrayList<String>(Arrays.asList("hah", "HEHE"))));

        ArrayList<Seeder> result = new ArrayList<>(seeders.stream().filter(seeder -> seeder.getKeywords().contains(keyword)).collect(Collectors.toList()));


        return result;
    }
}
