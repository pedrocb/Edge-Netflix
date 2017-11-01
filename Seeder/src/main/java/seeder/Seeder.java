package seeder;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Seeder {

    private Server server;
    private byte[] fileContent;

    public Seeder(String filename) {
        server = ServerBuilder.forPort(0).build();
        //downloadFile("video-files-groupc", filename);
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
}
