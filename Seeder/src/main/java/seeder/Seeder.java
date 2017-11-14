package seeder;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import core.Endpoint;
import datamodels.File;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import services.SendChunkService;

import java.security.MessageDigest;
import java.util.Arrays;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;


public class Seeder {

    private Server server;
    private ArrayList<File> files;
    private ArrayList<byte[]> hashes;
    private String filename;
    private int chunkSize;
    private HashMap<Endpoint, Integer> messagesFailed;
    private SeederService seederService;
    public boolean isOk;

    public Seeder(String filename, int chunkSize) {
        this.filename = filename;
        this.chunkSize = chunkSize;
        this.files = new ArrayList<>();
        this.hashes = new ArrayList<>();
        this.messagesFailed = new HashMap<>();
        this.seederService = new SeederService(hashes, messagesFailed);
        int minPort = Integer.parseInt(MasterSeeder.config.getProperty("minPort", "9985"));
        int maxPort = Integer.parseInt(MasterSeeder.config.getProperty("maxPort", "9995"));

        isOk = startServer(seederService, maxPort, minPort);
    }

    public void setup() {
        synchronized (hashes) {
            synchronized (files) {
                System.out.println("Starting " + filename + " download.");
                byte[] fileContent = downloadFile("video-files-groupc", filename);
                System.out.println("Print 5");
                File file = new File(filename, fileContent.length, chunkSize, new ArrayList<Endpoint>());
                file.setData(fileContent);
                for (int i = 0; i < file.getNumChunks(); i++) {
                    file.setChunkAt(i, true);
                }
                files.add(file);
                new CleanupThread(messagesFailed, seederService.getClients(),server).start();
            }
            calculateChunkHashes();
        }
    }

    public boolean startServer(SeederService seederService, int maxPort, int port) {
        server = ServerBuilder.forPort(port)
                .addService(seederService)
                .addService(new SendChunkService(files))
                .build();
        try {
            server.start();
            seederService.addSeederToClients(port);
            return true;
        } catch (IOException e) {
            System.out.println(port + "failed");
            if (port < maxPort) {
                System.out.println("Trying port " + (port + 1));
                return startServer(seederService, maxPort, port + 1);
            } else {
                return false;
            }
        }
    }

    public int getPort() {
        return server.getPort();
    }

    public byte[] downloadFile(String bucketName, String fileName) {
        System.out.println("Print 1");
        Storage storage = StorageOptions.newBuilder().build().getService();
        System.out.println("Print 2");
        Blob blob = storage.get(bucketName, fileName);
        System.out.println("Print 3");
        if (blob == null) {
            return null;
        }
        System.out.println("Print 4");
        return blob.getContent();
    }


    public void calculateChunkHashes() {
        File file = files.get(0);
        int fileLength = file.getSize();
        int startIndex = 0, endIndex;
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            System.out.println(e.toString());
            return;
        }
        while (startIndex < fileLength) {
                int chunkMaxSize = file.getChunkSize();
                endIndex = startIndex + chunkMaxSize;
                if (endIndex > fileLength) {
                    endIndex = fileLength;
                }
                byte[] chunk = Arrays.copyOfRange(file.getData(), startIndex, endIndex);
                byte[] hash = digest.digest(chunk);
                hashes.add(hash);
                System.out.println("Hash from " + startIndex + " to " + endIndex + " size " + (endIndex - startIndex) + ": " + Base64.getEncoder().encodeToString(hash));
                startIndex += chunkMaxSize;
        }
        byte[] hash = digest.digest(file.getData());
        System.out.println(file.getData().length);
        hashes.add(hash);
        System.out.println("File hash = " + Base64.getEncoder().encodeToString(hash));
    }
}
