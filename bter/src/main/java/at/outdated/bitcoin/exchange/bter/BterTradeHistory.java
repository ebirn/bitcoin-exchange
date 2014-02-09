package at.outdated.bitcoin.exchange.bter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by ebirn on 09.02.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BterTradeHistory extends BterResult {

    List<BterTrade> data;

    public List<BterTrade> getData() {
        return data;
    }
}
