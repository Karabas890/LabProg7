package Models;

import java.io.Serializable;

/**
 * Пользователь
 */
public class User implements Serializable {
    //region Поля
    private int id;
    private String login;
    private String password;
    //endregion

    //region Конструкторы
    public User(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }
    //endregion

    //region Методы
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    //endregion

    //region Переопределения

    /**
     * Возвращает строковое представление объекта User.
     *
     * @return строковое представление объекта User
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login=" + login +
                ", password=" + password +
                '}';
    }
//endregion
}

