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

        File file = new File("tl_512kb.mp4", string, string.length, string.length/3);
        files = new ArrayList<>();
        files.add(file);
        listener = new Listener(files);
        System.out.println("port!!!"+listener.getPort());
        int port = listener.getPort();
        listener.start();
        new CLI(files, port);
    }
    public static void main(String[] args) {
        new Client();
    }
}
