package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.jaxb.OrderTypeAdapter;
import at.outdated.bitcoin.exchange.api.jaxb.StringBigDecimalAdapter;
import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeDateAdapter;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.OrderType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ebirn on 08.02.14.
 */

public class MtGoxTrade {


    Currency price_currency;

    Currency item;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal amount;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal price;

    long tid;

    @XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    Date date;

    // "trade_type":"bid","primary":"Y","properties":"limit"

    @XmlElement(name="trade_type")
    @XmlJavaTypeAdapter(OrderTypeAdapter.class)
    OrderType type;

    String primary;

    String properties;




    public MarketOrder getOrder(Market market) {

        MarketOrder order = new MarketOrder(new OrderId(market, Long.toString(tid)));

        order.setTimestamp(date);

        order.setAsset(new AssetPair(item, price_currency));
        order.setPrice(new CurrencyValue(price, price_currency));
        order.setVolume(new CurrencyValue(amount, item));

        order.setType(type);

        return order;
    }
}
