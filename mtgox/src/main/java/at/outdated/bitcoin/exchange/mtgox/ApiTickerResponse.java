package at.outdated.bitcoin.exchange.mtgox;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 02.05.13
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */

@XmlRootElement
public class ApiTickerResponse extends ApiResponse {


    @XmlElement
    protected TickerResponse data;

    public TickerResponse getData() {
        return data;
    }


}
