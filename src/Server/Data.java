package Server;

import Commands.Command;
import Models.User;

import java.io.Serializable;

/**
 * Класс Data содержит команды и данные для пересылки информации между сервером и клиентом.
 */
public class Data implements Serializable {

    /**
     * Текущий пользователь
     */
    public User currentUser;

    /**
     * Команда, которая будет выполнена на сервере или клиенте.
     */
    public Command command;
    /**
     * Данные, связанные с командой.
     */
    public Object[] data;

    /**
     * Конструктор для создания объекта Data с заданной командой и данными.
     *
     * @param command команда
     * @param data    данные
     */
    public Data(Command command, Object[] data) {
        this.command = command;
        this.data = data;
    }

    /**
     * Конструктор для создания объекта Data с заданной командой и единственным объектом данных.
     *
     * @param command команда
     * @param data    данные
     */
    public Data(Command command, Object data) {
        this.command = command;
        this.data = new Object[]{data};
    }

    public Object Get(int index) {
        if (index >= 0 && index < data.length) {
            return data[index];
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds!");
        }
    }

    /**
     * Возвращает строковое представление объекта Data.
     *
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return "Data{" +
                "command=" + command +
                ", data=" + java.util.Arrays.toString(data) +
                '}';
    }
}
