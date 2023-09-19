package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandWait extends Command {

    public CommandWait(CollectionManager collectionManager) {
        super(
                Titles.wait,
                Descriptions.wait,
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
        if (this.CheckParams(data, 1)) {
            Thread.sleep(Long.parseLong(data.data[0].toString()));
            System.exit(0);
        }
        return null;
    }


}
