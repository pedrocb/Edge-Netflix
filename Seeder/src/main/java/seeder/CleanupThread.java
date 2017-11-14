package seeder;

import core.Endpoint;

import java.util.ArrayList;
import java.util.HashMap;

public class CleanupThread extends Thread {
    private HashMap<Endpoint, Integer> messagesFailed;
    private ArrayList<Endpoint> clients;

    @Override
    public void run() {
        ArrayList<Endpoint> endpoints = new ArrayList<>();
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            System.out.println(messagesFailed);
            for(Endpoint address : messagesFailed.keySet()) {
                if(messagesFailed.get(address) >= 3) {
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
    }

    public CleanupThread(HashMap<Endpoint, Integer> messagesFailed, ArrayList<Endpoint> clients) {
        this.messagesFailed = messagesFailed;
        this.clients = clients;
    }
}
