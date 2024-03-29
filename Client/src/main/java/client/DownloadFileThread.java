package client;

import com.google.protobuf.ByteString;
import core.*;
import datamodels.File;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;

public class DownloadFileThread extends Thread {
    private int port;
    private File file;
    private String seederEndpoint;
    private ArrayList<File> files;

    public DownloadFileThread(File file, String seederEndpoint, int port, ArrayList<File> files) {
        this.files = files;
        this.file = file;
        this.seederEndpoint = seederEndpoint;
        this.port = port;
    }

    @Override
    public void run() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(seederEndpoint).usePlaintext(true).build();
        SeederServiceGrpc.SeederServiceBlockingStub stub = SeederServiceGrpc.newBlockingStub(channel);
        String address = Client.config.getProperty("address", "localhost");
        Endpoint endpoint = Endpoint.newBuilder().setAddress(address).setPort(port).build();
        JoinResponse joinResponse = null;
        try{
            joinResponse = stub.joinSwarm(endpoint);
        } catch (Exception e){
            System.out.println("Could not connect to seeder");
            files.remove(file);
            return;
        }

        file.setPeers(new ArrayList<>(joinResponse.getClientsList()));
        String[] hashes = new String[file.getNumChunks()];
        for (int i = 0; i < joinResponse.getHashesList().size(); i++) {
            hashes[i] = Base64.getEncoder().encodeToString(joinResponse.getHashesList().get(i).toByteArray());
        }
        file.setHashes(hashes);
        file.setFileHash(Base64.getEncoder().encodeToString(joinResponse.getFileHash().toByteArray()));

        ArrayList<Integer> missingChunksIndex = new ArrayList<>();
        for (int i = 0; i < file.getNumChunks(); i++) {
            missingChunksIndex.add(i);
        }
        new UpdatePeersThread(file, seederEndpoint, port).start();
        while (!file.isDownloaded()) {
            int chosenMissingIndex = (int) (Math.random() * missingChunksIndex.size());
            int chunkIndex = missingChunksIndex.get(chosenMissingIndex);
            ArrayList<Endpoint> neighbours = (ArrayList<Endpoint>) file.getPeers().clone();
            boolean fetched = false;
            while (!fetched && !neighbours.isEmpty()) {
                Endpoint neighbour = neighbours.get((int) (Math.random() * neighbours.size()));
                try {
                    downloadChunk(neighbour, chunkIndex);
                    missingChunksIndex.remove(chosenMissingIndex);
                    fetched = true;
                } catch (Exception e) {
                    neighbours.remove(neighbour);
                }
            }
            if(neighbours.isEmpty()){
                System.out.println("Error acessing seeder.");
                files.remove(file);
                return;
            }
        }
        if (!calculateChunkHash(file.getData()).equals(file.getFileHash())) {
            System.out.println("File hash doesn't match ");
            files.remove(files);
            return;
        }
        System.out.println("File " + file.getFilename() + " finished download!");
        try {
            //TODO: File path
            FileOutputStream fos = new FileOutputStream("video-files/" + file.getFilename());
            fos.write(file.getData());
            fos.close();
            file.setPath("video-files/" + file.getFilename());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Was not able to write file");
            files.remove(file);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to write file");
            files.remove(file);
        }
    }

    void downloadChunk(Endpoint neighbour, int chunkIndex) throws Exception {
        int chunkSize = file.getChunkSize();
        ManagedChannel clientChannel = ManagedChannelBuilder.forTarget(neighbour.getAddress() + ":" + neighbour.getPort()).usePlaintext(true).build();
        SendChunkServiceGrpc.SendChunkServiceBlockingStub clientStub = SendChunkServiceGrpc.newBlockingStub(clientChannel);
        Request request = Request.newBuilder().setFilename(file.getFilename()).setIndex(chunkIndex).build();
        Chunk recievedChunk = clientStub.requestChunk(request);
        ByteString chunk = recievedChunk.getData();
        byte[] receivedBytes = chunk.toByteArray();
        if (!calculateChunkHash(receivedBytes).equals(file.getHashes()[chunkIndex])) {
            throw new Exception("Hash doesn't match.");
        }
        byte[] bytes = file.getData();
        for (int i = 0; i < receivedBytes.length; i++) {
            bytes[chunkSize * chunkIndex + i] = receivedBytes[i];
        }
        file.setData(bytes);
        file.setChunkAt(chunkIndex, true);
    }

    public String calculateChunkHash(byte[] chunk) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(chunk);
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
