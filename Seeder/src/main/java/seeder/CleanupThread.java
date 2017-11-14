package seeder;

import core.Endpoint;
import io.grpc.Server;

import java.util.ArrayList;
import java.util.HashMap;

public class CleanupThread extends Thread {
    private Server server;
    private HashMap<Endpoint, Integer> messagesFailed;
    private ArrayList<Endpoint> clients;
    private boolean running;

    @Override
    public void run() {
        int noClientsCount = 0;
        ArrayList<Endpoint> endpoints = new ArrayList<>();
        while (running) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            if(messagesFailed.keySet().isEmpty()) {
                noClientsCount++;
            } else {
                for (Endpoint address : messagesFailed.keySet()) {
                    if (messagesFailed.get(address) >= 3) {
                        System.out.println("Removing " + address);
                        clients.remove(address);
                        endpoints.add(address);
                    } else {
                        messagesFailed.compute(address, (endpoint, integer) -> integer + 1);
                    }
                }
                for(Endpoint endpoint : endpoints) {
                    messagesFailed.remove(endpoint);
                }
            }
            if(noClientsCount >= 3) {
                server.shutdown();
                running = false;
            }
        }
    }

    public CleanupThread(HashMap<Endpoint, Integer> messagesFailed, ArrayList<Endpoint> clients, Server server) {
        this.messagesFailed = messagesFailed;
        this.clients = clients;
        this.server = server;
        this.running = true;
    }
}
