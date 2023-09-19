package Models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс Coordinates содержит координаты x и y с определенными условиями.
 */
public class Coordinate implements Serializable {

    //region Поля
    /**
     * ID объекта
     */
    private int id;
    /**
     * Координата X.
     */
    private float x;
    /**
     * Координата Y.
     */
    private int y;
    //endregion

    //region Конструкторы

    /**
     * Конструктор по умолчанию для класса Coordinates.
     */
    public Coordinate() throws Exception {
        this(0, 0, 0);
    }

    public Coordinate(int id, float x, int y) throws Exception {
        setId(id);
        setX(x);
        setY(y);
    }

    /**
     * Конструктор для создания объекта Coordinates с заданными координатами.
     *
     * @param x координата X
     * @param y координата Y
     * @throws Exception если значения координат некорректны
     */
    public Coordinate(float x, int y) throws Exception {
        this(0, x, y);
    }

    /**
     * Создает новый объект Coordinates путем копирования значений из другого объекта Coordinates.
     *
     * @param coordinate объект Coordinates, который будет скопирован
     * @throws Exception если происходит ошибка в процессе копирования
     */
    public Coordinate(Coordinate coordinate) throws Exception {
        this(coordinate.getX(), coordinate.getY());
    }
    //endregion


    //region Геттеры и сеттеры

    /**
     * Возвращает значение ID объекта
     *
     * @return ID объекта
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает значение ID объекта
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
     * Возвращает значение x координаты
     *
     * @return значение x
     */
    public float getX() {
        return x;
    }

    /**
     * Устанавливает значение x координаты
     *
     * @param x значение x
     * @throws Exception если значение x некорректно
     */
    public void setX(float x) throws Exception {
        if (x <= -298)
            throw new Exception("Значение X должно быть больше -298");
        this.x = x;
    }

    /**
     * Возвращает значение y координаты
     *
     * @return значение y
     */
    public int getY() {
        return y;
    }

    /**
     * Устанавливает значение y координаты
     *
     * @param y значение y
     * @throws Exception если значение y некорректно
     */
    public void setY(int y) throws Exception {
        if (y <= -126)
            throw new Exception("Значение Y должно быть больше -126");
        this.y = y;
    }
    //endregion

    //region Переопределения
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Float.compare(that.x, x) == 0 && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "id=" + id +
                "x=" + x +
                ", y=" + y +
                '}';
    }
    //endregion

}
