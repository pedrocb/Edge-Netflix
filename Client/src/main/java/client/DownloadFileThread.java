package client;

public class DownloadFileThread extends Thread {
    private File file;

    public DownloadFileThread(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        System.out.println("Starting " + file.getFilename() + " download!");
    }
}
