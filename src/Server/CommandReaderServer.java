package Server;

import Commands.Command;
import Commands.CommandReader;
import Models.CollectionManager;
import Models.SpaceMarine;
import Models.User;
import Models.WeaponType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CommandReaderServer extends CommandReader {
    //region Конструкторы

    /**
     * Конструктор CommandReader.
     *
     * @param collectionManager менеджер коллекции
     * @param inputStream       входной поток для чтения команд
     */
    public CommandReaderServer(CollectionManager collectionManager, InputStream inputStream) throws IOException {
        super(collectionManager, inputStream);
    }

    public CommandReaderServer(CollectionManager collectionManager, InputStream inputStream, User currentUser) throws IOException {
        super(collectionManager, inputStream, currentUser);
    }
    //endregion


    public Object Execute(String commandName, String params) throws Exception {
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
    @Override
    public Object Execute(String commandName, Object[] params) throws Exception {
        return this.Execute(commandName, new Data(null, params));
    }

    @Override
    public Object Execute(String commandName, Data data) throws Exception {
        this.UpdateReader();
        this.currentUser = data.currentUser;
        Command currentCommand = this.commandHelp.GetCommand(commandName);
        if (currentCommand == null) {
            throw new Exception(String.format("Сервер: Получена несуществующая команда '%s'", commandName));
        }
        //region Ничего не возвращают
        if (commandName.equals(Command.Titles.clear)) {
            if (this.collectionManager.ClearBd(data.currentUser.getId()))
                currentCommand.Execute(data);
        }
        if (commandName.equals(Command.Titles.exit))
            currentCommand.Execute(data);
        if (commandName.equals(Command.Titles.removeHead)) {
         //  int lastid= this.collectionManager.getLastId();
           int userid=data.currentUser.getId();
            if (this.collectionManager.RevomeHeadBd(userid)) {
                Object result = currentCommand.Execute(data);
                return "Объект успешно удален";
            }
            else
                return "Не удалось удалить данный объект";
        }
        if (commandName.equals(Command.Titles.executeScript)) {
        if (data.data.length < 1)
                throw new Exception("Не передано имя файла!");
            this.commandResponses.clear();
            currentCommand.SetCommandReader(new CommandReaderServer(this.collectionManager, new FileInputStream(data.data[0].toString()), data.currentUser));
            currentCommand.Execute(data);
            return currentCommand.GetCommandReader().GetCommandResponses();
        }
        //endregion
        //region Возвращают строку
        if (commandName.equals(Command.Titles.help) || commandName.equals(Command.Titles.history) ||
                commandName.equals(Command.Titles.show) || commandName.equals(Command.Titles.info))
            return currentCommand.Execute(data.currentUser);
        //endregion
        //region Возвращают число
        if (commandName.equals(Command.Titles.add)) {
            if (this.isReadFromFile)
                data.data = new Object[]{this.inputReader.GetSpaceMarine()};
            if (data.data == null || data.data.length == 0)
                throw new Exception("Передано неверное число аргументов");
            SpaceMarine newItem = (SpaceMarine) data.data[0];
            newItem.setId(collectionManager.getLastId()+1);
            newItem.setIdUser(data.currentUser.getId());
            int idNew=this.collectionManager.AddToBd(newItem);
            if(idNew==0) {
               //  ((SpaceMarine) data.data[0]).setId(idNew);
                Object result = currentCommand.Execute(data);
                if (result == null)
                    return "Не удалось добавить объект";
                else
                    return String.format("Объект добавлен, его ID в коллекции:%d", Integer.valueOf(result.toString()));
            }else{
                return "Не удалось добавить объект";
            }


        }
        if (commandName.equals(Command.Titles.countLessThanHealth)) {
            if (data.data == null || data.data.length == 0)
                throw new Exception("Передано неверное число аргументов");
            Object result = currentCommand.Execute(data);
            if (result == null)
                return "Не удалось найти значение";
            else
                return String.format("Число объектов со здоровьем меньше указанного:%d", Integer.valueOf(result.toString()));
        }
        //endregion
        //region Возвращают булевское значение
        if (commandName.equals(Command.Titles.save)) {
            currentCommand.CheckParams(data.data, 1);
            if ((Boolean.parseBoolean(data.data[0].toString()))) {
                Object result = currentCommand.Execute(null);
                if (result == null || !((boolean) result))
                    return "Не удалось сохранить коллекцию";
                else
                    return "Коллекция сохранена";
            } else {
                return "Сохранение может быть вызвано только на стороне сервера";
            }
        }
        if (commandName.equals(Command.Titles.load)) {
            currentCommand.CheckParams(data.data, 1);
            if ((Boolean.parseBoolean(data.data[0].toString()))) {
                Object result = currentCommand.Execute(null);
                if (result == null || !((boolean) result))
                    return "Не удалось загрузить коллекцию";
                else
                    return "Коллекция загружена";
            } else {
                return "Загрузка может быть вызвана только на стороне сервера";
            }
        }
        if (commandName.equals(Command.Titles.update)) {
            if (data.data == null || data.data.length != 2)
                throw new Exception("Передано неверное число аргументов");
            int marineID=Integer.parseInt(data.data[0].toString());
            int userId=data.currentUser.getId();
            SpaceMarine marine=(SpaceMarine) data.data[1];
            Object result = currentCommand.Execute(data);
            if (result == null || !((boolean) result))
                return "Не удалось обновить указанный элемент";
            else {
                //marine.setId(marineID);
                //marine.setIdUser(data.currentUser.getId());
                if(this.collectionManager.UpdateBd( userId,marineID,marine))
                return "Объект успешно обновлен";else{
                    return "Объект не обновлен";
                }
            }

        }
        if (commandName.equals(Command.Titles.removeById)) {
            if (data.data == null || data.data.length != 1)
                throw new Exception("Передано неверное число аргументов");
           if (this.collectionManager.RevomeByIdBd(data.currentUser.getId(),Integer.parseInt((String) data.data[0]))) {
               Object result = currentCommand.Execute(data);
               return "Объект успешно удален";
           }
            else
                return "Не удалось удалить данный объект";
        }
        if (commandName.equals(Command.Titles.addIfMin)) {
            if (data.data == null || data.data.length != 1)
                throw new Exception("Передано неверное число аргументов");
            Object result = currentCommand.Execute(data);
            if (result == null || !((boolean) result))
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
            List<SpaceMarine> marines = (List<SpaceMarine>) currentCommand.Execute(data);
            StringBuilder result = new StringBuilder();
            for (SpaceMarine marine : marines) {
                result.append(marine).append("\n");
            }
            return result.toString();
        }
        //endregion
        //region Возвращает объект
        if (commandName.equals(Command.Titles.login) || commandName.equals(Command.Titles.register)) {
            return currentCommand.Execute(data);
        }
        //endregion
        return null;
    }

    public void Start() {
        try {
            while (true) {
                this.Print("Введите команду:", !this.isReadFromFile);
                String command = scanner.nextLine();
                String[] splitCommand = command.split(" ", 2);
                try {
                    //Start(splitCommand[0], splitCommand.length > 1 ? splitCommand[1] : null);
                    Data data = new Data(null, splitCommand.length > 1 ? splitCommand[1] : null);
                    data.currentUser = this.currentUser;
                    Object result = Execute(splitCommand[0], data);
                  //  this.collectionManager.Save();
                    if (result != null)
                        this.commandResponses.add(result.toString());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
