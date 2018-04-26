package com.share.responsive.sharemarket;

/**
 * Created by Binoy on 11/20/2017.
 */

public class StockTable {
    public String stockSymbol;
    public String lastPrice;
    public String change;
    public String changeperc;
    public String timestamp;
    public String open;
    public String close;
    public String dayRange;
    public String volume;

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getChangeperc() {
        return changeperc;
    }

    public void setChangeperc(String changeperc) {
        this.changeperc = changeperc;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getDayRange() {
        return dayRange;
    }

    public void setDayRange(String dayRange) {
        this.dayRange = dayRange;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
