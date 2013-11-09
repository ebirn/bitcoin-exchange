package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 23.05.13
 * Time: 20:44
 * To change this template use File | Settings | File Templates.
 */
public class TradeTransaction {


    TradeDecision decision;

    TickerValue ticker;

    CurrencyValue volume;

    Market market;

    Date timestamp;

    public TradeTransaction() {
        timestamp = new Date();
    }

    public TradeTransaction(TradeDecision decision) {
        timestamp = new Date();
        this.decision = decision;

    }




    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public TradeDecision getDecision() {
        return decision;
    }

    public void setDecision(TradeDecision decision) {
        this.decision = decision;
    }

    public TickerValue getTicker() {
        return ticker;
    }

    public void setTicker(TickerValue ticker) {
        this.ticker = ticker;
    }

    public CurrencyValue getVolume() {
        return volume;
    }

    public void setVolume(CurrencyValue amount) {
        this.volume = amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
