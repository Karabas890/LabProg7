package Exceptions;

public class NotImplementedException extends RuntimeException {
    public NotImplementedException() {
        super("Метод/функция не реализован!");
    }
}

