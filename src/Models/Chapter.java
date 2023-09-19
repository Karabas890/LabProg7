package Models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс Chapter представляет собой главу, содержащую имя и родительский легион.
 */
public class Chapter implements Serializable {

    //region Поля
    /**
     * ID объекта
     */
    private int id;
    private String name;            // Поле не может быть null, Строка не может быть пустой
    private String parentLegion;
    //endregion

    //region Конструкторы
    public Chapter() {
        this.id = 0;
    }

    public Chapter(int id, String name, String parentLegion) throws Exception {
        this(name, parentLegion);
        setId(id);
    }

    /**
     * Создает главу с указанными параметрами
     *
     * @param name         имя
     * @param parentLegion родительский легион
     * @throws Exception ошибка при создании
     */
    public Chapter(String name, String parentLegion) throws Exception {
        setId(0);
        setName(name);
        setParentLegion(parentLegion);
    }

    /**
     * Конструктор для создания объекта Chapter путем копирования значений из другого объекта Chapter.
     *
     * @param chapter объект Chapter, который будет скопирован
     * @throws Exception если происходит ошибка в процессе копирования
     */
    public Chapter(Chapter chapter) throws Exception {
        this(chapter.getId(), chapter.getName(), chapter.getParentLegion());
    }

    //endregion

    //region Геттеры и сеттеры

    /**
     * Возвращает ID объекта
     *
     * @return ID объекта
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает ID объекта
     *
     * @param id ID объекта
     * @throws Exception если значение ID отрицательное
     */
    public void setId(int id) throws Exception {
        if (id < 0)
            throw new Exception("ID не может быть отрицательным");
        this.id = id;
    }

    /**
     * Возвращает имя главы
     *
     * @return имя главы
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя главы
     *
     * @param name имя главы
     * @throws Exception если имя пустое или null
     */
    public void setName(String name) throws Exception {
        if (name == null || name.trim().isEmpty())
            throw new Exception("Имя не может быть пустым");
        this.name = name;
    }

    /**
     * Возвращает имя родительского легиона
     *
     * @return имя родительского легиона
     */
    public String getParentLegion() {
        return parentLegion;
    }

    /**
     * Устанавливает имя родительского легиона
     *
     * @param parentLegion имя родительского легиона
     */
    public void setParentLegion(String parentLegion) {
        this.parentLegion = parentLegion;
    }
    //endregion

    //region Переопределения
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(name, chapter.name) && Objects.equals(parentLegion, chapter.parentLegion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parentLegion);
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "name='" + name + '\'' +
                ", parentLegion='" + parentLegion + '\'' +
                '}';
    }
    //endregion

}
