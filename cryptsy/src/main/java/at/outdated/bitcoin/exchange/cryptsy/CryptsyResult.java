package at.outdated.bitcoin.exchange.cryptsy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 09.02.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CryptsyResult {

    int success;

    String error;

    public int getSuccess() {
        return success;
    }

    public boolean isSuccess() {
        return success == 1;
    }

    public String getError() {
        return error;
    }
}
