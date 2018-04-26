package com.share.responsive.sharemarket;

import android.view.View;
import android.webkit.JavascriptInterface;

/**
 * Created by Binoy on 11/18/2017.
 */

public class WebViewMethods {
    private String symbol;
    private String stockdata;
    @JavascriptInterface
    public String getStockdata() {
        return stockdata;
    }
    @JavascriptInterface
    public void setStockdata(String stockdata) {
        this.stockdata = stockdata;
    }


    @JavascriptInterface
    public String getSymbol() {
        return symbol;
    }
    @JavascriptInterface
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    @JavascriptInterface
    public void hidePgb() {
        Tab1Fragment.pgbCharts.setVisibility(View.INVISIBLE);
    }
}
