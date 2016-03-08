package fi.huulivoide.velkoja;

import android.support.annotation.NonNull;

import java.util.List;

public class UnpaidPaidPair {
    public List<Debt> unpaid;
    public List<Debt> paid;

    public UnpaidPaidPair(@NonNull List<Debt> unpaid, @NonNull List<Debt> paid) {
        this.unpaid = unpaid;
        this.paid = paid;
    }
}
