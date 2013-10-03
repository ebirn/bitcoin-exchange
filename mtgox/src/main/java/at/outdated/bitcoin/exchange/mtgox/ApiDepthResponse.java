package at.outdated.bitcoin.exchange.mtgox;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by ebirn on 03.10.13.
 */
public class ApiDepthResponse extends ApiResponse {


    @XmlElement
    protected DepthResponse data;



}
