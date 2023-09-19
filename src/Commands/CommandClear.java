package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandClear extends Command {

    public CommandClear(CollectionManager collectionManager) {
        super(
                Titles.clear,
                Descriptions.clear,
                collectionManager
        );
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
        if (data == null || data.currentUser == null)
            this.collectionManager.clear();
        else {
            this.collectionManager.clear(data.currentUser.getId());
        }
        return null;
    }


}
