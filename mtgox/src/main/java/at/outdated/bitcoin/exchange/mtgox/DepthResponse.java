package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeMicroDateAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ebirn on 03.10.13.
 */
public class DepthResponse {


    @XmlElement
    @XmlJavaTypeAdapter(UnixTimeMicroDateAdapter.class)
    Date now;

    @XmlElement
    @XmlJavaTypeAdapter(UnixTimeMicroDateAdapter.class)
    Date cached;

    @XmlElement
    List<DepthEntry> asks = new ArrayList<>();

    @XmlElement
    List<DepthEntry> bids = new ArrayList<>();


    public Date getNow() {
        return now;
    }

    public Date getCached() {
        return cached;
    }

    public List<DepthEntry> getAsks() {
        return asks;
    }

    public List<DepthEntry> getBids() {
        return bids;
    }



}
