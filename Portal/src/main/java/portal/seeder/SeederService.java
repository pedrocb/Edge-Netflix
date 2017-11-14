package portal.seeder;

import com.google.api.client.util.Lists;
import datamodels.FileBean;
import datamodels.SeederBean;
import portal.Database;

import javax.swing.plaf.nimbus.State;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
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
    public Response listServices() {
        ArrayList<FileBean> result;
        try {
            result = Database.getAllFiles();
        } catch (SQLException e) {
            System.out.println("[ERROR] Can't access database");
            return Response.status(503).build();
        }
        GenericEntity<ArrayList<FileBean>> entity
                = new GenericEntity<ArrayList<FileBean>>(Lists.newArrayList(result)) {};

        return Response.status(200).entity(entity).build();
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchServices(@QueryParam("keyword") final List<String> keyword) {
        ArrayList<FileBean> files;
        ArrayList<FileBean> result;
        try {
            files = Database.getAllFiles();
            result = new ArrayList<>(files.stream().filter(file -> file.getKeywords().containsAll(keyword)).collect(Collectors.toList()));
        } catch (SQLException e) {
            System.out.println("[ERROR] Can't access database");
            return Response.status(503).build();
        }
        GenericEntity<ArrayList<FileBean>> entity
                = new GenericEntity<ArrayList<FileBean>>(Lists.newArrayList(result)) {};
        return Response.status(200).entity(entity).build();
    }

}

