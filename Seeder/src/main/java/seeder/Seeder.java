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


public class Seeder {

    private Server server;
    private ArrayList<File> files;
    private ArrayList<byte[]> hashes;
    private String filename;
    private int chunkSize;
    public boolean isOk;

    public Seeder(String filename, int chunkSize) {
        this.filename = filename;
        this.chunkSize = chunkSize;
        this.files = new ArrayList<>();
        this.hashes = new ArrayList<>();
        SeederService seederService = new SeederService(hashes);
        int minPort = Integer.parseInt(MasterSeeder.config.getProperty("minPort", "9985"));
        int maxPort = Integer.parseInt(MasterSeeder.config.getProperty("maxPort", "9995"));

        isOk = startServer(seederService, maxPort, minPort);
        //TODO: Desligar Seeder
        //TODO: Retirar clients desligados
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
        while (startIndex < fileLength) {
            try {
                int chunkMaxSize = file.getChunkSize();
                endIndex = startIndex + chunkMaxSize;
                if (endIndex > fileLength) {
                    endIndex = fileLength;
                }
                byte[] chunk = Arrays.copyOfRange(file.getData(), startIndex, endIndex);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(chunk);
                hashes.add(hash);
                System.out.println("Hash from " + startIndex + " to " + endIndex + " size " + (endIndex - startIndex) + ": " + Base64.getEncoder().encodeToString(hash));
                startIndex += chunkMaxSize;
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}
