package at.outdated.bitcoin.exchange.mtgox;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ebirn on 14.01.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MtGoxOrderList {

    @XmlElement
    String result;

    @XmlElement
    List<MtGoxOrder> data = new ArrayList<>();

    public String getResult() {
        return result;
    }

    public List<MtGoxOrder> getData() {
        return data;
    }
}
