package client;

import com.google.protobuf.ByteString;

public class File {
    private String filename;
    private byte[] data;
    private int size;
    private int chunkSize;
    private int numChunks;
    private boolean[] hasChunk;

    public File(String filename, byte[] data, int size, int chunkSize) {
        this.filename = filename;
        this.data = data;
        this.size = size;
        this.chunkSize = chunkSize;
        this.numChunks = calculateNumChunks(size, chunkSize);
        this.hasChunk = new boolean[numChunks];
    }

    public int calculateNumChunks(int size, int chunkSize){
        for(int numChunks = 0;;){
            numChunks = numChunks + 1;
            if(numChunks*chunkSize >= size){
                return numChunks;
            }
        }
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
}
