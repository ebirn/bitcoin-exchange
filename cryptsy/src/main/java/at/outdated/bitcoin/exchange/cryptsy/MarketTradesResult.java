package at.outdated.bitcoin.exchange.cryptsy;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by ebirn on 09.02.14.
 */
public class MarketTradesResult extends CryptsyResult {
/*
    success - Either a 1 or a 0. 1 Represents sucessful call, 0 Represents unsuccessful
    error - If unsuccessful, this will be the error message
    return - If successful, this will be the data returned
  */

    @XmlElement(name="return")
    List<CryptsyTrade> result;

    public List<CryptsyTrade> getResult() {
        return result;
    }
}
