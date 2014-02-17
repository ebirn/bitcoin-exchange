package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.currency.Currency;

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
public class MtGoxWallets {

    @XmlElement(name = "EUR")
    private MtGoxWallet eur;

    @XmlElement(name="BTC")
    private MtGoxWallet btc;

    @XmlElement(name="USD")
    private MtGoxWallet usd;

    public MtGoxWallet getBTC() {
        return btc;
    }

    public MtGoxWallet getEUR() {
        return eur;
    }

    public MtGoxWallet getUSD() {
        return usd;
    }

    public Currency[] getCurrencies() {
        return new Currency[] {Currency.USD, Currency.EUR, Currency.BTC};
    }

    public MtGoxWallet getWallet(Currency curr) {

        MtGoxWallet wallet = null;
        switch (curr) {
            case USD:
                wallet = getUSD();
                break;


            case EUR:
                wallet = getEUR();
                break;


            case BTC:
                wallet = getBTC();
                break;

            default:
        }



        return wallet;
    }

    private void setWalletCurrency(MtGoxWallet wallet, Currency curr) {

    }
}
