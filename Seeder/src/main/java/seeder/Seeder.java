package seeder;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Seeder {

    private final int port;
    private final Server server;
    public Seeder(int port) {
        this.port = port;
        server = ServerBuilder.forPort(9998).addService(new SeederServiceP()).build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Server started");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may has been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                Seeder.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    private void stop() {
        if (server != null)
            server.shutdown();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Seeder seeder = new Seeder(9998);
        seeder.start();
        seeder.blockUntilShutdown();

        /*System.out.println("Downloading file...");
        try {
            downloadFile("video-files-grupoc", "tl_512kb.mp4");
        } catch (IOException e) {
            System.out.println("NOPE");
        }*/
    }

    public static boolean downloadFile(String bucketName, String fileName) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Blob blob = storage.get(bucketName, fileName);
        if (blob == null) {
            return false;
        }
        ReadChannel readChannel = blob.reader();
        FileOutputStream fileOuputStream = new FileOutputStream("test_file");
        fileOuputStream.getChannel().transferFrom(readChannel, 0, Long.MAX_VALUE);
        fileOuputStream.close();
        return true;
    }
}
