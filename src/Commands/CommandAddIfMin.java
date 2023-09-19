package Commands;

import Models.CollectionManager;
import Models.SpaceMarine;
import Server.Data;

public class CommandAddIfMin extends Command {

    public CommandAddIfMin(CollectionManager collectionManager) {
        super(
                Titles.addIfMin,
                Descriptions.addIfMin,
                collectionManager
        );
    }

    /**
     * Выполняет команду с хранилищем объектов
     *
     * @param data параметры команды
     * @return результат выполнения команды
     */
    @Override
    protected Object excecute(Data data) throws Exception {
        if (this.CheckParams(data.data, 1)) {
            return collectionManager.addIfMin((SpaceMarine) data.data[0]);
        } else {
            return null;
        }
    }


}
