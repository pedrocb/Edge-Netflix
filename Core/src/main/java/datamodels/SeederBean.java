package datamodels;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class SeederBean {
    private String endpoint;
    private int bitrate;
    private int id;

    public SeederBean() {

    }

    public SeederBean(String endpoint, int bitrate) {
        this.endpoint = endpoint;
        this.bitrate = bitrate;
    }


    @Override
    public String toString() {
        return endpoint + " " + bitrate;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
