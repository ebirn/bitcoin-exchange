package at.outdated.bitcoin.exchange.bitcurex.jaxb;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Created by ebirn on 20.10.13.
 */
@XmlEnum
public enum BitcurexTransactionType {

    /*
    <option value="">--- select type ---</option>
    <option value="1" selected="selected">BTC Deposit</option>
    <option value="2">BTC Withdrawal</option>
    <option value="3">EUR € Deposit</option>
    <option value="4">EUR € Withdrawal</option>
    <option value="5">Sell offers</option>
    <option value="6">Buy offers</option>
    <option value="7">BTC Transaction</option>
    <option value="8">EUR € Transaction</option>
     */

    NONE,
    BTC_DEPOST,
    BTC_WITHDRAWAL,
    EUR_DEPOSIT,
    EUR_WITHDRAWAL,
    SELL_OFFERS,
    BUY_OFFERS,
    BTC_TRANSACTION,
    EUR_TRANSACTION

}
