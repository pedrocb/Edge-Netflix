package portal.file;

import datamodels.SeederBean;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import core.FileInfo;
import core.MasterSeederServiceGrpc;
import core.MasterSeederServiceGrpc.MasterSeederServiceBlockingStub;
import core.Endpoint;

import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

@Path("file")
public class FileService {
    @POST
    @Path("download")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SeederBean downloadFile(JsonObject file) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9998).usePlaintext(true).build();
        MasterSeederServiceBlockingStub stub = MasterSeederServiceGrpc.newBlockingStub(channel);
        String filename = file.getString("file");
        try {
            SeederBean seeder = getSeeder(filename);
            if (seeder == null) {
                System.out.println("Seeder not found. Creating one ...");
                Endpoint info = stub.createSeeder(FileInfo.newBuilder().setFilename(filename).build());
                seeder = registerSeeder(filename, "localhost", info.getPort());
            }
            System.out.println(seeder);
            return seeder;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private SeederBean getSeeder(String filename) throws SQLException {
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s&"
                        + "socketFactory=com.google.cloud.sql.mysql.SocketFactory",
                "edgeNetflix",
                "groupc-179216:europe-west1:einstance-sql");

        Connection connection = DriverManager.getConnection(jdbcUrl, "root", "");
        PreparedStatement st = connection.prepareStatement("SELECT * from Seeders INNER JOIN Files on Files.id = Seeders.FileId where Files.Name = ?;");
        st.setString(1, filename);
        ResultSet result = st.executeQuery();
        if(result.next()) {
            return new SeederBean(result.getString("Address") + ":" + result.getInt("Port"), result.getInt("Bitrate")) ;
        }
        else {
            return null;
        }
    }

    private SeederBean registerSeeder(String filename, String address, int port) throws SQLException {
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s&"
                        + "socketFactory=com.google.cloud.sql.mysql.SocketFactory",
                "edgeNetflix",
                "groupc-179216:europe-west1:einstance-sql");

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
