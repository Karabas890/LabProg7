package Commands;

import Models.CollectionManager;
import Server.Data;

public class CommandExit extends Command {

    public CommandExit(CollectionManager collectionManager) {
        super(
                Titles.exit,
                Descriptions.exit,
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
        System.out.println("Выход из программы!");
        //System.exit(0);
        Thread.currentThread().interrupt();
        return null;
    }


}
