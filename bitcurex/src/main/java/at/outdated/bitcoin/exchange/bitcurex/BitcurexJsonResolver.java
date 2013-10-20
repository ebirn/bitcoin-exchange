package at.outdated.bitcoin.exchange.bitcurex;

import at.outdated.bitcoin.exchange.api.jaxb.JSONResolver;
import at.outdated.bitcoin.exchange.bitcurex.jaxb.BitcurexTickerValue;

import javax.ws.rs.ext.Provider;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */


@Provider
public class BitcurexJsonResolver extends JSONResolver {


    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[] { BitcurexTickerValue.class };  //To change body of implemented methods use File | Settings | File Templates.
    }
}

