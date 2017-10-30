package portal;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Portal {

    public static void main(String[] args) throws Exception {
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s&"
                        + "socketFactory=com.google.cloud.sql.mysql.SocketFactory",
                "edgeNetflix",
                "groupc-179216:europe-west1:einstance-sql");

        Connection connection = DriverManager.getConnection(jdbcUrl, "root", "");
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Files;");
        while (rs.next()) {
            System.out.println(rs.getString("Name"));
        }

        ResourceConfig config = new ResourceConfig()
                .packages("portal")
                .property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true)
                .register(MoxyJsonFeature.class);


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
