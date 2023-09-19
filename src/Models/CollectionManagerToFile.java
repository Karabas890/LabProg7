package Models;

import Exceptions.NotImplementedException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Класс, управляющий коллекцией объектов класса SpaceMarine
 */
public class CollectionManagerToFile extends CollectionManager implements Serializable {


    //region Поля

    /**
     * Файл с данными
     */
    private File dataFile;

    /**
     * Объект библиотеки
     * для преобразования объектов Java в формат JSON
     */
    private Gson gson;

    //endregion

    //region Конструкторы
    public CollectionManagerToFile(String filename) throws Exception {
        executedScripts = new HashSet<>();
        marines = new LinkedList<>();
        dataFile = new File(filename);
        gson = new Gson();
        initializationDate = LocalDate.now();
        commandHistory = new LinkedList<>();
        Load(dataFile.getPath());
    }
    //endregion

    //region Методы

    /**
     * Загрузка из файла
     *
     * @param filePath путь до файла
     * @return
     * @throws Exception
     */
    public boolean Load(String filePath) throws Exception {
        try (Reader reader = new InputStreamReader(new FileInputStream(filePath))) {
            marines = gson.fromJson(reader, new TypeToken<LinkedList<SpaceMarine>>() {
            }.getType());

            //region Генерация ID и проверка на дубликаты
            for (SpaceMarine marine : marines) {
                if (marine.getId() <= lastId) {
                    marine.setId(++lastId);
                } else {
                    lastId = marine.getId();
                }
            }
            //endregion

            //region Проверка полей на правильность заполнения
            LinkedList<SpaceMarine> marinesTemp = new LinkedList<SpaceMarine>();
            for (SpaceMarine marine : marines) {
                try {
                    marinesTemp.add(new SpaceMarine(marine));
                } catch (Exception ex) {
                    System.out.println(String.format("Элемент не был загружен, его описание:'%s'", marine));
                }
            }
            //endregion
            return true;
        } catch (IOException e) {
            System.err.println("Не удалось загрузить файл: " + e.getMessage());
            return false;
        }
    }

    /**
     * Загрузка из файла
     *
     * @return
     * @throws Exception
     */
    public boolean Load() throws Exception {
        return false;
    }

    /**
     * Сохранение в файл по указанному пути
     *
     * @param filePath путь до файла
     * @return
     * @throws Exception Ошибка при сохранении файла
     */
    public boolean Save(String filePath) throws Exception {
        return false;
    }

    /**
     * Сохранение в файл
     */
    public boolean Save() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(dataFile))) {
            gson.toJson(marines, writer);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save collection to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Аутентифицирует пользователя по заданному логину и паролю.
     *
     * @param login    Логин пользователя.
     * @param password Пароль пользователя.
     * @return Аутентифицированный пользователь.
     * @throws Exception Если произошла ошибка в процессе аутентификации.
     */
    @Override
    public User Login(String login, String password) throws Exception {
        throw new NotImplementedException();
    }

    /**
     * Регистрирует нового пользователя с заданным логином и паролем.
     *
     * @param login    Логин пользователя.
     * @param password Пароль пользователя.
     * @return Зарегистрированный пользователь.
     * @throws Exception Если произошла ошибка в процессе регистрации.
     */
    @Override
    public User Register(String login, String password) throws Exception {
        throw new NotImplementedException();
    }


    //endregion
}
