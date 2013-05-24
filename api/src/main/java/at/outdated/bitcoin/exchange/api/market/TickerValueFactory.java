package at.outdated.bitcoin.exchange.api.market;

import at.outdated.mtgox.TickerResponse;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 07.05.13
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public class TickerValueFactory {


    public static TickerValue convert(TickerResponse ticker) {


        TickerValue value = new TickerValue();

        value.setTimestamp(ticker.getTimestamp());

        value.setLast(ticker.getLast().getValue());

        value.setBuy(ticker.getBuy().getValue());
        value.setSell(ticker.getSell().getValue());

        value.setLow(ticker.getLow().getValue());
        value.setAvg(ticker.getAvg().getValue());
        value.setHigh(ticker.getHigh().getValue());

        value.setVolume(ticker.getVol().getValue());

        value.setVolumeWeightedAvg(ticker.getVwap().getValue());
        value.setCurrency(ticker.getInCurrency());


        return value;

    }
}
