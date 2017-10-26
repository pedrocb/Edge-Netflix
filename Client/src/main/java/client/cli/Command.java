package client.cli;


import javax.ws.rs.client.WebTarget;

public interface Command {
    public void run(WebTarget target);
}
