package Client;

/**
 * Для запуска основного клиента при тестах вручную
 */
public class ClientEntryPoint {
    public static void main(String[] args) throws Exception {
        UDPClient client = new UDPClient();
        client.Start();
    }
}
