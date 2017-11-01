package portal.seeder;

import datamodels.SeederBean;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Path("seeder")
public class SeederService {
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<SeederBean> listServices() {
        ArrayList<SeederBean> result = new ArrayList<SeederBean>();
        result.add(new SeederBean("video1", "tcp://localhost:9000", 900, 23, new ArrayList<String>(Arrays.asList("HAHA", "HEHE"))));
        result.add(new SeederBean("video2", "tcp://localhost:9001", 900, 23, new ArrayList<String>(Arrays.asList("HAHA", "HEHE"))));
        return result;
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<SeederBean> searchServices(@QueryParam("keyword") final String keyword) {
        ArrayList<SeederBean> seeders = new ArrayList<SeederBean>();
        seeders.add(new SeederBean("video1", "tcp://localhost:9000", 900, 23,new ArrayList<String>(Arrays.asList("HAHA", "HEHE"))));
        seeders.add(new SeederBean("video2", "tcp://localhost:9001", 900, 23,new ArrayList<String>(Arrays.asList("hah", "HEHE"))));

        ArrayList<SeederBean> result = new ArrayList<>(seeders.stream().filter(seeder -> seeder.getKeywords().contains(keyword)).collect(Collectors.toList()));

        return result;
    }
}
