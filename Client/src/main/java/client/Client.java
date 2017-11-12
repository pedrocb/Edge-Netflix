package client;

import client.chunk.Listener;
import client.cli.CLI;
import datamodels.File;

import java.util.ArrayList;

public class Client {
    private Listener listener;
    private ArrayList<File> files;

    public Client() {
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
