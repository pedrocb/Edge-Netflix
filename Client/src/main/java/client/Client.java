package client;

import client.chunk.Listener;
import client.chunk.SendChunkService;
import client.cli.CLI;
import io.grpc.ServerBuilder;

public class Client {
    private Listener listener;

    public Client() {
        listener = new Listener();
        listener.start();
        new CLI();
    }
    public static void main(String[] args) {
        new Client();
    }
}
