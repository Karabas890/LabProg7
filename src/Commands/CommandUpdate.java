package Commands;

import Models.CollectionManager;
import Models.SpaceMarine;
import Server.Data;

public class CommandUpdate extends Command {

    public CommandUpdate(CollectionManager collectionManager) {
        super(
                Titles.update,
                Descriptions.update,
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
        if (this.CheckParams(data, 2)) {
            if (data.data[0] == null)
                throw new Exception("Не указан ID!");
            if (data.data[1] == null)
                throw new Exception("Не указан Морпех!");
            if (data.currentUser == null) {
                return this.collectionManager.update(
                        Integer.parseInt(data.data[0].toString()),
                        (SpaceMarine) data.data[1]
                );
            } else {
                return this.collectionManager.update(
                        Integer.parseInt(data.data[0].toString()),
                        (SpaceMarine) data.data[1],
                        data.currentUser.getId()
                );
            }
        } else {
            return null;
        }
    }


}
