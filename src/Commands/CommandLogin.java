package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandLogin extends Command {
    public CommandLogin(CollectionManager collectionManager) {
        super(
                Titles.login,
                Descriptions.login,
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
        if (this.CheckParams(data.data, 2))
            return collectionManager.Login((String) data.data[0], (String) data.data[1]);
        else
            return null;
    }


}
