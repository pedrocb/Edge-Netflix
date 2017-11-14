package client;

import core.*;
import datamodels.File;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;

public class UpdatePeersThread extends Thread {
    private String seederEndpoint;
    private File file;
    private int port;

    public UpdatePeersThread(File file, String seederEndpoint, int port) {
        this.file = file;
        this.seederEndpoint = seederEndpoint;
        this.port = port;
    }

    public void run() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(seederEndpoint).usePlaintext(true).build();
        SeederServiceGrpc.SeederServiceBlockingStub stub = SeederServiceGrpc.newBlockingStub(channel);
        String address = Client.config.getProperty("address", "localhost");
        Endpoint endpoint = Endpoint.newBuilder().setAddress(address).setPort(port).build();
        while (true) {
            ClientList list = stub.updateList(endpoint);
            ArrayList<Endpoint> peers = new ArrayList<>(list.getClientsList());
            peers.removeIf(endpoint1 -> endpoint1.getAddress().equals(address) && endpoint1.getPort() == port);
            file.setPeers(peers);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
