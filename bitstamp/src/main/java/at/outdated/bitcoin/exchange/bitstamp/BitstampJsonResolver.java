package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.jaxb.JSONResolver;

import javax.ws.rs.ext.Provider;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */


@Provider
public class BitstampJsonResolver extends JSONResolver {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{ BitstampTickerValue.class, BitstampAccountBalance.class, BitstampAccountInfo.class, BitstampWallet.class };  //To change body of implemented methods use File | Settings | File Templates.
    }

}

