package portal.seeder;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class Seeder {
    private String name;
    private String endpoint;
    private int videoSize;
    private int bitrate;
    private ArrayList<String> keywords;


    public Seeder() {

    }

    public Seeder(String name, String endpoint, int videoSize, int bitrate, ArrayList<String> keywords) {
        this.name = name;
        this.endpoint = endpoint;
        this.videoSize = videoSize;
        this.bitrate = bitrate;
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return name + " " + endpoint + " " + videoSize + " " + bitrate + " " + keywords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(int videoSize) {
        this.videoSize = videoSize;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }
}
