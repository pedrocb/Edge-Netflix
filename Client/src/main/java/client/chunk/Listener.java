package client.chunk;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.Console;
import java.io.File;
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
       port = selectPort(9000);
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
    public int selectPort(int port){
        try {
            System.out.println("trying on oport "+port);
            server = ServerBuilder.forPort(port).addService(new SendChunkService(files)).build();
            server.start();
        } catch (Exception e){
            port = selectPort(port+1);
        }
        return port;
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
