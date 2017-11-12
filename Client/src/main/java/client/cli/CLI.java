package client.cli;


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
    private int listenerPort;

    public CLI(ArrayList files, int listenerPort) {
        this.files = files;
        Client httpclient = ClientBuilder.newClient()
                .property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true)
                .register(MOXyJsonProvider.class);
        String endpoint = client.Client.config.getProperty("portalEndpoint", "http://localhost:9999");
        System.out.println(endpoint);
        WebTarget target = httpclient.target(endpoint);

        Scanner scanner = new Scanner(System.in);
        Command command;

        while (running) {
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
                System.out.println(files);
            } else if(input.equals("quit")){
                running = false;
            } else {
                System.out.println("Bad usage.");
                continue;
            }
        }
    }
}
