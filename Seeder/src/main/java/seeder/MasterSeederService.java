package seeder;

import core.HealthResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import core.FileInfo;
import core.MasterSeederServiceGrpc;
import core.Endpoint;

import java.util.ArrayList;
import java.util.Scanner;

public class MasterSeederService extends MasterSeederServiceGrpc.MasterSeederServiceImplBase {
    private ArrayList<Seeder> seeders;

    public MasterSeederService() {
       super();
       seeders = new ArrayList<>();
    }

    @Override
    public void createSeeder(FileInfo request, StreamObserver<Endpoint> responseObserver) {
        String filename = request.getFilename();
        int chunkSize = request.getChunkSize();
        Seeder seeder = new Seeder(filename, chunkSize);
        seeders.add(seeder);
        String address = MasterSeeder.config.getProperty("address", "localhost");
        if(seeder.isOk) {
            responseObserver.onNext(Endpoint.newBuilder().setPort(seeder.getPort()).setAddress(address).build());
            responseObserver.onCompleted();
            seeder.setup();
        } else {
            responseObserver.onError(Status.RESOURCE_EXHAUSTED.withCause(new Exception("All ports in use")).asException());
        }
    }
}
