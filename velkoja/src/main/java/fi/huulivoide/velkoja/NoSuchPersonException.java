package fi.huulivoide.velkoja;

public class NoSuchPersonException extends RuntimeException {
    public NoSuchPersonException(long id) {
        super("No person with an id " + id + " in the database.");
    }

    public NoSuchPersonException(long id, Throwable reason) {
        super("No person with an id " + id + " in the database.", reason);
    }
}
