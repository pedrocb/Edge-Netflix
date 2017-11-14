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
        HashMap<String, Integer> messagesFailed = new HashMap<>();
        ArrayList<SeederBean> seeders = null;
        while (running) {
            ArrayList<SeederBean> removedSeeders = new ArrayList<>();
            try {
                seeders = Database.getAllSeeders();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for (SeederBean seeder : seeders) {
                ManagedChannel channel = ManagedChannelBuilder.forTarget(seeder.getEndpoint()).usePlaintext(true).build();
                SeederServiceGrpc.SeederServiceBlockingStub stub = SeederServiceGrpc.newBlockingStub(channel);
                if (!messagesFailed.containsKey(seeder.getEndpoint())) {
                    messagesFailed.put(seeder.getEndpoint(), 0);
                }
                try {
                    HealthResponse response = stub.withDeadlineAfter(10, TimeUnit.SECONDS).healthCheck(Empty.getDefaultInstance());
                    messagesFailed.replace(seeder.getEndpoint(), 0);
                    System.out.println(response);
                } catch (StatusRuntimeException exc) {
                    try {
                        if (messagesFailed.get(seeder.getEndpoint()) >= 3) {
                            System.out.println("Deleting seeder " + seeder + seeder.getId());
                            Database.removeSeeder(seeder.getId());
                            removedSeeders.add(seeder);
                        }
                        messagesFailed.replace(seeder.getEndpoint(), messagesFailed.get(seeder.getEndpoint()) + 1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }
            for (SeederBean s : removedSeeders) {
                messagesFailed.remove(s.getEndpoint());
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
