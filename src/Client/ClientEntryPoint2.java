package Client;

/**
 * Для запуска дополнительного клиента при тестах вручную
 */
public class ClientEntryPoint2 {
    public static void main(String[] args) throws Exception {
        UDPClient client = new UDPClient();
        client.Start();
    }
}
