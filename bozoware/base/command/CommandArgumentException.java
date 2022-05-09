package bozoware.base.command;

public class CommandArgumentException extends Exception {

    public CommandArgumentException(String message) {
        super("\247eInvalid Arguments: \247f" + message);
    }

}
