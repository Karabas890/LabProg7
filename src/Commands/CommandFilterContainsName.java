package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandFilterContainsName extends Command {

    public CommandFilterContainsName(CollectionManager collectionManager) {
        super(
                Titles.filterContainsName,
                Descriptions.filterContainsName,
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
                throw new Exception("Не укзано имя");
            return this.collectionManager.filterContainsName(data.data[0].toString());
        }
        return null;
    }


}
