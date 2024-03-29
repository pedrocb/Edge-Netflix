package portal.file;

import datamodels.FileBean;
import datamodels.SeederBean;
import io.grpc.*;
import core.FileInfo;
import core.MasterSeederServiceGrpc;
import core.MasterSeederServiceGrpc.MasterSeederServiceBlockingStub;
import core.Endpoint;
import portal.Database;
import portal.Portal;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.crypto.Data;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

@Path("file")
public class FileService {
    @POST
    @Path("download")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadFile(String request) {
        JsonObject fileObject;
        try {
            JsonReader jsonReader = Json.createReader(new StringReader(request));
            fileObject = jsonReader.readObject();
        } catch (Exception e) {
            return Response.status(402).build();
        }
        String msAddress = Portal.config.getProperty("msAddress", "localhost");
        int msPort;
        try {
            msPort = Integer.parseInt(Portal.config.getProperty("msPort", "9998"));
        } catch (NumberFormatException e) {
            System.out.println("[ERROR][CONFIG] msPort is not a number.");
            return Response.status(503).build();
        }

        String filename = fileObject.getString("file");
        synchronized (Database.connection) {
            try {
                FileBean file = Database.getFile(filename);
                if (file != null) {
                    if (file.getSeeder() == null) {
                        ManagedChannel channel = ManagedChannelBuilder.forAddress(msAddress, msPort).usePlaintext(true).build();
                        MasterSeederServiceBlockingStub stub = MasterSeederServiceGrpc.newBlockingStub(channel);

                        System.out.println("Seeder not found. Creating one ...");
                        try {
                            Endpoint info = stub.createSeeder(FileInfo.newBuilder().setFilename(filename).setChunkSize(file.getChunkSize()).build());
                            SeederBean seeder = Database.registerSeeder(filename, info.getAddress(), info.getPort());
                            file.setSeeder(seeder);
                        } catch (StatusRuntimeException e) {
                            if (e.getStatus().getCode() == Status.Code.RESOURCE_EXHAUSTED) {
                                System.out.println("[ERROR]: Can't create new Seeder");
                            } else {
                                System.out.println("[ERROR]: Can't access Master Seeder");
                            }
                            return Response.status(503).build();
                        }
                    }
                    return Response.status(200).entity(file).build();
                } else {
                    return Response.status(404).build();
                }

            } catch (SQLException e) {
                System.out.println("[ERROR]: Can't access database.");
                return Response.status(503).build();
            }
        }
    }
}
