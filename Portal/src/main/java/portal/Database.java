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

    public static ArrayList<FileBean> getAllFiles() throws SQLException {
        ArrayList<FileBean> result = new ArrayList<FileBean>();
        Connection connection = DriverManager.getConnection(jdbcUrl, "root", "");
        ResultSet files = connection.createStatement().executeQuery("SELECT * FROM Files LEFT JOIN Seeders ON Seeders.FileId = Files.id;");
        int fileId;
        while (files.next()) {
            fileId = files.getInt("Files.ID");
            ArrayList<String> keywords = getKeywords(connection, fileId);
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

    public static FileBean getFile(String filename) throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl, "root", "");
        PreparedStatement st = connection.prepareStatement("SELECT * from Files LEFT JOIN Seeders ON Seeders.FileId = Files.id where Files.Name = ?;");
        st.setString(1, filename);
        ResultSet result = st.executeQuery();
        if(result.next()) {
            SeederBean seeder = null;
            ArrayList<String> keywords = getKeywords(connection, result.getInt("Files.ID"));
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

    private static ArrayList<String> getKeywords(Connection connection, int fileId) throws SQLException {
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
        Connection connection = DriverManager.getConnection(jdbcUrl, "root", "");
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
