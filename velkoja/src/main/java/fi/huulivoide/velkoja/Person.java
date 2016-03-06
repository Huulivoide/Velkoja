package fi.huulivoide.velkoja;

import android.support.annotation.NonNull;

public class Person
{
    public final long id;
    public final String name;
    public final String iban;
    public final String bic;

    public Person(@NonNull long id, @NonNull String name, @NonNull String iban, @NonNull String bic)
    {
        this.id = id;
        this.name = name;
        this.iban = iban;
        this.bic = bic;
    }
}
