package at.outdated.bitcoin.exchange.kraken.jaxb;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by ebirn on 22.09.13.
 */
@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class KrakenDepthResponse extends KrakenResponse<KrakenDepthValue> {


    public MarketDepth getDepthValue() {
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

    private void addOrders(TradeDecision dec, float[][] raw, List<MarketOrder> orders, Currency base, Currency quote) {
        for(float[] askVal : raw) {
            float volume = askVal[1];
            CurrencyValue price = new CurrencyValue(askVal[0], quote);
            orders.add(new MarketOrder(dec, volume, base, price));
        }
    }
}
