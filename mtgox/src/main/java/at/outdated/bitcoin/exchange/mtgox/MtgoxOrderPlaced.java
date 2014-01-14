package at.outdated.bitcoin.exchange.mtgox;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 14.01.14.
 */
@XmlRootElement
public class MtgoxOrderPlaced {

    @XmlElement
    String result;

    @XmlElement
    String data;


    public String getResult() {
        return result;
    }

    public String getData() {
        return data;
    }
}
