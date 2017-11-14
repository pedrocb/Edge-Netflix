package seeder;

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
        System.out.println("Request received");
        String filename = request.getFilename();
        int chunkSize = request.getChunkSize();
        Seeder seeder = new Seeder(filename, chunkSize);
        //TODO: Check if seeder was created with success
        seeders.add(seeder);
        System.out.println("Created seeder on port " + seeder.getPort());
        String address = MasterSeeder.config.getProperty("address", "localhost");
        responseObserver.onNext(Endpoint.newBuilder().setPort(seeder.getPort()).setAddress(address).build());
        responseObserver.onCompleted();
    }
}
