package seeder;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MasterSeeder {
    private Server server;
    public static Properties config;

    public MasterSeeder(int port) {
        this.server = ServerBuilder.forPort(port).addService(new MasterSeederService()).build();
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
        try {
            loadConfig();
        } catch (IOException e) {
            System.out.println("[Error] Missing masterSeeder.config");
            return;
        }
        int port;
        try {
            port = Integer.parseInt(config.getProperty("port", "9998"));
        } catch (NumberFormatException e){
            System.out.println("[Error] Port must be an integer");
            return;
        }
        try {
            MasterSeeder ms = new MasterSeeder(port);
            ms.start();
            ms.blockUntilShutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[Error] Port already in use");
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if(server != null) {
            server.awaitTermination();
        }
    }

    public static void loadConfig() throws IOException {
        config = new Properties();
        FileInputStream configFile = new FileInputStream("masterSeeder.config");
        config.load(configFile);
        configFile.close();
    }
}
