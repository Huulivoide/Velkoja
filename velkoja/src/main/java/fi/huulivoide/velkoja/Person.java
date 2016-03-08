package fi.huulivoide.velkoja;

import android.support.annotation.NonNull;

import java.util.List;

public class Person
{
    public final long id;
    public final String name;
    public final String iban;
    public final String bic;
    public final List<Debt> unpaid;
    public final List<Debt> paid;

    public Person(@NonNull long id, @NonNull String name, @NonNull String iban, @NonNull String bic,
                  @NonNull List<Debt> unpaid, @NonNull List<Debt> paid) {
        this.id = id;
        this.name = name;
        this.iban = iban;
        this.bic = bic;
        this.unpaid = unpaid;
        this.paid = paid;
    }
}
