package at.outdated.bitcoin.exchange.mtgox;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 03.05.13
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LagResponse {

    @XmlElement(name="lag")
    private long lag;

    @XmlElement(name = "lag_text")
    private String text;

    @XmlElement(name ="lag_secs")
    private double seconds;

    @XmlElement
    private String length;

    public long getLag() {
        return lag;
    }

    public String getText() {
        return text;
    }

    public double getSeconds() {
        return seconds;
    }

    public String getLength() {
        return length;
    }
}
