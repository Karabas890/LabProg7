package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandHistory extends Command {

    public CommandHistory(CollectionManager collectionManager) {
        super(
                Titles.history,
                Descriptions.history,

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
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : this.collectionManager.history()) {
            stringBuilder.append(item);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }


}
