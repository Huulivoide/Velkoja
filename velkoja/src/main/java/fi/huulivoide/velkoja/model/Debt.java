package fi.huulivoide.velkoja.model;

import android.support.annotation.NonNull;

import java.util.Calendar;

public class Debt {
    public final long id;
    public final String description;
    public final double sum;
    public final Calendar due;
    public final Calendar paid;
    public final long person;

    public Debt(long id, @NonNull String description, double sum, @NonNull Calendar due, Calendar paid, long person) {
        this.id = id;
        this.description = description;
        this.sum = sum;
        this.due = due;
        this.paid = paid;
        this.person = person;
    }
}
