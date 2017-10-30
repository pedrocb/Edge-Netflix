package seeder;

import io.grpc.stub.StreamObserver;
import masterSeeder.FileInfo;
import masterSeeder.MasterSeederServiceGrpc;
import masterSeeder.SeederEndpointInfo;

import java.util.ArrayList;

public class MasterSeederService extends MasterSeederServiceGrpc.MasterSeederServiceImplBase {
    private ArrayList<Seeder> seeders;

    public MasterSeederService() {
       super();
       seeders = new ArrayList<>();
    }

    @Override
    public void createSeeder(FileInfo request, StreamObserver<SeederEndpointInfo> responseObserver) {
        String filename = request.getFilename();
        Seeder seeder = new Seeder(filename);
        seeders.add(seeder);
        responseObserver.onNext(SeederEndpointInfo.newBuilder().setPort(seeder.getPort()).build());
        responseObserver.onCompleted();
    }
}