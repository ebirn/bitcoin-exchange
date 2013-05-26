package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.currency.Currency;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
public class BitstampWallet extends Wallet {


    public BitstampWallet() {

    }

    public BitstampWallet(Currency curr) {
        this.currency = curr;
    }
}
