package portal;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class Portal {

    public static void main(String[] args) throws Exception {
        ResourceConfig config = new ResourceConfig()
                .packages("portal");

        Server jettyServer = new Server(9999);
        ServletContextHandler context = new ServletContextHandler(jettyServer, "/*");
        context.addServlet(new ServletHolder(new ServletContainer(config)), "/*");
        NCSARequestLog requestLog = new NCSARequestLog();
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT");
        requestLog.setLogLatency(true);
        requestLog.setRetainDays(90);

        jettyServer.setRequestLog(requestLog);


        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            e.printStackTrace();
            jettyServer.stop();
            jettyServer.destroy();
        }
    }

}
