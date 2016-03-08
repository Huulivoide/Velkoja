/**
 * Copyright (c) 2016, Jesse Jaara <jesse.jaara@gmail.com>
 */

package fi.huulivoide.velkoja;

public class NoSuchDebtException extends RuntimeException {
    public NoSuchDebtException(long id) {
        super("No debt with an id " + id + " in the database.");
    }

    public NoSuchDebtException(long id, Throwable reason) {
        super("No debt with an id " + id + " in the database.", reason);
    }
}
