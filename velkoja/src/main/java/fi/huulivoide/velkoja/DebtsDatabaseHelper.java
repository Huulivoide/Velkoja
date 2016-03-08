package fi.huulivoide.velkoja;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DebtsDatabaseHelper {
    private static final String DEBTS_TABLE = "debts";
    private static final String[] ALL_COLUMNS = new String[] {"id", "description", "sum", "due", "paid", "person"};

    private SQLiteDatabase db;
    private Context mContext;

    private SQLiteStatement mMarkAsPaidSt;
    private SQLiteStatement mInsertSt;
    private SQLiteStatement mDebtExists;

    public DebtsDatabaseHelper(@NonNull Context context) {
        mContext = context;
        db = VelkojaDatabaseHelper.getInstance(context).getWritableDatabase();

        mMarkAsPaidSt = db.compileStatement("UPDATE debts SET paid=? WHERE id=?;");
        mInsertSt = db.compileStatement("INSERT INTO debts(description, sum, due, paid, person) VALUES(?, ?, ?, ?, ?);");
        mDebtExists = db.compileStatement("SELECT COUNT(id) FROM debts WHERE id=?;");
    }

    /**
     * Returns a readonly lists unpaid and paid debts for the given person.
     * For unexisting persons the list will just be empty.
     *
     * @param person id of the person, whose debts to fetch
     * @return unmodifiable list
     */
    public UnpaidPaidPair debtsFor(long person) {
        Cursor c = db.query(DEBTS_TABLE, ALL_COLUMNS, "person=?",
                            new String[] {Long.toString(person)},
                            null, null, "description");

        ArrayList<Debt> unpaid = new ArrayList<>();
        ArrayList<Debt> paid = new ArrayList<>();

        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            Calendar due = GregorianCalendar.getInstance();
            due.setTimeInMillis(c.getLong(3));

            if (!c.isNull(4)) {
                Calendar payDay = GregorianCalendar.getInstance();
                payDay.setTimeInMillis(c.getLong(4));
                paid.add(new Debt(c.getLong(0), c.getString(1), c.getDouble(2), due, payDay, c.getLong(5)));
            } else {
                unpaid.add(new Debt(c.getLong(0), c.getString(1), c.getDouble(2), due, null, c.getLong(5)));
            }
        }

        c.close();

        return new UnpaidPaidPair(unpaid, paid);
    }

    /**
     * Marks the given debt as paid back. Registers current system time as paytime.
     *
     * @param debt debt to mark as paid.
     * @throws NoSuchDebtException if the specified debt doesn't exist
     */
    public void markAsPaid(long debt) throws NoSuchDebtException {
        mDebtExists.bindLong(1, debt);
        if (mDebtExists.simpleQueryForLong() != 1) {
            throw new NoSuchDebtException(debt);
        }

        mMarkAsPaidSt.bindLong(1, System.currentTimeMillis());
        mMarkAsPaidSt.bindLong(2, debt);
        mMarkAsPaidSt.executeUpdateDelete();
    }

    /**
     * Inserts a new debt to the database.
     * Returns the id of the newly inserted debt if all goes well.
     *
     * @param description a shortish identifier for the debt
     * @param sum amount of money loaned
     * @param due a due date for the loan
     * @param paid when was the loan paid
     * @param person id of the person the money was loaned from
     * @return id of the new debt
     * @throws NoSuchPersonException in case the specified person does not exist
     * @throws SQLException something is horribly wrong
     */
    public long insert(@NonNull String description, double sum, @NonNull Calendar due, Calendar paid, long person)
            throws NoSuchPersonException, SQLException {
        mInsertSt.bindString(1, description);
        mInsertSt.bindDouble(2, sum);
        mInsertSt.bindLong(3, due.getTimeInMillis());
        mInsertSt.bindLong(5, person);

        if (paid != null) {
            mInsertSt.bindLong(4, paid.getTimeInMillis());
        } else {
            mInsertSt.bindNull(4);
        }

        try {
            return mInsertSt.executeInsert();
        } catch (SQLException e) {
            // All other constrains have been met with @NonNull:s. Only constraint that
            // might not have been met is a non-existing personId.
            if (e instanceof SQLiteConstraintException) {
                throw new NoSuchPersonException(person, e);
            } else {
                throw e;
            }
        }
    }

    /**
     * Inserts a new debt to the database.
     * Returns the id of the newly inserted debt if all goes well.
     *
     * @param description a shortish identifier for the debt
     * @param sum amount of money loaned
     * @param due a due date for the loan
     * @param person id of the person the money was loaned from
     * @return id of the new debt
     * @throws NoSuchPersonException in case the specified person does not exist
     * @throws SQLException something is horribly wrong
     */
    public long insert(@NonNull String description, double sum, @NonNull Calendar due, long person) throws NoSuchPersonException, SQLException {
        return insert(description, sum, due, null, person);
    }
}
