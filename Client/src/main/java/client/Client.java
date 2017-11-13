package client;

import client.chunk.Listener;
import client.cli.CLI;
import datamodels.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Client {
    private Listener listener;
    private ArrayList<File> files;
    public static Properties config;

    public Client() {
        files = new ArrayList<>();
        listener = new Listener(files);
        int port = listener.getPort();
        listener.start();
        new CLI(files, port);
    }
    public static void main(String[] args) {
        try {
            loadConfig();
            new Client();
        } catch (IOException e) {
            System.out.println("[ERROR] Missing client.config..");
        }
    }

    public static void loadConfig() throws IOException {
        config = new Properties();
        FileInputStream configFile = new FileInputStream("client.config");
        config.load(configFile);
        configFile.close();
    }
}
