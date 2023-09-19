package Commands;

import Models.CollectionManager;
import Models.SpaceMarine;
import Server.Data;

public class CommandAdd extends Command {

    public CommandAdd(CollectionManager collectionManager) {
        super(
                Titles.add,
                Descriptions.add,
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
        if (data.data == null || data.data.length == 0)
            throw new Exception("Передано неверное число аргументов");
        SpaceMarine newItem = (SpaceMarine) data.data[0];
        if (data.currentUser != null)
            newItem.setIdUser(data.currentUser.getId());
        return this.collectionManager.add(newItem);
    }


}
