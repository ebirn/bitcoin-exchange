package at.outdated.bitcoin.exchange.mtgox;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by ebirn on 14.01.14.
 */
public class OrderDeletion {


    @XmlElement
    String oid;

    @XmlElement
    String qid;


    public String getOid() {
        return oid;
    }

    public String getQid() {
        return qid;
    }
}
