package Models;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public abstract class CollectionManager implements Serializable {

    //region Поля
    /**
     * Дата инициализации
     */
    protected LocalDate initializationDate;
    /**
     * Коллекция объектов
     */
    protected LinkedList<SpaceMarine> marines;
    /**
     * Список выполненных команд
     */
    protected LinkedList<String> commandHistory;
    /**
     * Последний вставленный ID
     */
    protected int lastId = 0;
    /**
     * Скрипты выполняемые в данный момента командами execute_script
     */
    protected HashSet<String> executedScripts;
    //endregion

    //region Конструкторы
    public CollectionManager() {
        executedScripts = new HashSet<>();
        marines = new LinkedList<>();
        initializationDate = LocalDate.now();
        commandHistory = new LinkedList<>();
    }
    //endregion

    //region Методы

    //region Загрузка

    /**
     * Загрузка из файла
     */
    public abstract boolean Load(String filePath) throws Exception;

    public abstract boolean Load() throws Exception;
    //endregion

    //region Сохранение
    public abstract boolean Save(String filePath) throws Exception;

    public abstract boolean Save() throws Exception;
    public  int AddToBd(SpaceMarine marine)throws Exception{
        return -1;
    }
    public  boolean ClearBd(int userId)throws Exception{
        return false;
    }
    public  boolean RevomeByIdBd(int userId,int spacemarineid)throws Exception{
        return false;
    }
    public  boolean RevomeHeadBd(int userId)throws Exception{
        return false;
    }
    public  boolean UpdateBd(int userId,int merineID,SpaceMarine marine)throws Exception{
        return false;
    }
            ;
    //endregion

    //region Исполняемые скрипты
    public void AddExecuteScript(String fileName) {
        executedScripts.add(fileName);
    }

    public void RemoveExecuteScript(String fileName) {
        executedScripts.remove(fileName);
    }

    public boolean CheckExecuteScript(String fileName) {
        return executedScripts.contains(fileName);
    }

    //endregion

    public int GetSize() {
        if (this.marines == null)
            return 0;
        return this.marines.size();
    }

    public List<SpaceMarine> GetMarinesByUserId(int userId) {
        return marines.stream()
                .filter(marine -> marine.getIdUser() == userId)
                .collect(Collectors.toList());
    }

    public SpaceMarine GetMarineById(int id) {
        return marines.stream()
                .filter(marine -> marine.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public boolean removeMarineByIdAndUserId(int id, int userId) {
        SpaceMarine marine = getMarineById(id);
        if (marine != null && marine.getIdUser() == userId) {
            marines.remove(marine);
            return true;
        }
        return false;
    }

    public boolean clear(int userId) {

        synchronized (marines) {
            return marines.removeIf(marine -> marine.getIdUser() == userId);
        }
    }

    public boolean update(int id, SpaceMarine newMarine, int userId) throws Exception {
        SpaceMarine marine = getMarineById(id);
        synchronized (marines) {
            if (marine != null && marine.getIdUser() == userId) {
                marine.setName(newMarine.getName());
                marine.setCoordinates(newMarine.getCoordinates());
                marine.setCreationDate(newMarine.getCreationDate());
                marine.setHealth(newMarine.getHealth());
                marine.setAchievements(newMarine.getAchievements());
                marine.setAstartes(newMarine.getAstartes());
                marine.setWeapon(newMarine.getWeapon());
                marine.setChapter(newMarine.getChapter());
                return true;
            }
        }
        return false;
    }

    /**
     * Возвращает коллекцию объектов менеджера
     *
     * @return
     */
    public LinkedList<SpaceMarine> getMarines() {
        return marines;
    }

    /**
     * Возвращает дату инициализации
     *
     * @return
     */
    public LocalDate getInitializationDate() {
        return initializationDate;
    }

    /**
     * Добавляет элемент в коллекцию, делает сортировку
     *
     * @param marine
     * @return последний вставленный ID
     * @throws Exception
     */
    public int add(SpaceMarine marine) throws Exception {
        if (this.marines == null)
            this.marines = new LinkedList<>();
        if (marine.getId() <= lastId) {
            marine.setId(++lastId);
        } else {
            lastId = marine.getId();
        }
        synchronized (marines) {
            marines.add(marine);
            Collections.sort(marines);
        }
        return lastId;
    }

    /**
     * Обновляет значение элемента коллекции, id которого равен заданному, делает сортировку
     *
     * @param id     ИД обновлемого объекта
     * @param marine новый объект для замены
     * @return True - если запись была найдена и обновлена
     */
    public boolean update(int id, SpaceMarine marine) throws Exception {
        for (int i = 0; i < marines.size(); i++) {
            if (marines.get(i).getId() == id) {
                synchronized (marines) {
                    marine.setId(id);
                    marines.set(i, marine);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Удаляет элемент из коллекции по ИД
     *
     * @param id ИД удаляемого объекта
     */
    public boolean removeById(int id) {
        synchronized (marines) {
            return marines.removeIf(marine -> marine.getId() == id);
        }
    }

    /**
     * Очищает коллекцию
     */
    public void clear() {
        synchronized (marines) {
            marines.clear();
        }
    }

    /**
     * Выводит первый элемент коллекции и удаляет его
     */
    public SpaceMarine removeHead() {
        synchronized (marines) {
            return marines.pollFirst();
        }
    }

    public boolean removeHead(int idUser) {
        if (!marines.isEmpty()) {
            SpaceMarine firstMarine = marines.getFirst();
            if (firstMarine.getIdUser() == idUser) {
                synchronized (marines) {
                    marines.removeFirst();
                }
                return true;
            } else {
                return false;
            }
        } else {
            throw new NoSuchElementException("The list is empty, there is no head to remove.");
        }
    }


    /**
     * Добавить новый элемент в коллекцию,
     * если его значение меньше, чем у наименьшего элемента этой коллекции
     *
     * @param marine Объект для добавления
     */
    public boolean addIfMin(SpaceMarine marine) {
        SpaceMarine minMarine = Collections.min(marines);
        if (marine.compareTo(minMarine) < 0) {
            synchronized (marines) {
                marines.addFirst(marine);
            }
            return true;
        }
        return false;
    }

    /**
     * Добавляет указанную команду в
     * список последних выполненных команд
     *
     * @param command команда для добавления
     */
    public void addToHistory(String command) {
        synchronized (commandHistory) {
            if (commandHistory.size() == 5) {
                commandHistory.removeFirst();
            }
            commandHistory.addLast(command);
        }
    }

    /**
     * Возвращает список выполненных команд
     *
     * @return
     */
    public LinkedList<String> history() {
        return commandHistory;
    }

    /**
     * Возвращает количество объектов у которых значение здоровья меньше указанного
     *
     * @param health значение здоровья
     * @return
     */
    public long countLessThanHealth(int health) {
        return marines.stream().filter(marine -> marine.getHealth() < health).count();
    }

    /**
     * Вывести элементы, значение поля name которых содержит заданную подстроку
     *
     * @param name подстрока для поиска
     * @return
     */
    public List<SpaceMarine> filterContainsName(String name) {
        return marines.stream().filter(marine -> marine.getName().contains(name)).collect(Collectors.toList());
    }

    /**
     * Вывести значения поля weaponType всех элементов в порядке убывания
     *
     * @return
     */
    public List<WeaponType> printFieldDescendingWeaponType() throws Exception {
        return marines.stream()
                .map(SpaceMarine::getWeaponType)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает информацию о коллекции в виде строки.
     *
     * @return информация о коллекции
     */
    public String info() {
        String collectionType = marines.getClass().getName();
        String initializationDate = String.valueOf(this.getInitializationDate());
        int numberOfElements = marines.size();

        return String.format("Type of collection: %s%n" +
                "Initialization date: %s%n" +
                "Number of elements: %d", collectionType, initializationDate, numberOfElements);
    }

    /**
     * Возвращает элементы коллекции в виде строки.
     *
     * @return элементы коллекции
     */
    public String show() {
        if (marines.isEmpty()) {
            return "Коллекция пуста!";
        }
        return marines.stream()
                .map(SpaceMarine::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Возвращает строку, содержащую информацию о пехотинцах, принадлежащих указанному пользователю.
     * Если коллекция пуста, возвращается сообщение "Коллекция пуста!".
     *
     * @param userId идентификатор пользователя
     * @return строка с информацией о пехотинцах пользователя или сообщение "Коллекция пуста!"
     */
    public String show(int userId) {
        if (marines.isEmpty()) {
            return "Коллекция пуста!";
        }
        return marines.stream()
                .filter(marine -> marine.getIdUser() == userId)
                .map(SpaceMarine::toString)
                .collect(Collectors.joining("\n"));
    }


    /**
     * Класс, возвращающий коллеклицю Spacemarine'ов
     *
     * @return
     */
    public LinkedList<SpaceMarine> getCollection() {
        return marines;
    }

    /**
     * Этот метод будет возвращать первый объект SpaceMarine,
     * у которого id совпадает с заданным, или null,
     * если такого объекта в коллекции нет.
     *
     * @param id ID объекта
     * @return
     */
    public SpaceMarine getMarineById(int id) {
        return marines.stream()
                .filter(marine -> marine.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает последний добавленный ID объекта
     *
     * @return последний добавленный ID объекта
     */
    public int getLastId() {
        return lastId;
    }

    /**
     * Аутентифицирует пользователя по заданному логину и паролю.
     *
     * @param login    Логин пользователя.
     * @param password Пароль пользователя.
     * @return Аутентифицированный пользователь.
     * @throws Exception Если произошла ошибка в процессе аутентификации.
     */
    public abstract User Login(String login, String password) throws Exception;

    /**
     * Регистрирует нового пользователя с заданным логином и паролем.
     *
     * @param login    Логин пользователя.
     * @param password Пароль пользователя.
     * @return Зарегистрированный пользователь.
     * @throws Exception Если произошла ошибка в процессе регистрации.
     */
    public abstract User Register(String login, String password) throws Exception;
    //endregion

}

