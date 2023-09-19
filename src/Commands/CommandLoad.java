package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandLoad extends Command {

    public CommandLoad(CollectionManager collectionManager) {
        super(
                Titles.load,
                Descriptions.load,
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
        return collectionManager.Load();
    }


}
