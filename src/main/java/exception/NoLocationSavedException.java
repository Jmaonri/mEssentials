package exception;

public class NoLocationSavedException extends Exception{
    public NoLocationSavedException(String message){
        super(message);
    }

    public NoLocationSavedException(String message, Throwable cause){
        super(message, cause);
    }
}
