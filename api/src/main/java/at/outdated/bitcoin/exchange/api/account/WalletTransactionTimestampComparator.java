package at.outdated.bitcoin.exchange.api.account;

import java.util.Comparator;

/**
 * Created by ebirn on 25.01.14.
 */
public class WalletTransactionTimestampComparator implements Comparator<WalletTransaction> {

    @Override
    public int compare(WalletTransaction t1, WalletTransaction t2) {
        return t1.getTimestamp().compareTo(t2.getTimestamp());
    }
}
