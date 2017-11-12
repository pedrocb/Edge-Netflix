package portal.seeder;

import datamodels.FileBean;
import datamodels.SeederBean;
import portal.Database;

import javax.swing.plaf.nimbus.State;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.Result;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import portal.Database;

@Path("seeder")
public class SeederService {
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response  listServices() {
        ArrayList<FileBean> result = new ArrayList<>();
        try {
            result = Database.getAllFiles();
            System.out.println(result);
        } catch (SQLException e) {
            System.out.println("[ERROR] Can't access database");
            return Response.status(503).build();
        }
        return Response.status(200).entity(result).build();
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchServices(@QueryParam("keyword") final List<String> keyword) {
        System.out.println(keyword);
        ArrayList<FileBean> files = new ArrayList<>();
        ArrayList<FileBean> result = new ArrayList<>();
        try {
            files = Database.getAllFiles();
            result = new ArrayList<>(files.stream().filter(file -> file.getKeywords().containsAll(keyword)).collect(Collectors.toList()));
        } catch (SQLException e) {
            System.out.println("[ERROR] Can't access database");
            return Response.status(503).build();
        }
        return Response.status(200).entity(result).build();
    }

}

