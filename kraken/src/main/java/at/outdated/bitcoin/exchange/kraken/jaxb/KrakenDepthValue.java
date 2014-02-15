package at.outdated.bitcoin.exchange.kraken.jaxb;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.OrderType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ebirn on 22.09.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KrakenDepthValue {

    @XmlElement
    float[][] asks;

    @XmlElement
    float[][] bids;

    public float[][] getAsks() {
        return asks;
    }

    public float[][] getBids() {
        return bids;
    }

    public MarketDepth getValue() {
        MarketDepth depth = new MarketDepth();

        /*
        KrakenDepthValue krakenDepth = result.getXXBTZEUR();

        Currency base = Currency.BTC;
        Currency quote = Currency.EUR;


        depth.setBaseCurrency(base);

        addOrders(TradeDecision.BUY, krakenDepth.asks, depth.getAsks(), base, quote);
        addOrders(TradeDecision.SELL, krakenDepth.bids, depth.getBids(), base, quote);
*/
        return depth;
    }

    private void addOrders(OrderType dec, double[][] raw, List<MarketOrder> orders, Currency base, Currency quote) {
        for(double[] val : raw) {

            CurrencyValue price = new CurrencyValue(new BigDecimal(val[0]), quote);
            CurrencyValue volume = new CurrencyValue(new BigDecimal(val[1]), base);

            orders.add(new MarketOrder(dec, volume, price));
        }
    }
}
