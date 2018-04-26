package com.share.responsive.sharemarket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * Created by Binoy on 11/20/2017.
 */

public class Tab3Fragment extends Fragment {
    private static final String TAG = "Tab3Fragment";
    private static final String NEWS_URL = "http://sharemarkethw-env.us-east-1.elasticbeanstalk.com/seekingnews?symbol=";
    ListView newsList;
    String symbol;
    JSONObject newsData;
    ArrayList<NewsFeed> newsfeed;
    TextView errortvtab3;
    ProgressBar pgbNews;
    boolean isNewsDataRequested = false;
    private boolean isErrorResponse=false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!isNewsDataRequested) {
            requestNewsData();
            isNewsDataRequested = true;
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment, container, false);
        symbol = getArguments().getString("symbol");
        newsList = (ListView) view.findViewById(R.id.news);
        errortvtab3=(TextView)view.findViewById(R.id.errortvtab3);
        pgbNews=(ProgressBar)view.findViewById(R.id.pgbNews);

        if(newsfeed!=null){
            initializeNewsList();
            pgbNews.setVisibility(View.INVISIBLE);
        }else if(isErrorResponse){
            pgbNews.setVisibility(View.INVISIBLE);
            errortvtab3.setVisibility(View.VISIBLE);
            errortvtab3.setText("Failed to load data");
        }
        return view;
    }

    private void initializeNewsList() {
        ArrayAdapter<NewsFeed> newsAdapter=new CustomNewsListAdapter();
        newsList.setAdapter(newsAdapter);
       // UIUtils.setListViewHeightBasedOnItems(newsList);


    }

    private void requestNewsData() {
        //News
        if (symbol == null) {
            symbol = getArguments().getString("symbol");
        }
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest newsRequest = new StringRequest(Request.Method.GET, NEWS_URL + symbol,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                            newsData = xmlToJson.toJson();
                            parseJsonNews(newsData);
                            initializeNewsList();
                            pgbNews.setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: News" + error.getMessage());
                        errortvtab3.setVisibility(View.VISIBLE);
                        errortvtab3.setText("Failed to load data");
                        pgbNews.setVisibility(View.INVISIBLE);
                        isErrorResponse=true;
                        // Toast.makeText(getActivity(), "Server error. Please try again later.", Toast.LENGTH_SHORT).show();

                    }
                }
        );
        queue.add(newsRequest);
    }
    private String getPSTTime(String pubDate){
        Calendar cal = Calendar.getInstance();
        String[] splitDate = pubDate.split(" ");
        //cal.setTime();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        TimeZone tzz = TimeZone.getTimeZone("UTC");
        format.setTimeZone(tzz);
        Date pstDate;
        String time ="";
        try {
             pstDate = format.parse(splitDate[4]);
             time = pstDate.toString().split(" ")[3] + " " + pstDate.toString().split(" ")[4];
        } catch (ParseException e) {
            e.printStackTrace();
            return splitDate[0]+" "+splitDate[1]+" "+splitDate[2]+" "+splitDate[3]+" "+splitDate[4]+" GMT";
        }
        return splitDate[0]+" "+splitDate[1]+" "+splitDate[2]+" "+splitDate[3]+" "+ time;
    }

    private void parseJsonNews(JSONObject newsData) {
        try {
            newsfeed = new ArrayList<NewsFeed>();
            JSONArray newsContent = newsData.getJSONObject("rss").getJSONObject("channel").getJSONArray("item");
            int i=0,j=0;
            while(j<5){
                if(newsContent.getJSONObject(i).has("title")) {
                    if (newsContent.getJSONObject(i).getString("link").contains("https://seekingalpha.com/article/")) {
                        newsfeed.add(new NewsFeed(newsContent.getJSONObject(i).getString("title"),
                                newsContent.getJSONObject(i).getString("sa:author_name"),
                                newsContent.getJSONObject(i).getString("pubDate"),
                                newsContent.getJSONObject(i).getString("link")));
                        j++;
                    }
                }else{
                    break;
                }
                i++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public Activity getCurrentActivity(){
        return getActivity();
    }
    private class CustomNewsListAdapter extends ArrayAdapter<NewsFeed> {
        public CustomNewsListAdapter() {
            super(getCurrentActivity(), R.layout.newslist, newsfeed);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView=convertView;
            if(itemView==null){
                itemView=getLayoutInflater().inflate(R.layout.newslist,parent,false);
            }
            final NewsFeed currNewsFeed = newsfeed.get(position);
            TextView title=(TextView)itemView.findViewById(R.id.newstitle);
            title.setText(currNewsFeed.getTitle());
            TextView author=(TextView)itemView.findViewById(R.id.newsauthor);
            author.setText("Author: "+currNewsFeed.getAuthor());
            TextView pubdate=(TextView)itemView.findViewById(R.id.newsdate);
            pubdate.setText("Date: "+getPSTTime(currNewsFeed.getPubDate()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currNewsFeed.getLink()));
                    startActivity(browserIntent);
                }
            });
            return itemView;
        }
    }

}
