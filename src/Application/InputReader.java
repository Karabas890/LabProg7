/**
 * The InputReader class is responsible for receiving user input and validating it
 * in order to create SpaceMarine objects that can be added to the CollectionManager.
 * It contains methods for receiving the name, X and Y coordinates, health, achievements,
 * and Astartes category of a space marine, as well as generating an ID and a creation date.
 */
package Application;

import Models.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * Класс InputReader отвечает за получение пользовательского ввода и его проверку
 * для создания объектов SpaceMarine, которые могут быть добавлены в CollectionManager.
 * Он содержит методы для получения имени, координат X и Y, здоровья, достижений
 * и категории Astartes для космических маринов, а также для генерации идентификатора и даты создания.
 */
public class InputReader {

    //region Поля
    /**
     * Объект для управления коллекцией
     */
    private CollectionManager collectionManager;
    /**
     * Сканер для считывания ввода из заданного потока данных.
     */
    private Scanner scanner;


    /**
     * Показывать вывод инструкций по вводу в консоль или нет
     */
    private boolean isShowPrompt;

    /**
     * Чтение из файла или нет
     */
    private boolean isReadFromFile;
    //endregion

    //region Конструктор

    /**
     * Конструктор, который инициализирует новый объект InputReader с CollectionManager и устанавливает
     * переменную-счетчик равной максимальному значению ID в CollectionManager плюс 1.
     *
     * @param collectionManager CollectionManager, который будет содержать объекты SpaceMarine.
     * @param scanner           Scanner, из которого будет считываться ввод.
     * @param isShowPrompt      флаг, указывающий, нужно ли отображать инструкции по вводу в консоль.
     */
    public InputReader(CollectionManager collectionManager, Scanner scanner, boolean isReadFromFile, boolean isShowPrompt) {
        this.isReadFromFile = isReadFromFile;
        this.collectionManager = collectionManager;
        this.scanner = scanner;
        this.isShowPrompt = isShowPrompt;
    }

    /**
     * Конструктор, который инициализирует новый объект InputReader с CollectionManager и устанавливает
     * переменную-счетчик равной максимальному значению ID в CollectionManager плюс 1.
     *
     * @param collectionManager CollectionManager, который будет содержать объекты SpaceMarine.
     * @param inputStream       InputStream, из которого будет считываться ввод.
     * @param isShowPrompt      флаг, указывающий, нужно ли отображать инструкции по вводу в консоль.
     */
    public InputReader(CollectionManager collectionManager, InputStream inputStream, boolean isShowPrompt) throws IOException {
        this.isReadFromFile = inputStream instanceof FileInputStream;
        this.collectionManager = collectionManager;
        this.scanner = new Scanner(inputStream);
        this.isShowPrompt = isShowPrompt;
    }
    //endregion

    //region Методы
    public static Timestamp getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        return Timestamp.valueOf(now);
    }

    public void Print(String message, boolean isShowPrompt) {
        if (isShowPrompt)
            System.out.println(message);
    }

    public String GetStr(String message) {
        return GetStr(message, this.isShowPrompt);
    }

    public String GetStr(String message, boolean isShowPrompt) {
        try {
            Print(message, isShowPrompt);
            return scanner.nextLine();
        } catch (Exception e) {
            Print(e.getMessage(), isShowPrompt);
            if (this.isReadFromFile)
                return null;
            if (scanner.hasNextLine())
                return GetStr(message, isShowPrompt);
            else
                return null;
        }
    }

    public Integer GetInt(String message) {
        return GetInt(message, this.isShowPrompt);
    }

    public Integer GetInt(String message, boolean isShowPrompt) {
        try {
            Print(message, isShowPrompt);
            String input = scanner.nextLine();
            Integer result = Integer.parseInt(input);
            return result;
        } catch (Exception e) {
            Print(e.getMessage(), isShowPrompt);
            if (this.isReadFromFile)
                return null;
            return GetInt(message, isShowPrompt);
        }
    }

    public Float GetFloat(String message) {
        return GetFloat(message);
    }

    public Float GetFloat(String message, boolean isShowPrompt) {
        try {
            Print(message, isShowPrompt);
            String input = scanner.nextLine();
            Float result = Float.parseFloat(input);
            return result;
        } catch (Exception e) {
            Print(e.getMessage(), isShowPrompt);
            if (this.isReadFromFile)
                return null;
            return GetFloat(message, isShowPrompt);
        }
    }


    //region Получение объектов

    /**
     * Получает объект Chapter из ввода пользователя.
     *
     * @return объект Chapter
     */
    public Chapter GetChapter() {
        try {
            return new Chapter(
                    GetStr("Введите название главы: ", this.isShowPrompt),
                    GetStr("Введите мир главы: ", this.isShowPrompt)
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return GetChapter();
        }
    }

    /**
     * Получает объект Coordinates из ввода пользователя.
     *
     * @return объект Coordinates
     */
    public Coordinate GetCoordinates() {
        try {
            return new Coordinate(
                    GetFloat("Введите координату X: ", this.isShowPrompt),
                    GetInt("Введите координату Y: ", this.isShowPrompt)
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (this.isReadFromFile)
                return null;
            return GetCoordinates();
        }
    }


    /**
     * Получает значение перечисления из указанного класса перечисления на основе ввода пользователя.
     * Пользователь может ввести либо номер, либо название одного из перечислений.
     * Если указанное значение не существует в перечислении, будет запрошено повторное ввод.
     *
     * @param <E>       тип перечисления
     * @param enumClass класс перечисления
     * @return значение перечисления, введенное пользователем
     */
    public <E extends Enum<E>> E GetEnumValue(Class<E> enumClass) {
        try {
            PrintEnumValues(enumClass);
            String value = this.scanner.nextLine();
            E[] enumConstants = enumClass.getEnumConstants();
            // Check if the input is a number
            if (value.matches("\\d+")) {
                int enumIndex = Integer.parseInt(value) - 1;
                if (enumIndex >= 0 && enumIndex < enumConstants.length) {
                    return enumConstants[enumIndex];
                }
            }
            // If the input is not a number, then it's considered as enum constant name
            value = value.toUpperCase(Locale.ROOT);
            return Enum.valueOf(enumClass, value);

        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println("Некорректный ввод. Пожалуйста, введите значение из списка или его номер:");
            return this.GetEnumValue(enumClass);
        }
    }

    /**
     * Выводит все значения перечисления, пронумерованные списком.
     *
     * @param <E>       тип перечисления
     * @param enumClass класс перечисления
     */
    public <E extends Enum<E>> void PrintEnumValues(Class<E> enumClass) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (int i = 0; i < enumConstants.length; i++) {
            this.Print(String.format("%d. %s", i + 1, enumConstants[i].name()), this.isShowPrompt);
        }
    }

    /**
     * Получает объект SpaceMarine на основе ввода пользователя.
     * Значения полей запрашиваются с помощью других вспомогательных методов.
     *
     * @return объект SpaceMarine, созданный на основе ввода пользователя
     */
    public SpaceMarine GetSpaceMarine() {
        try {

            return new SpaceMarine(
                    0,
                    GetStr("Введите имя: ", this.isShowPrompt),
                    GetCoordinates(),
                    GetInt("Введите значение здоровья: ", this.isShowPrompt),
                    GetStr("Введите достижения: ", this.isShowPrompt),
                    GetEnumValue(Category.class),
                    GetEnumValue(WeaponType.class),
                    GetChapter()
            );
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            if (this.isReadFromFile)
                return null;
            return GetSpaceMarine();
        }

    }

    public SpaceMarine GetSpaceMarineWithId() {
        try {

            return new SpaceMarine(
                    GetInt("Введите id: ", this.isShowPrompt),
                    GetStr("Введите имя: ", this.isShowPrompt),
                    GetCoordinates(),
                    GetInt("Введите значение здоровья: ", this.isShowPrompt),
                    GetStr("Введите достижения: ", this.isShowPrompt),
                    GetEnumValue(Category.class),
                    GetEnumValue(WeaponType.class),
                    GetChapter()
            );
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return GetSpaceMarine();
        }

    }
    //endregion

    //endregion


}
