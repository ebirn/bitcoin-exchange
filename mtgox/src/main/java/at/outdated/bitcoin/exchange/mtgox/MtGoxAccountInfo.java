package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 03.05.13
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class MtGoxAccountInfo extends AccountInfo {

    //
    // "data":{
    // "Login":"ebirn",
    // "Index":"437336",
    // "Id":"07bcf275-06f5-485b-bae9-c4022aab5e19",
    // "Rights":["get_info"],
    // "Language":"en_US",
    // "Created":"2013-04-12 08:45:29",
    // "Last_Login":"2013-05-03 08:45:10",
    // "Wallets":{
    //      "BTC":{"Balance":{"value":"3.12006660","value_int":"312006660","display":"3.12006660\u00a0BTC","display_short":"3.12\u00a0BTC","currency":"BTC"},"Operations":4,"Daily_Withdraw_Limit":{"value":"100.00000000","value_int":"10000000000","display":"100.00000000\u00a0BTC","display_short":"100.00\u00a0BTC","currency":"BTC"},"Monthly_Withdraw_Limit":null,"Max_Withdraw":{"value":"100.00000000","value_int":"10000000000","display":"100.00000000\u00a0BTC","display_short":"100.00\u00a0BTC","currency":"BTC"},"Open_Orders":{"value":"0.00000000","value_int":"0","display":"0.00000000\u00a0BTC","display_short":"0.00\u00a0BTC","currency":"BTC"}},
    //      "EUR":{"Balance":{"value":"0.79880","value_int":"79880","display":"0.79880\u00a0\u20ac","display_short":"0.80\u00a0\u20ac","currency":"EUR"},"Operations":3,"Daily_Withdraw_Limit":{"value":"1000.00000","value_int":"100000000","display":"1,000.00000\u00a0\u20ac","display_short":"1,000.00\u00a0\u20ac","currency":"EUR"},"Monthly_Withdraw_Limit":{"value":"10000.00000","value_int":"1000000000","display":"10,000.00000\u00a0\u20ac","display_short":"10,000.00\u00a0\u20ac","currency":"EUR"},"Max_Withdraw":{"value":"1000.00000","value_int":"100000000","display":"1,000.00000\u00a0\u20ac","display_short":"1,000.00\u00a0\u20ac","currency":"EUR"},
    //
    // "Open_Orders":{"value":"0.00000","value_int":"0","display":"0.00000\u00a0\u20ac","display_short":"0.00\u00a0\u20ac","currency":"EUR"}}},
    // "Monthly_Volume":{"value":"3.13890000","value_int":"313890000","display":"3.13890000\u00a0BTC","display_short":"3.14\u00a0BTC","currency":"BTC"},
    // "Trade_Fee":0.6}}|#]


    @XmlElement(name="Id")
    private String id;


    @XmlElement(name="Login")
    private String login;


    @XmlElement(name="Index")
    private long index;

    @XmlElement(name="Trade_Fee")
    private double tradeFee = Double.MAX_VALUE;

    @XmlElement(name="Last_Login")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date lastLogin;

    @XmlElement(name="Created")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date created;

    @XmlElement(name="Language")
    private String locale;

    @XmlElement(name="Open_Orders")
    private CurrencyValue openOrders;

    @XmlElement(name="Monthly_Volume")
    private CurrencyValue monthlyVolume;

    @XmlElement(name="Wallets")
    private MtGoxWallets mtGoxWallets;


    public String getLogin() {
        return login;
    }

    @Override
    public Fee getTradeFee(TradeDecision trade) {

        return new SimplePercentageFee(tradeFee / 100.0);

    }

    public MtGoxWallets getWallets() {
        return mtGoxWallets;
    }

    public String getId() {
        return id;
    }


    public long getIndex() {
        return index;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public Date getCreated() {
        return created;
    }

    public String getLocale() {
        return locale;
    }

    public CurrencyValue getOpenOrders() {
        return openOrders;
    }

    public CurrencyValue getMonthlyVolume() {
        return monthlyVolume;
    }


    @Override
    public Wallet getWallet(Currency curr) {

        return super.getWallet(curr);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
