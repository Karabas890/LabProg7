package Exceptions;

public class CurrentUserMissedException extends RuntimeException {
    public CurrentUserMissedException() {
        super("Не переданы данные пользователя!");
    }
}
