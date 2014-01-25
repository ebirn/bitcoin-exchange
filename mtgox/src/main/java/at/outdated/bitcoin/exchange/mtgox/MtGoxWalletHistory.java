package at.outdated.bitcoin.exchange.mtgox;



import at.outdated.bitcoin.exchange.api.account.WalletTransaction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.05.13
 * Time: 20:18
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class MtGoxWalletHistory {

    private int records;

    private int current_page;

    private int max_page;

    private int max_results;

    @XmlElement(name="result")
    private List<MtGoxWalletTransaction> transactions = new ArrayList<>();


    public int getRecords() {
        return records;
    }

    public int getCurrentPage() {
        return current_page;
    }

    public int getMaxPage() {
        return max_page;
    }

    public int getMaxResults() {
        return max_results;
    }

    public List<MtGoxWalletTransaction> getTransactions() {
        Collections.sort(transactions);
        return transactions;

    }
}
