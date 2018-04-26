package com.share.responsive.sharemarket;

/**
 * Created by Binoy on 11/21/2017.
 */

public class NewsDataReceivedEvent {
    String xmlNews;

    public String getXmlNews() {
        return xmlNews;
    }

    public void setXmlNews(String xmlNews) {
        this.xmlNews = xmlNews;
    }

    public NewsDataReceivedEvent(String xmlNews) {
        this.xmlNews = xmlNews;
    }
}
