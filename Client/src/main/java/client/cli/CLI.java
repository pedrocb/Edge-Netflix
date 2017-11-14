package client.cli;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import datamodels.File;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.glassfish.jersey.CommonProperties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class CLI {
    private boolean running = true;
    private String input;
    private ArrayList<File> files;

    public CLI(ArrayList files, int listenerPort) {
        this.files = files;
        Client httpclient = ClientBuilder.newClient()
                .property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true)
                .register(MOXyJsonProvider.class);
        String endpoint = client.Client.config.getProperty("portalEndpoint", "http://localhost:9999");
        WebTarget target = httpclient.target(endpoint);

        Scanner scanner = new Scanner(System.in);
        Command command;

        while (running) {
            System.out.println("COMMANDS:");
            System.out.println("seeder list");
            System.out.println("seeder search <keys>");
            System.out.println("download <file>");
            System.out.println("list files");
            System.out.println("info <file>");
            System.out.println("play <file>");
            System.out.println("quit");
            input = scanner.nextLine();
            if (input.equals("seeder list")) {
                command = new ListSeedersCommand();
                command.run(target);
            } else if (input.startsWith("seeder search ")) {
                String[] keywords = input.replace("seeder search ", "").split(" ");
                command = new SearchSeedersCommand(keywords);
                command.run(target);
            } else if(input.startsWith("download ")) {
                String filename = input.replace("download ", "");
                command = new DownloadFileCommand(filename, files, listenerPort);
                command.run(target);
            } else if(input.equals("list files")) {
                listFiles();
            } else if(input.startsWith("info ")) {
                String filename = input.replace("info ", "");
                infoFile(filename);
            } else if(input.startsWith("play ")) {
                String filename = input.replace("play ", "");
                playFile(filename);
            } else if(input.equals("quit")){
                System.exit(0);
            } else {
                System.out.println("Bad usage.");
                continue;
            }
        }
    }

    private void playFile(String filename) {
        File file = getFile(filename);
        if(file != null && file.isDownloaded()) {
            ProcessBuilder processBuilder = new ProcessBuilder("ffplay", file.getPath());
            try {
                processBuilder.start();
            } catch (IOException e) {
                System.out.println("There was a problem playing the video.");
            }
        }
        else {
            System.out.println("You don't have a file with that name.");
        }
    }

    private void infoFile(String filename) {
        File file = getFile(filename);
        if(file != null) {
            System.out.println(file.info());
        }
        else {
            System.out.println("You don't have a file with that name.");
        }
    }

    private void listFiles() {
        if(files.isEmpty()){
            System.out.println("No files.");
            return;
        }
        for (File file : files) {
            System.out.println(file.basicInfo());
        }
    }

    private File getFile(String filename) {
        for(File file : files) {
            if(file.getFilename().equals(filename)) {
                return file;
            }
        }
        return null;
    }
}
