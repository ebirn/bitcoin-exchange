package at.outdated.bitcoin.exchange.api.currency;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 02.05.13
 * Time: 20:15
 * To change this template use File | Settings | File Templates.
 */
@XmlEnum
public enum Currency {
    BTC(100000000, 0.00000001),
    EUR(100000, 0.00001),
    USD(100000, 0.00001),
    JPY(1000, 0.001);

    private int divide = 1;
    private double multiply = 1.0;

    Currency(int divide, double multiply) {
        this.divide = divide;
        this.multiply = multiply;
    }

    public int getDivide() {
        return divide;
    }

    public double getMultiply() {
        return multiply;
    }



}
