package at.outdated.bitcoin.exchange.coinse.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 30.01.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DepositAddress extends BaseResponse {
/*
    {
        "status": true,
            "message": "success",
            "deposit_address": "1Q1RaxqZipDgaUg4r7KYqgoftZGhba1CyV",
            "systime": 1372852975
    }
    */

    @XmlElement
    String deposit_address;

    public String getDepositAddress() {
        return deposit_address;
    }
}
