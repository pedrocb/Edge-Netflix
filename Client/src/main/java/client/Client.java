package client;

import client.chunk.Listener;
import client.chunk.SendChunkService;
import client.cli.CLI;
import io.grpc.ServerBuilder;

import java.util.ArrayList;

public class Client {
    private Listener listener;
    private ArrayList<File> files;

    public Client() {
        byte [] string = null;
        try{
            string = "Hello World".getBytes("UTF-8");
        } catch (Exception e){
            e.printStackTrace();
        }

        files = new ArrayList<>();
        listener = new Listener(files);
        int port = listener.getPort();
        listener.start();
        new CLI(files, port);
    }
    public static void main(String[] args) {
        new Client();
    }
}
