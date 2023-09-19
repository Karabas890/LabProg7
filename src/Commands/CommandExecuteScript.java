
package Commands;

import Models.CollectionManager;
import Server.Data;

import java.io.*;

public class CommandExecuteScript extends Command {

    public CommandExecuteScript(CollectionManager collectionManager) {
        super(
                Titles.executeScript,
                Descriptions.executeScript,
                collectionManager
        );
    }

    /**
     * Выполняет команду с хранилищем объектов
     *
     * @param data параметры команды
     * @return результат выполнения команды
     */
    @Override
    protected Object excecute(Data data) throws Exception {
        if (this.CheckParams(data, 1)) {
            String fileName = data.data[0].toString();
            FileInputStream fileInputStream = new FileInputStream(fileName);
            if (this.collectionManager.CheckExecuteScript(fileName)) {
                throw new Exception("Обнаружен рекурсивный вызов");
            }

            //CommandReader commandReader = new CommandReader(this.collectionManager, fileInputStream);
            //commandReader.Start();
            if (this.commandReader != null) {
                this.collectionManager.AddExecuteScript(fileName);
                commandReader.Start();
            }
            this.collectionManager.RemoveExecuteScript(fileName);

        }
        return null;
    }

}

