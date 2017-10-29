package portal.file;

import datamodels.SeederBean;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import portal_seeder.SeederInputP;
import portal_seeder.SeederOutputP;
import portal_seeder.SeederServicePGrpc;
import portal_seeder.SeederServicePGrpc.SeederServicePBlockingStub;

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
        SeederServicePBlockingStub stub = SeederServicePGrpc.newBlockingStub(channel);
        SeederOutputP seeder = stub.start(SeederInputP.newBuilder().setFilename("test").build());
        System.out.println(seeder);
        return new SeederBean(file.getString("file"), seeder.getEndpoint(), 900, 23,new ArrayList<String>(Arrays.asList("hah", "HEHE")));
    }
}
