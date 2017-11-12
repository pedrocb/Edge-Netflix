package seeder;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import core.Endpoint;
import datamodels.File;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.security.MessageDigest;
import java.util.Arrays;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;


public class Seeder {

    private Server server;
    private ArrayList<File> files;

    public Seeder(String filename, int chunkSize) {
        System.out.println("Starting " + filename + " download.");
        byte [] fileContent = downloadFile("video-files-groupc", filename);
        File file = new File(filename,fileContent.length,chunkSize,new ArrayList<Endpoint>());
        file.setData(fileContent);
        for(int i = 0; i<file.getNumChunks();i++){
            file.setChunkAt(i,true);
        }
        files = new ArrayList<>();
        files.add(file);
        ArrayList<byte[]> chunkHashes = calculateChunkHashes();
        SendChunkListener sendChunkListener = new SendChunkListener(files);
        sendChunkListener.start();
        int listeningPort = sendChunkListener.getPort();
        server = ServerBuilder.forPort(0).addService(new SeederService(chunkHashes, fileContent.length, file.getChunkSize(), listeningPort)).build();
        try {
            server.start();
        } catch (IOException e) {
            System.out.println("Failed to start Seeder");
            e.printStackTrace();
        }
    }

    public int getPort() {
        return server.getPort();
    }

    public byte [] downloadFile(String bucketName, String fileName) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Blob blob = storage.get(bucketName, fileName);
        if (blob == null) {
            return null;
        }
        return blob.getContent();
    }


    public ArrayList<byte []> calculateChunkHashes(){
        File file = files.get(0);
        int fileLength = file.getSize();
        int startIndex=0, endIndex;
        ArrayList<byte[]> chuckHashes = new ArrayList<>();
        while(startIndex < fileLength){
            try {
                int chunkMaxSize = file.getChunkSize();
                endIndex = startIndex + chunkMaxSize;
                if (endIndex > fileLength) {
                    endIndex = fileLength;
                }
                byte[] chunk = Arrays.copyOfRange(file.getData(), startIndex, endIndex);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(chunk);
                chuckHashes.add(hash);
                System.out.println("Hash from "+startIndex+" to "+endIndex+" size "+(endIndex-startIndex)+": "+ Base64.getEncoder().encodeToString(hash));
                startIndex += chunkMaxSize;
            } catch (Exception e) {
                System.out.println(e.toString());
            }

        }
        return chuckHashes;
    }
}
