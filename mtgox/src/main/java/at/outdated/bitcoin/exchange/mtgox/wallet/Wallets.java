package at.outdated.bitcoin.exchange.mtgox.wallet;

import at.outdated.bitcoin.exchange.api.account.Wallet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 04.05.13
 * Time: 19:32
 * I only do this because I am to lazy to write a real JAXB/Map adapter for this
 */

//TODO: replace this class with a real JAXB adapter
@XmlAccessorType(XmlAccessType.FIELD)
public class Wallets {

    @XmlElement(name = "EUR")
    private Wallet eur;

    @XmlElement(name="BTC")
    private Wallet btc;

    public Wallet getBTC() {
        return btc;
    }

    public Wallet getEUR() {
        return eur;
    }
}
