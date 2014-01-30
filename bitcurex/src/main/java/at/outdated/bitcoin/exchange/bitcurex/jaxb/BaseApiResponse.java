package at.outdated.bitcoin.exchange.bitcurex.jaxb;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by ebirn on 30.01.14.
 */
public class BaseApiResponse {


    @XmlElement
    String error;

    public String getError() {
        return error;
    }

    public boolean isError() {
        return error != null && error.isEmpty() == false;
    }
}
