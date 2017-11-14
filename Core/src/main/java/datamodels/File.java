package datamodels;

import core.Endpoint;

import java.util.ArrayList;

public class File {
    //TODO: Sync file write/reads
    private String filename;
    private byte[] data;
    private int size;
    private int chunkSize;
    private int numChunks;
    private boolean[] hasChunk;
    private ArrayList<Endpoint> peers;
    private String[] hashes;
    private boolean isDownloaded;
    private String path;

    public File(String filename, int size, int chunkSize, ArrayList<Endpoint> peers) {
        this.filename = filename;
        this.size = size;
        this.chunkSize = chunkSize;
        this.numChunks = (int) Math.ceil((float) size / chunkSize);
        this.hasChunk = new boolean[numChunks];
        this.data = new byte[size];
        this.peers = peers;
        this.isDownloaded = false;
    }

    public boolean isDownloaded() {
        synchronized (this) {
            return isDownloaded;
        }
    }

    public void setDownloaded(boolean downloaded) {
        synchronized (this) {
            isDownloaded = downloaded;
        }
    }

    public void updateIsDownloaded() {
        synchronized (this) {
            for (boolean value : hasChunk) {
                if (!value) {
                    isDownloaded = false;
                    return;
                }
            }
            isDownloaded = true;
        }
    }

    public boolean hasChunkAt(int index) {
        synchronized (this) {
            return this.hasChunk[index];
        }
    }

    public ArrayList<Endpoint> getPeers() {
        synchronized (this) {
            return peers;
        }
    }

    public void setPeers(ArrayList<Endpoint> peers) {
        synchronized (this) {
            this.peers = peers;
        }
    }

    public void setChunkAt(int index, boolean value) {
        synchronized (this) {
            this.hasChunk[index] = value;
            updateIsDownloaded();
        }
    }

    public String getFilename() {
        synchronized (this) {
            return filename;
        }
    }

    public void setFilename(String filename) {
        synchronized (this) {
            this.filename = filename;
        }

    }

    public byte[] getData() {
        synchronized (this) {
            return data;
        }
    }


    public void setData(byte[] data) {
        synchronized (this) {
            this.data = data;
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        synchronized (this) {
            this.size = size;
        }
    }

    public int getChunkSize() {
        synchronized (this) {
            return chunkSize;
        }
    }

    public void setChunkSize(int chunkSize) {
        synchronized (this) {
            this.chunkSize = chunkSize;
        }
    }

    public int getNumChunks() {
        synchronized (this) {
            return numChunks;
        }
    }

    public void setNumChunks(int numChunks) {
        synchronized (this) {
            this.numChunks = numChunks;
        }
    }

    public boolean[] getHasChunk() {
        synchronized (this) {
            return hasChunk;
        }
    }

    public void setHasChunk(boolean[] hasChunk) {
        synchronized (this) {
            this.hasChunk = hasChunk;
        }
    }

    public String[] getHashes() {
        synchronized (this) {
            return hashes;
        }
    }

    public void setHashes(String[] hashes) {
        synchronized (this) {
            this.hashes = hashes;
        }
    }

    public String basicInfo() {
        synchronized (this) {
            String result = filename + " " + (isDownloaded ? "Downloaded." : "Downloading...");
            return result;
        }
    }

    private int currentSize() {
        synchronized (this) {
            if (isDownloaded) {
                return size;
            }
            int count = 0;
            for (boolean i : hasChunk) {
                if (i) {
                    count++;
                }
            }
            return count * chunkSize;
        }
    }

    public String info() {
        synchronized (this) {
            String result = basicInfo() + "\n";
            result += "Video size (in bytes): " + currentSize() + "/" + size + "\n";
            if (isDownloaded) {
                result += "Full path: " + path + "\n";
            } else {
                result += "Peers: \n";
                for (Endpoint peer : peers) {
                    result += peer.getAddress() + ":" + peer.getPort() + "\n";
                }
            }
            return result;
        }
    }

    @Override
    public String toString() {
        synchronized (this) {
            return filename + " " + size + " " + chunkSize;
        }
    }

    public void setPath(String path) {
        synchronized (this) {
            this.path = path;
        }
    }

    public String getPath() {
        synchronized (this) {
            return path;
        }
    }

}
