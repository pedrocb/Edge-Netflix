package seeder;

import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketListOption;
import com.google.cloud.storage.StorageOptions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Seeder {
    public static void main(String[] args) {
        System.out.println("Downloading file...");
        try {
            downloadFile("video-files-grupoc", "tl_512kb.mp4");
        } catch (IOException e) {
            System.out.println("NOPE");
        }
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
