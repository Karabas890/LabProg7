package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandShow extends Command {

    public CommandShow(CollectionManager collectionManager) {
        super(
                Titles.show,
                Descriptions.show,
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
            if (data.currentUser != null) {
                return this.collectionManager.show(data.currentUser.getId());
            }
        }
        return this.collectionManager.show();
    }


}
