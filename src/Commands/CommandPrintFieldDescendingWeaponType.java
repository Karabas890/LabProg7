package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandPrintFieldDescendingWeaponType extends Command {

    public CommandPrintFieldDescendingWeaponType(CollectionManager collectionManager) {
        super(
                Titles.printFieldDescendingWeaponType,
                Descriptions.printFieldDescendingWeaponType,
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
        return this.collectionManager.printFieldDescendingWeaponType();
    }


}
