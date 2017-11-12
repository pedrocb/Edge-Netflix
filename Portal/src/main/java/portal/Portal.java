package portal;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.xml.crypto.Data;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.sql.*;
import java.util.Properties;

public class Portal {
    public static Properties config;

    public static void main(String[] args) {
        try {
            loadConfig();
        } catch (IOException e) {
            System.out.println("Missing portal.config..");
           return;
        }
        ResourceConfig resourceConfig = new ResourceConfig()
                .packages("portal")
                .property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true)
                .register(MoxyJsonFeature.class);

        int port;

        try {
            port = Integer.parseInt(config.getProperty("port", "9999"));
        } catch (NumberFormatException e) {
            System.out.println("Port must be a number");
            return;
        }
        System.out.println("Port: " + port);
        Server jettyServer = new Server(port);
        ServletContextHandler context = new ServletContextHandler(jettyServer, "/*");
        context.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), "/*");
        NCSARequestLog requestLog = new NCSARequestLog();
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT");
        requestLog.setLogLatency(true);
        requestLog.setRetainDays(90);

        jettyServer.setRequestLog(requestLog);
        System.out.println("REST API started..");

        try {
            jettyServer.start();

            //new HealthCheck().start();

            jettyServer.join();
        } catch (SocketException e) {
            System.out.println("Port already in use");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jettyServer.stop();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            jettyServer.destroy();
        }
    }

    public static void loadConfig() throws IOException {
        config = new Properties();
        FileInputStream configFile = new FileInputStream("portal.config");
        config.load(configFile);
        configFile.close();
    }

}
