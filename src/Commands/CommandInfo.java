package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandInfo extends Command {

    public CommandInfo(CollectionManager collectionManager) {
        super(
                Titles.info,
                Descriptions.info,
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
        return this.collectionManager.info();
    }


}
