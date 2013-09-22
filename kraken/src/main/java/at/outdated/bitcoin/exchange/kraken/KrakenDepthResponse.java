package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by ebirn on 22.09.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KrakenDepthResponse extends KrakenResponse<KrakenDepthValue> {


    public MarketDepth getDepthValue() {

        KrakenDepthValue krakenDepth = result.getXXBTZEUR();

        Currency base = Currency.BTC;
        Currency quote = Currency.EUR;

        MarketDepth depth = new MarketDepth();
        depth.setBaseCurrency(base);

        addOrders(krakenDepth.asks, depth.getAsks(), base, quote);
        addOrders(krakenDepth.bids, depth.getBids(), base, quote);

        return depth;
    }

    private void addOrders(float[][] raw, List<MarketOrder> orders, Currency base, Currency quote) {
        for(float[] askVal : raw) {
            float volume = askVal[1];
            CurrencyValue price = new CurrencyValue(askVal[0], quote);
            orders.add(new MarketOrder(volume, base, price));
        }
    }
}
