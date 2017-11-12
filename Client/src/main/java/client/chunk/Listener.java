package client.chunk;

import datamodels.File;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import services.SendChunkService;

import java.io.IOException;
import java.util.ArrayList;

public class Listener extends Thread{
    private Server server;

    public int getPort() {
        return port;
    }

    private int port;
    private ArrayList<File> files;

    public Listener(ArrayList files){
       this.files = files;
       server = ServerBuilder.forPort(0).addService(new SendChunkService(files)).build();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        port = server.getPort();
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
        System.out.println("Listener started on port "+port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may has been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                Listener.this.stopServer();
                System.err.println("*** server shut down");
            }
        });
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
