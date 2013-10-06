package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeDateAdapter;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 23:44
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BtcEAccountInfo extends AccountInfo {


    @XmlElement(name = "server_time")
    @XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    Date serverTime;

    @XmlElement
    BtceFunds funds;

    @Override
    public String getLogin() {
        return "btce";  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public CurrencyValue getTradeFee(CurrencyValue volume, TradeDecision trade) {

        SimplePercentageFee fee = new SimplePercentageFee(0.002);
        return fee.calculate(trade, volume);
    }
}
