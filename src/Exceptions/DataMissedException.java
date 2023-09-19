package Exceptions;

public class DataMissedException extends RuntimeException {
    public DataMissedException() {
        super("Не передан блок данных!");
    }
}
