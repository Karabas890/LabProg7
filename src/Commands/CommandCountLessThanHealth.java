package Commands;//package commands;
//
//import Models.CollectionManager;
//import data.SpaceMarine;
//

import Models.CollectionManager;
import Server.Data;

public class CommandCountLessThanHealth extends Command {

    public CommandCountLessThanHealth(CollectionManager collectionManager) {
        super(
                Titles.countLessThanHealth,
                Descriptions.countLessThanHealth,
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
            if (data.data[0] == null)
                throw new Exception("Не укзано значение здоровья");
            return collectionManager.countLessThanHealth(Integer.parseInt(data.data[0].toString()));
        } else {
            return null;
        }
    }

}
