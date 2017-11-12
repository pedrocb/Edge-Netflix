package client;

import com.google.protobuf.ByteString;
import core.Endpoint;

import java.util.ArrayList;

public class File {
    private String filename;
    private byte[] data;
    private int size;
    private int chunkSize;
    private int numChunks;
    private boolean[] hasChunk;

    private ArrayList<Endpoint> peers;
    private String[] hashes;

    public File(String filename, int size, int chunkSize, ArrayList<Endpoint> peers) {
        this.filename = filename;
        this.size = size;
        this.chunkSize = chunkSize;
        this.numChunks = (int) Math.ceil((float)size/chunkSize);
        this.hasChunk = new boolean[numChunks];
        this.data = new byte[size];
        this.peers = peers;
    }

    public boolean isDownloaded(){
        for(boolean value : hasChunk ){
            if(!value){
                return false;
            }
        }
        return true;
    }

    public boolean hasChunkAt(int index){
        return this.hasChunk[index];
    }

    public void setChunkAt(int index){
        this.hasChunk[index] = true;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getNumChunks() {
        return numChunks;
    }

    public void setNumChunks(int numChunks) {
        this.numChunks = numChunks;
    }

    public boolean[] getHasChunk() {
        return hasChunk;
    }

    public void setHasChunk(boolean[] hasChunk) {
        this.hasChunk = hasChunk;
    }

    public String[] getHashes() {
        return hashes;
    }

    public void setHashes(String[] hashes) {
        this.hashes = hashes;
    }

    @Override
    public String toString() {
        return filename + " " + size + " " + chunkSize;
    }
}
