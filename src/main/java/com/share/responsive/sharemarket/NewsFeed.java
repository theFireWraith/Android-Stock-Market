package com.share.responsive.sharemarket;

/**
 * Created by Binoy on 11/21/2017.
 */

public class NewsFeed {
    String title;
    String author;
    String pubDate;
    String link;

    public NewsFeed(String title, String author, String pubDate, String link) {
        this.title = title;
        this.author = author;
        this.pubDate = pubDate;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
