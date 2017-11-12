package seeder;

import datamodels.File;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import services.SendChunkService;

import java.util.ArrayList;

public class SendChunkListener extends Thread{
    private Server server;
    private ArrayList<File> files;

    public SendChunkListener(ArrayList files){
        this.files = files;
        try {
            server = ServerBuilder.forPort(0).addService(new SendChunkService(files)).build();
            server.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            startServer();
            blockUntilShutdown();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startServer () throws Exception{
        System.out.println("Listener started on port "+server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may has been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                SendChunkListener.this.stopServer();
                System.err.println("*** server shut down");
            }
        });
    }

    public int getPort() {
        return server.getPort();
    }

    private void stopServer() {
        if(server!=null)
            server.shutdown();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if(server != null) {
            server.awaitTermination();
        }
    }
}
