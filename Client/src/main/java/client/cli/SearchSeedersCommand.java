package client.cli;

import datamodels.Seeder;

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
        Response response = target.path("seeder/search")
                .request(MediaType.APPLICATION_JSON)
                .get(Response.class);
        if(response.getStatus() == 200) {
            ArrayList<Seeder> list = response.readEntity(new GenericType<ArrayList<Seeder>>(){});
            for (Seeder i : list) {
                System.out.println(i);
            }
        }
    }
}
