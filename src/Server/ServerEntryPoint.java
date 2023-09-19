package Server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerEntryPoint {
    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;

    public static void LoadCredentials(String fileName) {
        try {
            Scanner credentials = new Scanner(new FileReader(fileName));
            DB_URL = credentials.nextLine().trim();
            DB_USERNAME = credentials.nextLine().trim();
            DB_PASSWORD = credentials.nextLine().trim();

        } catch (FileNotFoundException e) {
            System.out.println("Нет нужного файла");
            System.exit(-1);
        } catch (NoSuchElementException e) {
            System.out.println("Нет нужных данных в файле");
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws Exception {
        LoadCredentials("credentials.txt");
        try {
            UDPServerMultiThread server = new UDPServerMultiThread(DB_URL, DB_USERNAME, DB_PASSWORD);
            server.Start();
        } catch (Exception e) {
            System.out.println("Не удалось подключиться к серверу");
            System.exit(-1);
        }
    }
}
