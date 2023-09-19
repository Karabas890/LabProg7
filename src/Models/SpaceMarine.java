package Models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс, представляющий космического морского пехотинца.
 * Он содержит информацию о различных атрибутах пехотинца, таких как идентификатор,
 * имя, координаты, дата создания, здоровье, достижения, категория, тип оружия и глава.
 */
public class SpaceMarine implements Comparable<SpaceMarine>, Serializable {

    //region Поля
    /**
     * Уникальный идентификатор SpaceMarine.
     */
    private int id;

    /**
     * Имя SpaceMarine.
     */
    private String name;

    /**
     * Координаты SpaceMarine.
     */
    private Coordinate coordinate;

    /**
     * Дата создания SpaceMarine.
     */
    private Timestamp creationDate;

    /**
     * Здоровье SpaceMarine.
     */
    private Integer health;

    /**
     * Достижения SpaceMarine.
     */
    private String achievements;

    /**
     * Категория Astartes SpaceMarine.
     */
    private Astartes astartes;

    /**
     * Тип оружия SpaceMarine.
     */
    private Weapon weapon;

    /**
     * Глава Chapter, к которой принадлежит SpaceMarine.
     */
    private Chapter chapter;

    /**
     * ID пользователя, который создал этот объект
     */
    private int idUser;
    //endregion

    //region Конструкторы
    public SpaceMarine() {
    }


    /**
     * @param id           идентификатор пехотинца
     * @param idUser       идентификатор пользователя, создавшего этот объект
     * @param name         имя пехотинца
     * @param coordinate   координаты пехотинца
     * @param creationDate дата создания пехотинца
     * @param health       здоровье пехотинца
     * @param achievements достижения пехотинца
     * @param astartes     категория пехотинца расы Astartes
     * @param weapon       оружие пехотинца
     * @param chapter      глава пехотинца
     * @throws Exception выбрасывает исключение, если значения атрибутов некорректны
     */
    public SpaceMarine(int id,
                       int idUser,
                       String name,
                       Coordinate coordinate,
                       Timestamp creationDate,
                       Integer health,
                       String achievements,
                       Astartes astartes,
                       Weapon weapon,
                       Chapter chapter) throws Exception {
        this.setId(id);
        this.setIdUser(idUser);
        this.setName(name);
        this.setCoordinates(coordinate);
        this.setCreationDate(creationDate);
        this.setHealth(health);
        this.setAchievements(achievements);
        this.setAstartes(astartes);
        this.setWeapon(weapon);
        this.setChapter(chapter);
    }


    /**
     * @param id               идентификатор пехотинца
     * @param name             имя пехотинца
     * @param coordinate       координаты пехотинца
     * @param creationDate     дата создания пехотинца
     * @param health           здоровье пехотинца
     * @param achievements     достижения пехотинца
     * @param astartesCategory категория пехотинца расы Astartes
     * @param weaponType       тип оружия
     * @param chapter          глава пехотинца
     * @throws Exception выбрасывает исключение, если значения атрибутов некорректны
     */
    public SpaceMarine(int id,
                       String name,
                       Coordinate coordinate,
                       Timestamp creationDate,
                       Integer health,
                       String achievements,
                       Category astartesCategory,
                       WeaponType weaponType,
                       Chapter chapter) throws Exception {
        this(id, -1, name, coordinate, creationDate, health, achievements, new Astartes(astartesCategory), new Weapon(weaponType), chapter);
    }

    public SpaceMarine(int id,
                       String name,
                       Coordinate coordinate,
                       Integer health,
                       String achievements,
                       Category astartesCategory,
                       WeaponType weaponType,
                       Chapter chapter) throws Exception {
        this.setId(id);
        this.setIdUser(-1);
        this.setName(name);
        this.setCoordinates(coordinate);
        this.setCreationDate(this.getCurrentTimestamp());
        this.setHealth(health);
        this.setAchievements(achievements);
        this.setAstartes(new Astartes(astartesCategory));
        this.setWeapon(new Weapon(weaponType));
        this.setChapter(chapter);
    }

    /**
     * Конструктор для создания объекта SpaceMarine с заданными значениями атрибутов.
     *
     * @param id           идентификатор пехотинца
     * @param name         имя пехотинца
     * @param coordinate   координаты пехотинца
     * @param creationDate дата создания пехотинца
     * @param health       здоровье пехотинца
     * @param achievements достижения пехотинца
     * @param astartes     категория пехотинца расы Astartes
     * @param weapon       оружие пехотинца
     * @param chapter      глава пехотинца
     * @throws Exception выбрасывает исключение, если значения атрибутов некорректны
     */
    public SpaceMarine(int id,
                       String name,
                       Coordinate coordinate,
                       Timestamp creationDate,
                       Integer health,
                       String achievements,
                       Astartes astartes,
                       Weapon weapon,
                       Chapter chapter) throws Exception {
        this(id, -1, name, coordinate, creationDate, health, achievements, astartes, weapon, chapter);
    }

    public SpaceMarine(int id,
                       int idUser,
                       String name,
                       Coordinate coordinate,
                       Integer health,
                       String achievements,
                       Astartes astartes,
                       Weapon weapon,
                       Chapter chapter) throws Exception {
        this.setId(id);
        this.setIdUser(idUser);
        this.setName(name);
        this.setCoordinates(coordinate);
        this.setCreationDate(this.getCurrentTimestamp());
        this.setHealth(health);
        this.setAchievements(achievements);
        this.setAstartes(astartes);
        this.setWeapon(weapon);
        this.setChapter(chapter);
    }

    public SpaceMarine(int id,
                       String name,
                       Coordinate coordinate,
                       Integer health,
                       String achievements,
                       Astartes astartes,
                       Weapon weapon,
                       Chapter chapter) throws Exception {
        this(id, -1, name, coordinate, health, achievements, astartes, weapon, chapter);
    }

    /**
     * Создает новый объект SpaceMarine путем копирования значений из другого объекта SpaceMarine.
     *
     * @param spaceMarine объект SpaceMarine, который будет скопирован
     * @throws Exception если происходит ошибка в процессе копирования
     */
    public SpaceMarine(SpaceMarine spaceMarine) throws Exception {
        this(spaceMarine.getId(),
                spaceMarine.getIdUser(),
                spaceMarine.getName(),
                new Coordinate(spaceMarine.getCoordinates()),
                spaceMarine.getCreationDate(),
                spaceMarine.getHealth(),
                spaceMarine.getAchievements(),
                spaceMarine.getAstartes(),
                spaceMarine.getWeapon(),
                new Chapter(spaceMarine.getChapter()));
    }


    //endregion

    //region Сеттеры и геттеры

    //region ID
    public int getId() {
        return id;
    }

    public void setId(int id) throws Exception {
        if (id < 0)
            throw new Exception("ID не может быть отрицательным");
        this.id = id;
    }
    //endregion

    //region Name
    public String getName() {
        return name;
    }

    public void setName(String name) throws Exception {
        if (name == null || name.trim().isEmpty())
            throw new Exception("Имя не может быть пустым");
        this.name = name;
    }
    //endregion

    //region Coordinates
    public Coordinate getCoordinates() {
        return coordinate;
    }

    public void setCoordinates(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
    //endregion

    //region CreationDate
    public Timestamp getCreationDate() {
        return this.creationDate;
    }


    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }
    //endregion

    //region Health
    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) throws Exception {
        if (health < 0)
            throw new Exception("Здоровье не может быть отрицательным");
        this.health = health;
    }
    //endregion

    //region Achievements
    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) throws Exception {
        this.achievements = achievements;
    }
    //endregion

    //region AstartesCategory
    public Astartes getAstartes() {
        return astartes;
    }

    public void setAstartes(Astartes astartes) {
        this.astartes = astartes;
    }
    //endregion

    //region WeaponType
    public WeaponType getWeaponType() {
        return weapon.getType();
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }

    //endregion

    //region Chapter
    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }
    //endregion

    //endregion

    //region Методы
    public Timestamp getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        return Timestamp.valueOf(now);
    }
    //endregion

    //region Переопределения
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpaceMarine that = (SpaceMarine) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(coordinate, that.coordinate)
                && Objects.equals(creationDate, that.creationDate) && Objects.equals(health, that.health) &&
                Objects.equals(achievements, that.achievements) && astartes == that.astartes
                && weapon == that.weapon && Objects.equals(chapter, that.chapter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinate, creationDate, health, achievements, astartes, weapon, chapter);
    }

    @Override
    public String toString() {
        return "SpaceMarine{" +
                "\n\tid=" + id +
                "\n\tidUser=" + idUser +
                "\n\tname='" + name + '\'' +
                "\n\tcoordinates=" + coordinate +
                "\n\tcreationDate=" + creationDate +
                "\n\thealth=" + health +
                "\n\tachievements='" + achievements + '\'' +
                "\n\tastartes=" + astartes +
                "\n\tweapon=" + weapon +
                "\n\tchapter=" + chapter +
                "\n}";
    }


    @Override
    public int compareTo(SpaceMarine another) {
        return Integer.compare(this.getId(), another.getId());
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    //endregion
}
