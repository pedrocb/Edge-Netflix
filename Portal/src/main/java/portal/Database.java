package portal;

import datamodels.FileBean;
import datamodels.SeederBean;
import javassist.compiler.ast.Keyword;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    private static String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s&"
                        + "socketFactory=com.google.cloud.sql.mysql.SocketFactory",
                "edgeNetflix",
                "groupc-179216:europe-west1:einstance-sql");
    private static Connection connection = null;

    public static void setupConnection() throws SQLException {
        String user = Portal.config.getProperty("dbUser", "root");
        String password = Portal.config.getProperty("dbPassword", "");
        if (connection == null) {
            connection = DriverManager.getConnection(jdbcUrl, user, password);
        }
    }

    public static ArrayList<FileBean> getAllFiles() throws SQLException {
        setupConnection();
        ArrayList<FileBean> result = new ArrayList<FileBean>();
        ResultSet files = connection.createStatement().executeQuery("SELECT * FROM Files LEFT JOIN Seeders ON Seeders.FileId = Files.id;");
        int fileId;
        while (files.next()) {
            fileId = files.getInt("Files.ID");
            ArrayList<String> keywords = getKeywords(fileId);
            SeederBean seeder = null;
            String address = files.getString("Address");
            if (!files.wasNull()) {
                seeder = new SeederBean(address + ":" + files.getInt("Port"), files.getInt("Bitrate"));
            }
            System.out.println(seeder);
            result.add(new FileBean(files.getString("Name"), files.getInt("Size"), seeder, keywords, files.getInt("ChunkSize")));
        }

        return result;
    }

    public static void removeSeeder(int id) throws SQLException {
        setupConnection();
        PreparedStatement ps = connection.prepareStatement("DELETE FROM Seeders WHERE Id = ?;");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public static ArrayList<SeederBean> getAllSeeders() throws SQLException {
        setupConnection();
        ArrayList<SeederBean> result = new ArrayList<>();
        ResultSet seeders = connection.createStatement().executeQuery("SELECT * FROM Seeders;");
        while (seeders.next()) {
            SeederBean seederBean = new SeederBean(seeders.getString("Address") + ":" + seeders.getInt("Port"), seeders.getInt("Bitrate"));
            seederBean.setId(seeders.getInt("Id"));
            result.add(seederBean);
        }

        return result;
    }

    public static FileBean getFile(String filename) throws SQLException {
        setupConnection();
        PreparedStatement st = connection.prepareStatement("SELECT * from Files LEFT JOIN Seeders ON Seeders.FileId = Files.id where Files.Name = ?;");
        st.setString(1, filename);
        ResultSet result = st.executeQuery();
        if(result.next()) {
            SeederBean seeder = null;
            ArrayList<String> keywords = getKeywords(result.getInt("Files.ID"));
            String seederAddress = result.getString("Address");
            if(!result.wasNull()) {
                seeder = new SeederBean(seederAddress + ":" + result.getString("Port"), result.getInt("Bitrate"));
            }
            return new FileBean(result.getString("Name"), result.getInt("Size"), seeder, keywords, result.getInt("ChunkSize"));
        }
        else {
            return null;
        }
    }

    private static ArrayList<String> getKeywords(int fileId) throws SQLException {
        ArrayList<String> keywords = new ArrayList<>();
        PreparedStatement keywordsStatement = connection.prepareStatement("SELECT Keyword FROM Keywords WHERE FileId = ?;");
        keywordsStatement.setInt(1, fileId);
        ResultSet keywordsResult = keywordsStatement.executeQuery();
        while (keywordsResult.next()) {
            keywords.add(keywordsResult.getString(1));
        }
        return keywords;
    }

    public static SeederBean registerSeeder(String filename, String address, int port) throws SQLException {
        setupConnection();
        PreparedStatement st = connection.prepareStatement("INSERT INTO Seeders (FileID, Address, Port, Bitrate) " +
                "VALUES ((SELECT id from Files where Name = ?), ?, ?, ?);");
        st.setString(1, filename);
        st.setString(2, address);
        st.setInt(3, port);
        st.setInt(4, 0);
        System.out.println(st);
        st.executeUpdate();
        return new SeederBean(address + ":" + port, 0) ;
    }
}
