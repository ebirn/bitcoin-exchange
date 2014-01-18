package at.outdated.bitcoin.exchange.icbit;

import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.market.Market;

/**
 * Created by ebirn on 31.10.13.
 */
public class IcbitMarket  extends Market {

    public IcbitMarket() {
        super("icbit", "https://icbit.se", "icbit.se");
    }

    @Override
    public ExchangeClient createClient() {
        return new IcbitClient(this);
    }
}
