package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandSave extends Command {
    public CommandSave(CollectionManager collectionManager) {
        super(
                Titles.save,
                Descriptions.save,
                collectionManager);
    }

    /**
     * Выполняет команду с хранилищем объектов
     *
     * @param data данные, полученные от клиента или сервера
     * @return результат выполнения команды
     * @throws Exception исключение при выполнении команды
     */
    @Override
    protected Object excecute(Data data) throws Exception {
        return collectionManager.Save();
    }


}
