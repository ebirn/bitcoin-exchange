package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.market.MarketDepth;

import javax.xml.bind.annotation.*;

/**
 * Created by ebirn on 22.09.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BtceMarketDepth {

    @XmlList
    Object[][] asks;

    @XmlList
    Object[][] bids;

    public Object[][] getAsks() {
        return asks;
    }

    public Object[][] getBids() {
        return bids;
    }

}
