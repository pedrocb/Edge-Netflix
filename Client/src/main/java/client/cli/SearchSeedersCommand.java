package client.cli;

import datamodels.FileBean;
import datamodels.SeederBean;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

public class SearchSeedersCommand implements Command{
    private String[] keywords;

    public SearchSeedersCommand(String[] keywords) {
        this.keywords = keywords;
    }

    public void run(WebTarget target) {
        try {
            Response response = target.path("seeder/search")
                    .queryParam("keyword", keywords)
                    .request(MediaType.APPLICATION_JSON)
                    .get(Response.class);
            if (response.getStatus() == 200) {
                ArrayList<FileBean> list = response.readEntity(new GenericType<ArrayList<FileBean>>() {
                });
                for (FileBean i : list) {
                    System.out.println(i);
                }
            } else {
                System.out.println("[Error] There was an error in the portal.");
            }
        } catch (ProcessingException e) {
            System.out.println("[Error]: Can't connect to Portal");
        }

    }
}
