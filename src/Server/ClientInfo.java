package Server;

public class ClientInfo {
    //region Поля
    private int id;
    private boolean isAuthorized;
    private int idUser;
    //endregion

    //region Конструкторы
    public ClientInfo(int id, boolean isAuthorized, int idUser) {
        this.id = id;
        this.isAuthorized = isAuthorized;
        this.idUser = idUser;
    }

    public ClientInfo(int id) {
        this(id, false, -1);
    }
    //endregion

    //region Сеттеры и геттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public int isIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    //endregion
}
