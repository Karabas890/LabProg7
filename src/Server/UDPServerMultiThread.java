package Server;

import Commands.Command;
import Models.CollectionManager;
import Models.CollectionManagerToSQL;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

/**
 * Класс UDPServer является сервером для обработки команд от клиентов.
 */
@SuppressWarnings("FieldCanBeLocal")
public class UDPServerMultiThread {

    //region Поля
    /**
     * Длина сообщения в байтах
     */
    private final int messageLength = 16384;
    /**
     * Порт, на котором работает сервер.
     */
    private int port;
    /**
     * Сканер для чтения команд из консоли.
     */
    private Scanner scanner;
    /**
     * Map, содержащий адреса клиентов и информацию о них.
     */
    private Map<SocketAddress, ClientInfo> clientAddresses;

    /**
     * Канал для обмена сообщениями с клиентами.
     */
    private DatagramChannel channel;
    /**
     * Запущен сервер или нет
     */
    @SuppressWarnings("FieldMayBeFinal")
    private boolean isStarted = false;
    /**
     * Менеджер для упроавления объектами в коллекции
     */
    private CollectionManager collectionManager;


    /**
     * Распознает и выполняет команды, передеанные серверу
     */
    private CommandReaderServer commandReader;
    //endregion

    //region Конструкторы

    /**
     * Конструктор по умолчанию, инициализирует список адресов клиентов.
     */
    public UDPServerMultiThread(String dbURL, String dbUsername, String dbPassword) {
        this(System.in, dbURL, dbUsername, dbPassword, 8365);
    }

    /**
     * Создает сервер с указанием входного потока данных
     *
     * @param inputStream входной поток данных
     */
    public UDPServerMultiThread(InputStream inputStream, String dbURL, String dbUsername, String dbPassword) {
        this(inputStream, dbURL, dbUsername, dbPassword, 5432);
    }

    public UDPServerMultiThread(InputStream inputStream, String dbURL, String dbUsername, String dbPassword, int port) {
        try {
            this.port = port;
            this.scanner = new Scanner(inputStream);
            this.collectionManager = new CollectionManagerToSQL(dbURL, dbUsername, dbPassword, -1);
            this.commandReader = new CommandReaderServer(this.collectionManager, System.in);
            this.clientAddresses = Collections.synchronizedMap(new HashMap<>());
        } catch (Exception ex) {
            this.Print(ex.getMessage());
        }
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

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }
    //endregion

    //region Методы

    /**
     * Метод для проверки наличия файла и его создания при отсутствии.
     *
     * @param fileName Имя файла.
     * @throws IOException В случае ошибок ввода/вывода.
     */
    public void CheckFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            System.out.println("Файл " + fileName + " уже существует.");
        } else {
            if (file.createNewFile()) {
                System.out.println("Файл " + fileName + " был успешно создан.");
            } else {
                System.out.println("Невозможно создать файл " + fileName);
            }
        }
    }

    /**
     * Выводит указаный объект в консоль.
     *
     * @param object Объект для вывода.
     */
    void Print(Object object) {
        System.out.println(object);
    }

    /**
     * Обрабатывает полученную команду и возвращает результат.
     *
     * @param clientAddress Адрес клиента
     * @param data          имя команды
     * @return Оьъект, возвращаемй после выполнения команды
     */
    private Object Receive(SocketAddress clientAddress, Data data) {
        try {
            Print(String.format("Получена команда от клиента %s(%s):%s", clientAddresses.get(clientAddress), clientAddress, data));
            return this.commandReader.Execute(data.command.getName(), data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Отправляет данные клиенту.
     *
     * @param clientAddress Адрес клиента
     * @param data          Объект с данными
     * @throws IOException Ошибка ввода/вывода
     */
    private void Send(SocketAddress clientAddress, Object data) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);

        objStream.writeObject(data);
        objStream.flush();

        ByteBuffer sendBuffer = ByteBuffer.wrap(byteStream.toByteArray());
        channel.send(sendBuffer, clientAddress);
    }


    public void Start() throws Exception {
        this.isStarted = true;
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(this.port));
        this.collectionManager.Load();
        Print("Сервер запущен...");

        //region Создаем новый поток для чтения команд из консоли
        new Thread(() -> {
            while (isStarted) {
                try {
                    if (this.scanner.hasNextLine()) {
                        // Удаление лишних пробелов в начале и конце строки
                        String commandName = this.scanner.nextLine().trim();
                        // Разбиение строки на отдельные слова на основе пробелов
                        String[] words = commandName.split("\\s+");
                        if (words.length == 1) {
                            ArrayList<Object> params = new ArrayList<>();
                            if (commandName.equals(Command.Titles.exit)) {
                                this.isStarted = false;
                                this.Print("Остановка сервера...");
                                return;
                            }
                            if (commandName.equals(Command.Titles.save) || commandName.equals(Command.Titles.load)) {
                                params.add(true);
                            }
                            Object result = this.commandReader.Execute(commandName, params.toArray());
                            this.collectionManager.Save();
                            Print(result);
                        }
                        if (words.length > 1) {
                            String[] params = new String[words.length - 1];
                            System.arraycopy(words, 1, params, 0, words.length - 1);
                            Object result = this.commandReader.Execute(words[0], params);
                            if (result != null)
                                Print(result);
                        }
                    }
                } catch (Exception e) {
                    Print(e.getMessage());
                }
            }
        }).start();
        //endregion

        // Create a ForkJoinPool
        ForkJoinPool fjp = new ForkJoinPool();

        //region Обработка подключений и сообщений клиентов
        ByteBuffer buffer = ByteBuffer.allocate(16384);

        fjp.submit(() -> {
            while (isStarted) {
                buffer.clear();
                SocketAddress clientAddress = null;
                try {
                    clientAddress = channel.receive(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (clientAddress != null) {
                    SocketAddress finalClientAddress = clientAddress;
                    if (!clientAddresses.containsKey(clientAddress)) {
                        clientAddresses.put(clientAddress, new ClientInfo(clientAddresses.size() + 1));
                        Print("Подключен новый клиент: " + clientAddress);
                    }

                    new Thread(() -> {
                        try {
                            if (buffer.array().length > 0) { // проверка, есть ли данные в буфере
                                try {
                                    ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer.array());
                                    ObjectInputStream objStream = new ObjectInputStream(byteStream);
                                    Data clientData = (Data) objStream.readObject();
                                    if (clientAddresses.get(finalClientAddress).isAuthorized() ||
                                            clientData.command.getName().equals(Command.Titles.login) ||
                                            clientData.command.getName().equals(Command.Titles.register)
                                    ) {
                                        Object result = Receive(finalClientAddress, clientData);
                                        /*
                                        if (clientData.command.equals(Command.Titles.clear)
                                                ||clientData.command.equals(Command.Titles.update)
                                                ||clientData.command.equals(Command.Titles.removeById)
                                                ||clientData.command.equals(Command.Titles.removeHead)

                                        ) {
                                            this.collectionManager.Save();
                                        }
                                         */
                                        if (clientData.command.getName().equals(Command.Titles.login) ||
                                                clientData.command.getName().equals(Command.Titles.register)) {
                                            ClientInfo clientInfo = this.clientAddresses.get(finalClientAddress);
                                            clientInfo.setAuthorized(true);
                                            this.clientAddresses.put(finalClientAddress, clientInfo);
                                        }
                                        Send(finalClientAddress, result);
                                    } else {
                                        Send(finalClientAddress, "Вы не авторизованы, используйте команды register или login.");
                                    }
                                } catch (StreamCorruptedException | ClassNotFoundException ex) {
                                    Thread.sleep(100);
                                }
                            }
                        } catch (Exception ex) {
                            this.Print(ex.getMessage());
                            try {
                                Send(finalClientAddress, ex.getMessage());
                            } catch (IOException e) {
                                this.Print(ex.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });

        //endregion

    }


    //endregion

}
