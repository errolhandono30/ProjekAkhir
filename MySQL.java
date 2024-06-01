import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private String url;
    private String username;
    private String password;
    private Connection conn;

    public MySQL(String hostname, String database, String port, String username, String password) {
        this.username = username;
        this.password = password;
        this.url = String.format("jdbc:mysql://%s:%s/%s", hostname, port, database);
    }

    public Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName(MySQL.DRIVER);
        conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}

