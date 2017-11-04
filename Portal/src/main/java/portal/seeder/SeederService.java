package portal.seeder;

import datamodels.SeederBean;

import javax.swing.plaf.nimbus.State;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Path("seeder")
public class SeederService {
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<SeederBean> listServices() {
        ArrayList<SeederBean> result = new ArrayList<>();
        try {
            result = getAllSeeders();
        } catch (SQLException e) {
            System.out.println("Error connecting to database");
        }
        return result;
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<SeederBean> searchServices(@QueryParam("keyword") final String keyword) {
        ArrayList<SeederBean> seeders = new ArrayList<>();
        ArrayList<SeederBean> result = new ArrayList<>();
        try {
            seeders = getAllSeeders();
            result = new ArrayList<>(seeders.stream().filter(seeder -> seeder.getKeywords().contains(keyword)).collect(Collectors.toList()));
        } catch (SQLException e) {
            System.out.println("Error connecting to database");
        }

        return result;
    }

    private static ArrayList<SeederBean> getAllSeeders() throws SQLException {
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s&"
                        + "socketFactory=com.google.cloud.sql.mysql.SocketFactory",
                "edgeNetflix",
                "groupc-179216:europe-west1:einstance-sql");
        ArrayList<SeederBean> result = new ArrayList<SeederBean>();
        Connection connection = DriverManager.getConnection(jdbcUrl, "root", "");
        ResultSet seeders = connection.createStatement().executeQuery("SELECT Seeders.FileId, Address, Port, Bitrate, Name FROM Seeders INNER JOIN Files on Seeders.FileId = Files.id;");
        PreparedStatement keywordsStatement = connection.prepareStatement("SELECT Keyword from Keywords WHERE FileId = ?;");
        int fileId;
        while (seeders.next()) {
            ArrayList<String> keywords = new ArrayList<>();
            fileId = seeders.getInt("FileId");
            keywordsStatement.setInt(1, fileId);
            ResultSet keywordsResult = keywordsStatement.executeQuery();
            while(keywordsResult.next()) {
                keywords.add(keywordsResult.getString(1));
            }
            result.add(new SeederBean(seeders.getString("Name"), seeders.getString("Address")+":"+seeders.getInt("Port"), 900, seeders.getInt("Bitrate"), keywords));
        }

        return result;
    }
}
