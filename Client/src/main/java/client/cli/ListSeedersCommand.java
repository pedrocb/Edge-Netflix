package client.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
//import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import jdk.nashorn.internal.parser.JSONParser;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.glassfish.jersey.client.JerseyClient;
import portal.seeder.Seeder;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class ListSeedersCommand implements Runnable{
    public ListSeedersCommand() {

    }

    public void run() {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(MOXyJsonProvider.class);
        Client client = Client.create(cc);
        ArrayList<Seeder> list = client.resource("http://localhost:9999")
                .path("seeder/list")
                .accept(MediaType.APPLICATION_JSON)
                .get(new GenericType<ArrayList<Seeder>>(){});
        for(Seeder i : list) {
            System.out.println(i);
        }
    }
}
