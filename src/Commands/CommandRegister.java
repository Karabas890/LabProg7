package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandRegister extends Command {
    CommandRegister(CollectionManager collectionManager) {
        super(
                Titles.register,
                Descriptions.register,
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
        if (this.CheckParams(data, 2))
            return collectionManager.Register((String) data.data[0], (String) data.data[1]);
        else
            return null;
    }


}
