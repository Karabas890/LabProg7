package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandRemoveHead extends Command {

    public CommandRemoveHead(CollectionManager collectionManager) {
        super(
                Titles.removeHead,
                Descriptions.removeHead,
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
        if (data == null || data.currentUser == null) {
            return this.collectionManager.removeHead();
        } else {
            if (data.currentUser != null)
                return this.collectionManager.removeHead(data.currentUser.getId());
        }
        return null;
    }


}
