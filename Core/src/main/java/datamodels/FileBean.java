package datamodels;

import java.util.ArrayList;

public class FileBean {
    private String name;
    private int size;
    private SeederBean seeder;
    private ArrayList<String> keywords;
    private int chunkSize;

    public FileBean() {

    }

    public FileBean(String name, int size, SeederBean seeder, ArrayList<String> keywords, int chunkSize) {
        this.name = name;
        this.size = size;
        this.seeder = seeder;
        this.keywords = keywords;
        this.chunkSize = chunkSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public SeederBean getSeeder() {
        return seeder;
    }

    public void setSeeder(SeederBean seeder) {
        this.seeder = seeder;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "Filename: " + name + " Size: " + size + " Keywords: " + keywords + "\n" + ((seeder != null) ? seeder : "");
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
