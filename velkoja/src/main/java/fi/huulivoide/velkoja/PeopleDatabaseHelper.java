package fi.huulivoide.velkoja;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PeopleDatabaseHelper
{
    private static final String PEOPLE_TABLE = "people";
    private static final String[] ALL_COLUMNS = new String[] {"id", "name", "iban", "bic"};

    private Context mContext;
    private SQLiteDatabase db;

    private SQLiteStatement mPersonExistSt;
    private SQLiteStatement mIbanExistSt;

    public PeopleDatabaseHelper(@NonNull Context context) {
        mContext = context;
        db = VelkojaDatabaseHelper.getInstance(context).getWritableDatabase();

        mPersonExistSt = db.compileStatement("SELECT COUNT(id) FROM people WHERE LOWER(name)=LOWER(?);");
        mIbanExistSt = db.compileStatement("SELECT COUNT(id) FROM people WHERE iban=?;");
    }

    /**
     * Checks whether the database contains a person named 'name'.
     * Ignores case.
     *
     * @param name name to look for
     * @return true if person named like that exists
     * @throws SQLException
     */
    public boolean personExists(@NonNull String name) throws SQLException {
        mPersonExistSt.bindString(1, name);
        return (mPersonExistSt.simpleQueryForLong() == 1);
    }

    /**
     * Checks whether the database contains a person with given IBAN.
     *
     * @param iban iban to look for
     * @return true if person with given IBAN exists
     * @throws SQLException
     */
    public boolean ibanExists(@NonNull String iban) throws SQLException {
        mIbanExistSt.bindString(1, iban);
        return (mIbanExistSt.simpleQueryForLong() == 1);
    }

    /**
     * Inserts the given person into the database.
     *
     * @param name name of the person
     * @param iban iban if the person
     * @param bic bic of the given iban
     * @return id of the inserted person
     * @throws SQLException
     */
    public long insert(@NonNull String name, @NonNull String iban, @NonNull String bic) throws SQLException {
        SQLiteStatement st = db.compileStatement("INSERT INTO people(name, iban, bic) VALUES(?, ?, ?);");
        st.bindString(1, name);
        st.bindString(2, iban);
        st.bindString(3, bic);

        return st.executeInsert();
    }

    /**
     * Fetches all personal information of the given person.
     *
     * @param id id of the person to fetch
     * @return person or NULL if the given id is invalid
     */
    @Nullable
    public Person findPerson(@NonNull long id) {
        Cursor c = db.query(PEOPLE_TABLE, ALL_COLUMNS, "id=?", new String[] {Long.toString(id)}, null, null, null);

        if (c.getCount() == 0) {
            return null;
        }

        return new Person(c.getLong(0), c.getString(1), c.getString(2), c.getString(3));
    }

    /**
     * Find all people whose name, IBAN or BIC contain the given string as substring.
     *
     * @param text what to look for
     * @return cursor over found people
     */
    public Cursor query(@NonNull String text) {
        String likyfied = "%" + text + "%";
        return db.query(PEOPLE_TABLE, ALL_COLUMNS,
                        "name LIKE ? OR iban LIKE ? OR bic LIKE ?", new String[] {likyfied, likyfied, likyfied},
                        null, null, "name");
    }

    /**
     * Get a PeopleAdapter with a initial cursor over all people in the db.
     *
     * @return
     */
    public PeopleAdapter getAdapter() {
        Cursor c = db.query(PEOPLE_TABLE, ALL_COLUMNS, null, null, null, null, "name");
        return new PeopleAdapter(mContext, c);
    }
}
