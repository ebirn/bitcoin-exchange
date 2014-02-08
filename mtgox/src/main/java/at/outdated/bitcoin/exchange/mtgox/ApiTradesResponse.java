package at.outdated.bitcoin.exchange.mtgox;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by ebirn on 08.02.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ApiTradesResponse extends ApiResponse {


    List<MtGoxTrade> data;

    public List<MtGoxTrade> getData() {
        return data;
    }

    public List<MtGoxTrade> getTrades() {
        return data;
    }
}
