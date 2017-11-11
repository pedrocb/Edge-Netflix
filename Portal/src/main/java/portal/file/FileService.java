package portal.file;

import datamodels.FileBean;
import datamodels.SeederBean;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import core.FileInfo;
import core.MasterSeederServiceGrpc;
import core.MasterSeederServiceGrpc.MasterSeederServiceBlockingStub;
import core.Endpoint;
import portal.Database;

import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

@Path("file")
public class FileService {
    @POST
    @Path("download")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public FileBean downloadFile(JsonObject fileObject) {
        // TODO: Change localhost to portal address
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9998).usePlaintext(true).build();
        MasterSeederServiceBlockingStub stub = MasterSeederServiceGrpc.newBlockingStub(channel);
        String filename = fileObject.getString("file");
        try {
            FileBean file = Database.getFile(filename);
            System.out.println(file);
            System.out.println(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
            if (file != null) {
                if(file.getSeeder() == null) {
                    System.out.println("Seeder not found. Creating one ...");
                    Endpoint info = stub.createSeeder(FileInfo.newBuilder().setFilename(filename).build());
                    SeederBean seeder = Database.registerSeeder(filename, info.getAddress(), info.getPort());
                    file.setSeeder(seeder);
                }
            }
            return file;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
