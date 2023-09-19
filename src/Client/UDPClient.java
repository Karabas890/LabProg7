package Client;

import Commands.Command;
import Models.User;
import Server.Data;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Класс UDPClient представляет клиента для обмена командами с сервером.
 */
public class UDPClient {

    /**
     * Длина сообщения в байтах
     */
    //region Поля
    private final int messageLength = 65536;
    /**
     * Порт, на котором работает сервер.
     */
    private int port;

    /**
     * Текущий пользователь
     */
    public User currentUser;

    /**
     * Канал для отправки и получения сообщений от сервера.
     */
    private DatagramChannel channel;

    /**
     * Сканер для чтения команд из консоли.
     */
    private Scanner scanner;

    /**
     * Распознает и выполняет команды, передеанные клиенту
     */
    private CommandReaderClient commandReader;
    //endregion

    //region Конструкторы

    /**
     * Конструктор по умолчанию инициализирует сканер для чтения команд.
     */
    public UDPClient() throws IOException {
        this(System.in, 8365);
    }

    public UDPClient(InputStream inputStream) throws IOException {
        this(inputStream, 8365);
    }

    //    private byte[] getInputStreamBytes(InputStream inputStream) {
//        try {
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            byte[] buffer = new byte[4096];
//            int length;
//            while ((length = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, length);
//            }
//            return outputStream.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new byte[0];
//    }
    public UDPClient(InputStream inputStream, int port) throws IOException {
        this.scanner = new Scanner(inputStream);
//        if (inputStream.markSupported()) {
//            inputStream.reset();
//        }
        this.commandReader = new CommandReaderClient(inputStream);
        this.port = port;
    }
    //endregion

    //region Сеттеры и геттеры

    /**
     * Устанавливает порт сервера
     *
     * @param port Порт сервера
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Возвращает порт сервера
     *
     * @return Порт сервера
     */
    public int getPort() {
        return port;
    }
    //endregion

    //region Методы

    /**
     * Выводит указаный объект в консоль.
     *
     * @param object Объект для вывода.
     */
    void Print(Object object) {
        System.out.println(object);
    }

    /**
     * Запускает клиента и ждет ввода команд от пользователя.
     */
    public void Start() throws Exception {
        channel = DatagramChannel.open();
        channel.bind(null);

        channel.configureBlocking(false);
        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        System.out.println("Клиент запущен...");

        while (true) {
            try {
                System.out.println("Введите команду или 'exit', чтобы выйти:");

                String commandName = scanner.nextLine().trim();  // Удаление лишних пробелов в начале и конце строки
                String[] words = commandName.split("\\s+");  // Разбиение строки на отдельные слова на основе пробелов
                if (words.length == 1) {
                    ArrayList<Object> params = new ArrayList<>();
                    if (commandName.equals(Command.Titles.exit)) {
//                        Print("Закрытие клиента: " + this.channel.getLocalAddress());
                        Print("Закрытие клиента");
                        return;
                    }
                    if (commandName.equals(Command.Titles.save) || commandName.equals(Command.Titles.load)) {
                        params.add(false);
                    }
                    Send((Data) this.commandReader.Execute(commandName, params.toArray()));
                }
                if (words.length > 1) {
                    String[] params = new String[words.length - 1];
                    System.arraycopy(words, 1, params, 0, words.length - 1);
                    Send((Data) this.commandReader.Execute(words[0], params));
                }
                Receive();
            } catch (Exception ex) {
                this.Print(ex.getMessage());
            }
        }
    }

    /**
     * Отправляет команду серверу.
     */
    private void Send(Data data) throws IOException {
        try {
            data.currentUser = this.currentUser;
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(byteStream);

            objStream.writeObject(data);
            objStream.flush();

            ByteBuffer buffer = ByteBuffer.wrap(byteStream.toByteArray());

            channel.send(buffer, new InetSocketAddress("localhost", this.port));

        } catch (IOException e) {
            System.out.println("Сервер недоступен.");
            System.out.println(e.getMessage());
            // Здесь вы можете добавить действия, которые должны выполняться при недоступности сервера.
            // Например, вы можете прекратить работу клиента или попытаться переподключиться.
            //throw e; // Повторно бросить исключение, если вы хотите его обработать на более высоком уровне
        }
    }

    /**
     * Получает ответ от сервера.
     */
    private void Receive() throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(this.messageLength);
        Selector selector = this.channel.provider().openSelector();

        int interestSet = SelectionKey.OP_READ;
        this.channel.register(selector, interestSet);

        while (true) {
            if (selector.select(15000) == 0) {  // тайм-аут в миллисекундах
                throw new SocketTimeoutException("Тайм-аут: сервер недоступен или не отвечает");
            }

            Iterator keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isReadable()) {
                    SocketAddress serverAddress = channel.receive(buffer);

                    ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer.array());
                    ObjectInputStream objStream = new ObjectInputStream(byteStream);
                    Object serverResponse = objStream.readObject();
                    if (serverResponse != null && serverResponse.getClass() == User.class) {
                        this.currentUser = (User) serverResponse;
                        System.out.println("Вы авторизованы на сервере!");
                    } else {
                        String response = (String) serverResponse;
                        System.out.println("Получен ответ от сервера: " + response);
                    }
                    keys.remove();
                    return;
                }
            }
        }
    }

    //endregion
}
