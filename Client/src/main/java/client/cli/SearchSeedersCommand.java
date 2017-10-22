package client.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters()
public class SearchSeedersCommand implements Runnable{
    @Parameter()
    private String[] keywords;

    public SearchSeedersCommand(String[] keywords) {
        this.keywords = keywords;
    }

    public void run() {
        System.out.println("Searching..");
        for (String i : keywords) {
            System.out.println(i);
        }
    }
}
