package model;

/**
 * Class for exceptions caused by moves that break game rules
 * TODO: change this to a normal exception and handle all the resultant handling requirements
 */
public class GameException extends RuntimeException {
    public GameException(String message) {
        super(message);
    }
}
