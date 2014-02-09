package at.outdated.bitcoin.exchange.bitkonan;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.jaxb.DateIso8601Adapter;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ebirn on 08.02.14.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BitkonanOrder {
    // {"total":111.75,"btc":0.15,"usd":745,"time":"2014-02-08T21:07:48.000Z","tradetype":1}

    BigDecimal total;

    BigDecimal btc;

    BigDecimal usd;

    @XmlJavaTypeAdapter(DateIso8601Adapter.class)
    Date time;


    int tradetype;

    public MarketOrder getOrder(Market m, AssetPair asset) {

        MarketOrder order = new MarketOrder(new OrderId(m, Long.toString(time.getTime())));

        order.setVolume(new CurrencyValue(btc, asset.getBase()));
        order.setPrice(new CurrencyValue(usd, asset.getQuote()));

        return order;
    }
}
