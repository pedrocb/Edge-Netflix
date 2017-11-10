package portal.seeder;

import datamodels.FileBean;
import datamodels.SeederBean;

import javax.swing.plaf.nimbus.State;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.Result;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Path("seeder")
public class SeederService {
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<FileBean> listServices() {
        ArrayList<FileBean> result = new ArrayList<>();
        try {
            result = getAllFiles();
        } catch (SQLException e) {
            System.out.println("Error connecting to database");
        }
        return result;
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<FileBean> searchServices(@QueryParam("keyword") final String keyword) {
        ArrayList<FileBean> files = new ArrayList<>();
        ArrayList<FileBean> result = new ArrayList<>();
        try {
            files = getAllFiles();
            result = new ArrayList<>(files.stream().filter(file -> file.getKeywords().contains(keyword)).collect(Collectors.toList()));
        } catch (SQLException e) {
            System.out.println("Error connecting to database");
        }

        return result;
    }


    private static ArrayList<FileBean> getAllFiles() throws SQLException {
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s&"
                        + "socketFactory=com.google.cloud.sql.mysql.SocketFactory",
                "edgeNetflix",
                "groupc-179216:europe-west1:einstance-sql");
        ArrayList<FileBean> result = new ArrayList<FileBean>();
        Connection connection = DriverManager.getConnection(jdbcUrl, "root", "");
        ResultSet files = connection.createStatement().executeQuery("SELECT * FROM Files left join Seeders ON Seeders.FileId = Files.id;");
        PreparedStatement keywordsStatement = connection.prepareStatement("SELECT Keyword FROM Keywords WHERE FileId = ?;");
        int fileId;
        while (files.next()) {
            ArrayList<String> keywords = new ArrayList<>();
            fileId = files.getInt("Files.ID");
            keywordsStatement.setInt(1, fileId);
            ResultSet keywordsResult = keywordsStatement.executeQuery();
            while (keywordsResult.next()) {
                keywords.add(keywordsResult.getString(1));
            }
            SeederBean seeder = null;
            String address = files.getString("Address");
            if(!files.wasNull()) {
                seeder = new SeederBean(address + ":" + files.getInt("Port"), files.getInt("Bitrate"));
            }
            System.out.println(seeder);
            result.add(new FileBean(files.getString("Name"), files.getInt("Size"), seeder, keywords));
        }

        return result;
    }
}
