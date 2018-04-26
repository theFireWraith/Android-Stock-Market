package com.share.responsive.sharemarket;

/**
 * Created by Binoy on 11/19/2017.
 */

public class StockData {
    private String parameter;
    private String value;
    private int arrow;

    public StockData(String parameter, String value, int arrow) {
        this.parameter = parameter;
        this.value = value;
        this.arrow = arrow;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getArrow() {
        return arrow;
    }

    public void setArrow(int arrow) {
        this.arrow = arrow;
    }
}
