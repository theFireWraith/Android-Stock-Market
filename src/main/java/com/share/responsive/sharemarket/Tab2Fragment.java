package com.share.responsive.sharemarket;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

/**
 * Created by Binoy on 11/20/2017.
 */

public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";
    WebView highstockview;
    String stockDataRecevied;
    TextView errortv;
    private boolean eventRegistered=false;
    ProgressBar pgbhStock;
    @Override
    public void onStart() {
        super.onStart();
        if(!eventRegistered) {
            EventBus.getDefault().register(this);
            eventRegistered=true;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.tab2_fragment,container,false);
        highstockview=(WebView)view.findViewById(R.id.highstockview);
        pgbhStock = (ProgressBar)view.findViewById(R.id.hstockprogressBar);
        errortv=(TextView)view.findViewById(R.id.errortvtab2);
        return view;
    }
    public void onEvent(StockDataReceivedEvent event) {
        //Toast.makeText(getActivity(),"Data Received from eventbus", Toast.LENGTH_LONG).show();
        if(event.stockData.equalsIgnoreCase("Server Timeout. Try again later.") || event.stockData.equals("{}")){
            pgbhStock.setVisibility(View.GONE);
            errortv.setVisibility(View.VISIBLE);
            errortv.setText("Failed to load data.");
            Log.e(TAG, "onEvent: ");
        }else {
            stockDataRecevied = event.stockData;
            initilaizeHighStock();
        }
    }

    private void initilaizeHighStock() {
        highstockview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "onPageStarted: ");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: ");
                pgbhStock.setVisibility(View.GONE);
                highstockview.evaluateJavascript("javascript:drawhsgraph()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d(TAG, "onReceiveValue from webview: Graph loaded");
                    }
                });
            }
        });
        if(stockDataRecevied!=null) {
            WebViewMethods webviewobj = new WebViewMethods();
            //from bundle
            webviewobj.setSymbol(getArguments().getString("symbol"));
            webviewobj.setStockdata(stockDataRecevied);
            highstockview.addJavascriptInterface(webviewobj, "androidMethod");
        }
        highstockview.getSettings().setJavaScriptEnabled(true);
        highstockview.loadUrl("file:///android_asset/drawgraph.html");
    }
}
