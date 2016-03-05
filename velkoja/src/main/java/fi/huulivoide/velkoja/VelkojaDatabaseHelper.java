package fi.huulivoide.velkoja;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VelkojaDatabaseHelper extends SQLiteOpenHelper
{
    private static final String CREATE_PEOPLE =
            "CREATE TABLE people (" +
            "id INTEGER PRIMARY KEY," +
            "name TEXT NOT NULL," +
            "iban TEXT NOT NULL);";

    private static final String CREATE_DEBTS =
            "CREATE TABLE debts (" +
            "id INTEGER PRIMARY KEY," +
            "description TEXT NOT NULL," +
            "sum REAL NOT NULL," + // No one is going to miss missing 0.000000000000001€
            "due INTEGER NOT NULL," +
            "paid INTEGER," +
            "person INTEGER NOT NULL," +
            "FOREIGN KEY(person) REFERENCES people(id))";

    private static final String DB_NAME = "Velkoja.db";
    private static final int DB_VERSION = 1;

    VelkojaDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_PEOPLE);
        db.execSQL(CREATE_DEBTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        // Implement when/if the DB schema gets updated
    }

    @Override
    public void onConfigure(SQLiteDatabase db)
    {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
