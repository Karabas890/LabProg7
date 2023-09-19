package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandRemoveById extends Command {

    public CommandRemoveById(CollectionManager collectionManager) {

        super(
                Titles.removeById,
                Descriptions.removeHead,
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
        if (this.CheckParams(data, 1)) {
            if (data.data[0] == null)
                throw new Exception("Не указан ID!");
            if (data.currentUser != null)
                return this.collectionManager.removeMarineByIdAndUserId(Integer.parseInt(data.data[0].toString()), data.currentUser.getId());
        } else {
            return null;
        }
        return null;
    }


}
