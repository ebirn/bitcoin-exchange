package at.outdated.bitcoin.exchange.api.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
public class UnixTimeDateAdapter extends XmlAdapter<Number,Date> {

    @Override
    public Date unmarshal(Number v) throws Exception {
        return new Date(v.longValue()*1000L);
    }

    @Override
    public Number marshal(Date v) throws Exception {
        return v.getTime()/1000L;
    }
}
