package seeder;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.security.MessageDigest;
import java.util.Arrays;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;


public class Seeder {

    private Server server;
    private byte[] fileContent;
    private int chunkMaxSize = 1024*1024;

    public Seeder(String filename) {
        downloadFile("video-files-groupc", filename);
        System.out.println("Video Size = " + fileContent.length);
        ArrayList<byte[]> chunkHashes = calculateChunkHashes();
        server = ServerBuilder.forPort(0).addService(new SeederService(chunkHashes, fileContent.length, chunkMaxSize)).build();
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

    public void downloadFile(String bucketName, String fileName) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Blob blob = storage.get(bucketName, fileName);
        if (blob == null) {
            return;
        }
        fileContent = blob.getContent();
    }


    public ArrayList<byte []> calculateChunkHashes(){
        int fileLength = fileContent.length;
        int startIndex=0, endIndex;
        ArrayList<byte[]> chuckHashes = new ArrayList<>();
        while(startIndex < fileLength){
            try {
                endIndex = startIndex + chunkMaxSize;
                if (endIndex > fileLength) {
                    endIndex = fileLength;
                }
                byte[] chunk = Arrays.copyOfRange(fileContent, startIndex, endIndex);
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
