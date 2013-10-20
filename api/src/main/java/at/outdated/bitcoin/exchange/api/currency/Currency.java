package at.outdated.bitcoin.exchange.api.currency;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 02.05.13
 * Time: 20:15
 * To change this template use File | Settings | File Templates.
 */
@XmlEnum
public enum Currency {
    BTC, // Bitcoin
    LTC, // Litecoin
    FTC, // Feathercoin
    NMC, // Namecoin
    XRP, // Ripple
    NVC, // Novacoin


    EUR(false), // Euro
    USD(false), // US Dollars
    JPY(false), // Japanese Yen
    CNY(false), // Chinese
    PLN(false) //Polish Zloty
;

    private Currency() {

    }

    private Currency(boolean crypto) {
        this.crypto = crypto;
    }

    private boolean crypto = true;

    public boolean isCrypto() {
        return crypto;
    }
}
