package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.jaxb.JSONResolver;

import javax.ws.rs.ext.Provider;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 02.05.13
 * Time: 20:32
 * To change this template use File | Settings | File Templates.
 */

@Provider
public class MtGoxJSONResolver extends JSONResolver {


    //@Override
    protected Class<?>[] getTypes() {
        return new Class<?>[] { ApiTickerResponse.class, ApiLagResponse.class, ApiAccountInfo.class, ApiWalletHistory.class };
    }
}
