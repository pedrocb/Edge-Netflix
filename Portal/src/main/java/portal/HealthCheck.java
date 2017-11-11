package portal;

import core.Empty;
import core.HealthResponse;
import core.SeederServiceGrpc;
import datamodels.SeederBean;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class HealthCheck extends Thread {

    private Boolean running;

    public HealthCheck() {
        running = true;
    }

    @Override
    public void run() {
        System.out.println("Health Check started...");
        HashMap<String, SeederServiceGrpc.SeederServiceBlockingStub> stubs = new HashMap<>();
        ArrayList<SeederBean> seeders = null;
        while (running) {
            try {
                seeders = Database.getAllSeeders();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for(SeederBean seeder : seeders) {
                System.out.println("Checking " + seeder.getEndpoint());
                SeederServiceGrpc.SeederServiceBlockingStub stub = stubs.get(seeder.getEndpoint());
                if(stub== null) {
                    ManagedChannel channel = ManagedChannelBuilder.forTarget(seeder.getEndpoint()).usePlaintext(true).build();
                    stub = SeederServiceGrpc.newBlockingStub(channel);
                    stubs.put(seeder.getEndpoint(), stub);
                }
                try {
                    HealthResponse response = stub.withDeadlineAfter(10, TimeUnit.SECONDS).healthCheck(Empty.getDefaultInstance());
                    System.out.println(response);
                } catch (StatusRuntimeException exc) {
                    try {
                        System.out.println("Deleting seeder " + seeder + seeder.getId());
                        Database.removeSeeder(seeder.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
