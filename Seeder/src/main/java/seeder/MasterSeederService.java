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
        Seeder seeder = new Seeder(filename);
        seeders.add(seeder);
        System.out.println("Created seeder on port " + seeder.getPort());
        // TODO: Change localhost
        responseObserver.onNext(Endpoint.newBuilder().setPort(seeder.getPort()).setAddress("localhost").build());
        responseObserver.onCompleted();
    }
}
