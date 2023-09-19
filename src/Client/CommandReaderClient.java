package Client;

import Commands.Command;
import Commands.CommandReader;
import Server.Data;

import java.io.IOException;
import java.io.InputStream;

public class CommandReaderClient extends CommandReader {
    /**
     * Конструктор CommandReader.
     *
     * @param inputStream входной поток для чтения команд
     */
    public CommandReaderClient(InputStream inputStream) throws IOException {
        super(null, inputStream);
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
        Command currentCommand = this.commandHelp.GetCommand(commandName);
        if (currentCommand == null) {
            throw new Exception(String.format("Клиент: Получена несуществующая команда '%s'", commandName));
        }
        //region Ничего не возвращают
        if (commandName.equals(Command.Titles.exit)) {
            currentCommand.Execute();
            return null;
        }
        //endregion
        //region Возвращают Data
        if (commandName.equals(Command.Titles.save))
            return new Data(currentCommand, false);
        if (commandName.equals(Command.Titles.clear) || commandName.equals(Command.Titles.removeHead) ||
                commandName.equals(Command.Titles.help) || commandName.equals(Command.Titles.history) ||
                commandName.equals(Command.Titles.show) || commandName.equals(Command.Titles.info) ||
                commandName.equals(Command.Titles.countLessThanHealth) || commandName.equals(Command.Titles.printFieldDescendingWeaponType) ||
                commandName.equals(Command.Titles.filterContainsName))
            return new Data(currentCommand, null);
        if (commandName.equals(Command.Titles.removeById) || commandName.equals(Command.Titles.executeScript))
            return new Data(currentCommand, params[0]);
        if (commandName.equals(Command.Titles.add)) {
            this.UpdateReader();
            return new Data(currentCommand, this.inputReader.GetSpaceMarine());
        }
        if (commandName.equals(Command.Titles.update)) {
            this.UpdateReader();
            return new Data(currentCommand, new Object[]{
                    this.inputReader.GetInt("Введите ID объекта, который хотите обновить"),
                    this.inputReader.GetSpaceMarine()}
            );
        }
        if (commandName.equals(Command.Titles.addIfMin)) {
            this.UpdateReader();
            return new Data(currentCommand, this.inputReader.GetSpaceMarineWithId());
        }
        if (commandName.equals(Command.Titles.login) || commandName.equals(Command.Titles.register)) {
//            this.UpdateReader();  String l = this.inputReader.GetStr("Введите логин");
////            String p = this.inputReader.GetStr("Введите пароль");
//
            return new Data(currentCommand, new Object[]{
                    this.inputReader.GetStr("Введите логин"),
                    this.inputReader.GetStr("Введите пароль")
            });
        }
        //endregion
        return null;
    }

    public Object Execute(String commandName, String params) throws Exception {
        return null;
    }


}
