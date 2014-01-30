package at.outdated.bitcoin.exchange.coinse.jaxb;

import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeDateAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * Created by ebirn on 30.01.14.
 */
public class BaseResponse {

    @XmlElement
    boolean status;

    @XmlElement
    String message;

    @XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    @XmlElement
    Date systime;

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public Date getSystime() {
        return systime;
    }

    public boolean isSuccess() {
        return status == true;
    }

    public boolean isFailure() {
        return status == false;
    }
}
