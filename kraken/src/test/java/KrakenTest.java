import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.kraken.*;
import at.outdated.bitcoin.exchange.kraken.jaxb.KrakenResponseResult;
import at.outdated.bitcoin.exchange.kraken.jaxb.KrakenTickerValue;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class KrakenTest {

    KrakenClient client = new KrakenClient();

    @Test
    public void testTickerclient() {


        TickerValue ticker = client.getTicker(Currency.EUR);

        Assert.assertNotNull(ticker.getBid());
        Assert.assertNotNull(ticker.getAsk());
        Assert.assertNotNull(ticker.getCurrency());

        Assert.assertNotNull(ticker.getCurrency());

        System.out.println(ticker);

    }

    @Test
    public void testDepthClient() {


        MarketDepth depth = client.getMarketDepth(Currency.BTC, Currency.EUR);

        Assert.assertNotNull(depth);
        Assert.assertNotNull(depth.getBaseCurrency());

        Assert.assertFalse(depth.getAsks().isEmpty());
        Assert.assertFalse(depth.getBids().isEmpty());
    }


    /*
    @Test
    public void serializeTest() throws Exception {

        String jsonString = "{\"error\":[],\"result\":{\"XXBTZEUR\":{\"a\":[\"92.50000\",\"4\"],\"b\":[\"90.25000\",\"1\"],\"c\":[\"92.50000\",\"0.50000000\"],\"v\":[\"0.50000000\",\"0.50000000\"],\"p\":[\"92.50000\",\"92.50000\"],\"t\":[1,1],\"l\":[\"92.50000\",\"92.50000\"],\"h\":[\"93.00000\",\"92.50000\"],\"o\":\"93.00000\"}}}";


        MOXyJsonProvider jsonProvider = new MOXyJsonProvider();
        KrakenTickerResponse response  = null;


        InputStream is = new ByteArrayInputStream(jsonString.getBytes());
        JAXBElement<KrakenTickerResponse> result = (JAXBElement<KrakenTickerResponse>)  jsonProvider.readFrom(Object.class, KrakenTickerResponse.class, null, MediaType.APPLICATION_JSON_TYPE, null, is);
        response = result.getValue();
        System.out.println("parsed ticker: " + response.getValue());


        response = new KrakenTickerResponse();

        KrakenResponseResult value = new KrakenResponseResult();

        KrakenTickerValue tickerValue = new KrakenTickerValue();

        tickerValue.setA(new String[]{"1", "2", "3"});
        tickerValue.setB(new String[]{"a", "b", "c"});

        value.setXXBTZEUR(tickerValue);

        //response.setResult(value);
        //response.setError(new String[]{"e1", "e2"});



        jsonProvider.writeTo(response, KrakenTickerResponse.class, KrakenTickerResponse.class, null, MediaType.APPLICATION_JSON_TYPE, null, System.out);
    }
    */
}
