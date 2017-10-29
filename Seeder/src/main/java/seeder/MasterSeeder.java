package seeder;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class MasterSeeder {
    private Server server;

    public MasterSeeder() {
        this.server = ServerBuilder.forPort(9998).addService(new MasterSeederService()).build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Master Seeder on");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may has been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                MasterSeeder.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if(server!=null)
            server.shutdown();
    }

    public static void main(String[] args) {
        MasterSeeder ms = new MasterSeeder();
        try {
            ms.start();
            ms.blockUntilShutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if(server != null) {
            server.awaitTermination();
        }
    }
}
