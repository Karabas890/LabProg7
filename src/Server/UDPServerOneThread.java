package Server;

import Models.CollectionManager;
import Models.CollectionManagerToFile;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

/**
 * Класс UDPServer является сервером для обработки команд от клиентов.
 */
@SuppressWarnings("FieldCanBeLocal")
public class UDPServerOneThread {

    //region Поля
    /**
     * Сканер для чтения команд из консоли.
     */
    private Scanner scanner;
    /**
     * Map, содержащий адреса клиентов и их уникальные идентификаторы.
     */
    private Map<SocketAddress, Integer> clientAddresses;
    /**
     * Канал для обмена сообщениями с клиентами.
     */
    private DatagramChannel channel;
    /**
     * Путь до файла с данными
     */
    private String dataFilePath;
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
    private Selector selector;

    /**
     * Максимальное число команда за итерацию
     */
    private int maxCommandsPerIteration = 50;
    /**
     * Отложенные команды
     */
    private ArrayList<Data> delayedExecution;
    //endregion

    //region Конструкторы

    /**
     * Конструктор по умолчанию, инициализирует список адресов клиентов.
     */
    public UDPServerOneThread() {
        try {
            this.dataFilePath = "Tests/Data/TestData.json";
            this.scanner = new Scanner(System.in);
            this.CheckFile(this.dataFilePath);
            this.collectionManager = new CollectionManagerToFile(this.dataFilePath);
            this.commandReader = new CommandReaderServer(this.collectionManager, System.in);
            this.clientAddresses = Collections.synchronizedMap(new HashMap<>());
        } catch (Exception ex) {
            this.Print(ex.getMessage());
        }
    }

    /**
     * Создает сервер с указанием входного потока данных
     *
     * @param inputStream входной поток данных
     */
    public UDPServerOneThread(InputStream inputStream) throws IOException {
        this(); // вызов первого конструктора
        this.scanner = new Scanner(inputStream);
        this.commandReader = new CommandReaderServer(this.collectionManager, inputStream);
    }
    //endregion

    //region Сеттеры и геттеры
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
//            if (Command.Titles.executeScript.equals(data.command.getName())) {
//                if (data.data != null && data.data.length > 0) {
//                    FileInputStream fileInputStream = new FileInputStream(data.data[0].toString());
//                    CommandReader scriptReader = new CommandReader(this.collectionManager, fileInputStream);
//                    ArrayList<Data> commands = scriptReader.ReadScript();
//                    int g=0;
//                }
//                return null;
//            } else {
//                return this.commandReader.Start(data.command.getName(), data.data);
//            }

            return this.commandReader.Execute(data.command.getName(), data.data);
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

    /**
     * Запускает сервер и ожидает входящих команд от клиентов.
     */
    public void Start() throws Exception {
        try {

            channel = DatagramChannel.open();
            channel.configureBlocking(false); // важно, чтобы канал был неблокирующим
            channel.bind(new InetSocketAddress(8080));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ); // регистрация канала в селекторе

            Print("Сервер запущен...");

            ByteBuffer buffer = ByteBuffer.allocate(16384);
            while (!isStarted) {
                //region Чтение команд с клавиатуры
                if (System.in.available() > 0) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String input = reader.readLine();
                    switch (input) {
                        case "exit": {
                            System.out.println("Программа завершена");
                            isStarted = false;
                            return;
                        }
                        case "save": {
                            collectionManager.Save();
                            System.out.println("Данные сохранены");
                        }
                    }
                }
                //endregion

                if (selector.selectNow() == 0) continue;
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) { // если канал готов к чтению
                        DatagramChannel client = (DatagramChannel) key.channel();
                        buffer.clear();
                        SocketAddress clientAddress = client.receive(buffer); // получение данных
                        // теперь обрабатываем данные без создания нового потока
                        if (clientAddress != null) {
                            if (!clientAddresses.containsKey(clientAddress)) {
                                clientAddresses.put(clientAddress, clientAddresses.size() + 1);
                                Print("Подключен новый клиент: " + clientAddress);
                            }

                            if (buffer.array().length > 0) {
                                try {
                                    ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer.array());
                                    ObjectInputStream objStream = new ObjectInputStream(byteStream);
                                    Data clientData = (Data) objStream.readObject();
                                    Object result = Receive(clientAddress, clientData);
                                    Send(clientAddress, result);
                                } catch (StreamCorruptedException ex) {
                                    Thread.sleep(100);
                                    break;
                                }
                            }
                        }
                    }
                    keyIterator.remove();
                }
            }
            Print("Остановка сервера...");
        } catch (IOException ex) {
            System.out.println("Host is busy.Try Later");
            System.exit(0);
        }
    }
    //endregion

}
