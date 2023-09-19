/**
 * The CommandReader class represents a command reader that receives user input and executes commands based on that input.
 * It includes methods for working with the collection, executing commands, and handling user input.
 */
package Commands;

import Application.InputReader;
import Exceptions.NotImplementedException;
import Models.CollectionManager;
import Models.SpaceMarine;
import Models.User;
import Models.WeaponType;
import Server.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandReader {

    //region Поля
    /**
     * Объект для управления коллекцией
     */
    protected CollectionManager collectionManager;
    /**
     * Сканер для считывания ввода из заданного потока данных.
     */
    protected Scanner scanner;

    /**
     * Читатель пользовательского ввода.
     */
    protected InputReader inputReader;

    protected CommandHelp commandHelp;

    /**
     * Хранит вывод команд, используется в ExecuteScript
     */
    protected ArrayList<String> commandResponses;

    /**
     * Чтение из файла или нет
     */
    protected boolean isReadFromFile;

    /**
     * Текущий пользователь, может быть null, но тогда может не хватать прав
     * для действий с коллекцией
     */
    protected User currentUser;
    //endregion

    //region Сеттеры/Геттеры

    public String GetCommandResponses() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String response : this.commandResponses) {
            stringBuilder.append(response).append("\n");
        }
        return stringBuilder.toString();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    //endregion

    //region Конструкторы

    /**
     * Конструктор CommandReader.
     *
     * @param collectionManager менеджер коллекции
     * @param inputStream       входной поток для чтения команд
     */
    public CommandReader(CollectionManager collectionManager, InputStream inputStream) throws IOException {
        this.collectionManager = collectionManager;
        this.isReadFromFile = inputStream instanceof FileInputStream;
        this.scanner = new Scanner(inputStream);
        this.inputReader = new InputReader(collectionManager, inputStream, true);
        this.commandHelp = new CommandHelp(this.collectionManager);
        this.currentUser = null;
        this.commandResponses = new ArrayList<>();
    }

    public CommandReader(CollectionManager collectionManager, InputStream inputStream, User currentUser) throws IOException {
        this(collectionManager, inputStream);
        this.setCurrentUser(currentUser);
    }
    //endregion

    //region Методы

    /**
     * Выводит указанное сообщение
     *
     * @param message      сообщение
     * @param isShowPrompt выводить в консоль или нет (отключается при рабрте с файлами)
     */
    public void Print(Object message, boolean isShowPrompt) {
        if (isShowPrompt)
            System.out.println(message);
    }

    /**
     * Выводит указанное сообщение
     *
     * @param message сообщение
     */
    public void Print(Object message) {
        this.Print(message, this.isReadFromFile);
    }

    /**
     * Обновляет сканер для чтения, нужен для работы с файлами,
     * иначе мы теряем возможность считывать с файла, поток теряется
     */
    public void UpdateReader() {
        this.inputReader = new InputReader(this.collectionManager, this.scanner, this.isReadFromFile, !this.isReadFromFile);
    }

    public Object Execute(String commandName) throws Exception {
        return this.Execute(commandName, "");
    }

    public Object Execute(String commandName, String params) throws Exception {
        Command currentCommand = this.commandHelp.GetCommand(commandName);

        //region Ничего не возвращают
        if (commandName.equals(Command.Titles.show) || commandName.equals(Command.Titles.info) ||
                commandName.equals(Command.Titles.clear) || commandName.equals(Command.Titles.exit) ||
                commandName.equals(Command.Titles.removeHead))
            currentCommand.Execute(null);
        if (commandName.equals(Command.Titles.executeScript) || commandName.equals(Command.Titles.wait)) {
            currentCommand.Execute(params);

        }
        //endregion
        //region Возвращают строку
        if (commandName.equals(Command.Titles.help) || commandName.equals(Command.Titles.history))
            return currentCommand.Execute(null);
        //endregion
        //region Возвращают число
        if (commandName.equals(Command.Titles.add)) {
            this.UpdateReader();
            Object result = currentCommand.Execute(this.inputReader.GetSpaceMarine());
            if (result == null)
                return "Не удалось добавить объект";
            else
                return String.format("Объект добавлен, его ID в коллекции:%d", result);
        }
        if (commandName.equals(Command.Titles.countLessThanHealth)) {
            Object result = currentCommand.Execute(params);
            if (result == null)
                return "Не удалось найти значение";
            else
                return String.format("Число объектов со здоровьем меньше указанного:%d", result);
        }
        //endregion
        //region Возвращают булевское значение
        if (commandName.equals(Command.Titles.save)) {
            Object result = currentCommand.Execute(null);
            if (result == null || !(boolean) result)
                return "Не удалось сохранить коллекцию";
            else
                return "Коллекция сохранена";
        }
        if (commandName.equals(Command.Titles.update)) {
            this.UpdateReader();
            Object result = currentCommand.Execute(new Object[]{params, this.inputReader.GetSpaceMarine()});
            if (result == null || !(boolean) result)
                return "Не удалось обновить указанный элемент";
            else
                return "Объект успешно обновлен";
        }
        if (commandName.equals(Command.Titles.removeById)) {
            Object result = currentCommand.Execute(params);
            if (result == null || !(boolean) result)
                return "Не удалось удалить указанный элемент";
            else
                return "Объект успешно удален";
        }
        if (commandName.equals(Command.Titles.addIfMin)) {
            this.UpdateReader();
            Object result = currentCommand.Execute(this.inputReader.GetSpaceMarineWithId());
            if (result == null || !(boolean) result)
                return "Не удалось добавить указанный элемент";
            else
                return "Объект успешно добавлен";
        }
        //endregion
        //region Возвращает список
        if (commandName.equals(Command.Titles.printFieldDescendingWeaponType)) {
            List<WeaponType> weaponTypes = (List<WeaponType>) currentCommand.Execute();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < weaponTypes.size(); i++) {
                result.append((i + 1)).append(". ").append(weaponTypes.get(i)).append("\n");
            }
            return result.toString();
        }
        if (commandName.equals(Command.Titles.filterContainsName)) {
            List<SpaceMarine> marines = (List<SpaceMarine>) currentCommand.Execute(params);
            StringBuilder result = new StringBuilder();
            for (SpaceMarine marine : marines) {
                result.append(marine).append("\n");
            }
            return result.toString();
        }
        //endregion

        return null;
    }


    /**
     * Выбполняет команду с несколькими параметрами в виде объектов
     *
     * @param commandName имя команды
     * @param params      параметры
     * @return Объект с результатом
     * @throws Exception
     */
    public Object Execute(String commandName, Object[] params) throws Exception {
        throw new NotImplementedException();
    }

    public Object Execute(String commandName, Data data) throws Exception {
        throw new NotImplementedException();
    }

    public ArrayList<Data> ReadScript() {
        ArrayList<Data> commands = new ArrayList<>();
        try {
            while (true) {
                this.Print("Введите команду:", !this.isReadFromFile);
                String command = scanner.nextLine();
                String[] splitCommand = command.split(" ", 2);
                Command commandObject = this.commandHelp.GetCommand(splitCommand[0]);
                String commandParams = splitCommand.length > 1 ? splitCommand[1] : null;
                commands.add(new Data(commandObject, commandParams));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return commands;
    }

    public void Start() {
        try {
            while (true) {
                this.Print("Введите команду:", !this.isReadFromFile);
                String command = scanner.nextLine();
                String[] splitCommand = command.split(" ", 2);
                try {
                    Object result = Execute(splitCommand[0], splitCommand.length > 1 ? splitCommand[1] : null);
                    this.commandResponses.add(result.toString());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //endregion


}
