package Server;

public class ServerEntryPoint2 {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/dbmarines";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "12345678";

    public static void main(String[] args) throws Exception {
        //Class.forName("org.postgresql.Driver");
        UDPServerMultiThread server = new UDPServerMultiThread(DB_URL, DB_USERNAME, DB_PASSWORD);
        server.Start();
    }
}
