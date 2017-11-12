package client;

import com.google.protobuf.ByteString;
import core.Chunk;
import core.Endpoint;
import core.Request;
import core.SendChunkServiceGrpc;
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
    private File file;

    public DownloadFileThread(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        System.out.println("Starting " + file.getFilename() + " download!");
        ArrayList<Integer> missingChunksIndex = new ArrayList<>();
        for (int i = 0; i < file.getNumChunks(); i++) {
            missingChunksIndex.add(i);
        }
        while (!file.isDownloaded()) {
            int chosenMissingIndex = (int) (Math.random() * missingChunksIndex.size());
            int chunkIndex = missingChunksIndex.get(chosenMissingIndex);
            Endpoint neighbour = file.getPeers().get((int) (Math.random() * file.getPeers().size()));
            try {
                System.out.println("Starting download of chunk " + chunkIndex + " from " + neighbour.getAddress() + ":" + neighbour.getPort());
                downloadChunk(neighbour, chunkIndex);
                System.out.println("Got chunk " + chunkIndex + " from " + neighbour.getAddress() + ":" + neighbour.getPort());
                missingChunksIndex.remove(chosenMissingIndex);
            } catch (Exception e) {
                System.out.println("Could not get chunk " + chunkIndex + " from " + neighbour.getAddress() + ":" + neighbour.getPort());
            }
        }
        System.out.println("File " + file.getFilename() + " finished download!");
        try {
            FileOutputStream fos = new FileOutputStream("video-files/"+file.getFilename());
            fos.write(file.getData());
            fos.close();
            System.out.println("File written.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        System.out.println("Got bytes, size: " + receivedBytes.length);
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
