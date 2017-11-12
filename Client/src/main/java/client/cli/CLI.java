package client.cli;


import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Scanner;

import client.File;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class CLI {
    private boolean running = true;
    private String input;
    private ArrayList<File> files;
    private int port;

    public CLI(ArrayList files, int port) {
        this.files = files;
        Client httpclient = ClientBuilder.newClient()
                .property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true)
                .register(MOXyJsonProvider.class);
        WebTarget target = httpclient.target("http://localhost:9999");

        Scanner scanner = new Scanner(System.in);
        Command command = null;

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
                command = new DownloadFileCommand(filename, files, port);
                command.run(target);
            } else if(input.equals("list files")) {
                System.out.println(files);
            } else if(input.equals("quit")){
                running = false;
            } else {
                System.out.println("Bad usage");
                continue;
            }
        }
    }
}
