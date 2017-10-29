package portal.file;

import datamodels.SeederBean;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import masterSeeder.FileInfo;
import masterSeeder.MasterSeederServiceGrpc;
import masterSeeder.MasterSeederServiceGrpc.MasterSeederServiceBlockingStub;
import masterSeeder.SeederEndpointInfo;

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
    public SeederBean downloadFile(JsonObject file) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9998).usePlaintext(true).build();
        MasterSeederServiceBlockingStub stub = MasterSeederServiceGrpc.newBlockingStub(channel);
        String filename = file.getString("file");
        SeederEndpointInfo info = stub.createSeeder(FileInfo.newBuilder().setFilename(filename).build());
        return new SeederBean(file.getString("file"), "localhost:"+info.getPort(), 900, 23,new ArrayList<String>(Arrays.asList("hah", "HEHE")));
    }
}
