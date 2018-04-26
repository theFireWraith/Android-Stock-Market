package com.share.responsive.sharemarket;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String TIME_SERIES_URL = "http://sharemarkethw-env.us-east-1.elasticbeanstalk.com/timeseries?symbol=";
    AutoCompleteTextView actv;
    String selectedSymbol = "";
    String[] country_names;
    ListView favlistview;
    boolean isFavListLoaded = false;
    android.support.v7.widget.SwitchCompat switchCompat;
    ArrayList<FavoritesInfo> favInfoFromPreference;
    ArrayAdapter<FavoritesInfo> favListAdapter;
    ProgressBar pgbRefresh;
    int favoriteAutoRefreshedCount = 0;
    ImageButton singlrefresh;
    Spinner sortby,orderby;
    ArrayAdapter<CharSequence> sortbyAdapter,orderbyAdapter;
    private String sortbyParam = "";
    private String orderByParam="";

    @Override
    protected void onStart() {
        super.onStart();
        if (isFavListLoaded) {
            redrawFavList();
            favoriteAutoRefreshedCount = 0;
            refreshFavList();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialise sharedpreferences
        UIUtils.initialiseSharedPreference(getApplicationContext());

        actv = (AutoCompleteTextView) findViewById(R.id.country);
        actv.setThreshold(1);
        String[] from = {"name"};
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter a = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, from, to, 0);
        a.setStringConversionColumn(1);
        FilterQueryProvider provider = new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint == null) {
                    return null;
                }
                String[] columnNames = {BaseColumns._ID, "name", "description"};
                MatrixCursor c = new MatrixCursor(columnNames);
                try {
                    String urlString = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=" + constraint;
                    URL url = new URL(urlString);
                    InputStream stream = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String jsonStr = reader.readLine();
                    JSONArray json = new JSONArray(jsonStr);
                    for (int i = 0; i < json.length(); i++) {
                        c.newRow().add(i).add(json.getJSONObject(i).getString("Symbol") + " - " + json.getJSONObject(i).getString("Name") + " (" + json.getJSONObject(i).getString("Exchange") + ")");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return c;
            }
        };
        a.setFilterQueryProvider(provider);
        actv.setAdapter(a);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                MatrixCursor matrix = (MatrixCursor) parent.getItemAtPosition(position);
                selectedSymbol = matrix.getString(1);
                selectedSymbol = selectedSymbol.split("-")[0].trim();
            }
        });

        Button getquote = (Button) findViewById(R.id.getquote);
        getquote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    selectedSymbol = actv.getEditableText().toString().split("-")[0].trim();

                if (selectedSymbol != "") {
                  loadDetailsActivity(selectedSymbol);
                } else {
                    Toast.makeText(getApplicationContext(), "Please select a valid symbol", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actv.setText("");
                selectedSymbol = "";
            }
        });

        //Auto Refresh
        switchCompat = (SwitchCompat) findViewById(R.id.compatSwitch);
        final Handler handler = new Handler();
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {

                    final int delay = 5000; //milliseconds

                    handler.postDelayed(new Runnable(){
                        public void run(){
                            favoriteAutoRefreshedCount = 0;
                            refreshFavList();
                            handler.postDelayed(this, delay);
                        }
                    }, delay);

                } else {
                    handler.removeCallbacksAndMessages(null);
                }
            }
        });

        //OrderBy Sortby spinners
        sortby = (Spinner)findViewById(R.id.sortby);
        orderby = (Spinner)findViewById(R.id.orderby);
        sortbyAdapter = ArrayAdapter.createFromResource(MainActivity.this,R.array.sortby,android.R.layout.simple_spinner_item);
        orderbyAdapter = ArrayAdapter.createFromResource(MainActivity.this,R.array.orderby,android.R.layout.simple_spinner_item);
        sortbyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderbyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderby.setEnabled(false);
        sortby.setAdapter(sortbyAdapter);
        orderby.setAdapter(orderbyAdapter);
        sortby.setSelection(0,false);
        orderby.setSelection(0,false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sortby.setTooltipText("checker");
        }
        sortby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                 sortbyParam = adapterView.getItemAtPosition(position).toString();
                 if(sortbyParam.equals("Default")){
                     orderby.setEnabled(false);
                 }else{
                     orderby.setEnabled(true);
                 }
                 redrawFavList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        orderby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                orderByParam = adapterView.getItemAtPosition(position).toString();
                redrawFavList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Manual single refresh
        singlrefresh=(ImageButton)findViewById(R.id.singlerefresh);
        singlrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteAutoRefreshedCount = 0;
                refreshFavList();
            }
        });
        pgbRefresh = (ProgressBar)findViewById(R.id.pgbRefresh);
        //Favorite Listview
        favlistview = (ListView) findViewById(R.id.favoriteslist);


        if (!isFavListLoaded) {
            favInfoFromPreference = UIUtils.getAllItemsfromSharedPreference();
            favListAdapter = new CustomFavListAdapter();
            favlistview.setAdapter(favListAdapter);

            isFavListLoaded = true;
        }
        registerForContextMenu(favlistview);
    }
    private void loadDetailsActivity(String symbol){
        Bundle bundle = new Bundle();
        bundle.putString("symbol", symbol);
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void redrawFavList() {
        favInfoFromPreference = UIUtils.getAllItemsfromSharedPreference();
        sortFavitems(favInfoFromPreference);
        favlistview.invalidateViews();
        favlistview.invalidate();
        favlistview.setAdapter(new CustomFavListAdapter());
    }

    private void sortFavitems(ArrayList<FavoritesInfo> favInfoFromPreference) {
        if(orderByParam.equals("Descending")) {
            FavoritesInfo.setDescending(true);
        }
        switch (sortbyParam){
            case "Default":
                    break;
            case "Symbol":
                Collections.sort(favInfoFromPreference,FavoritesInfo.SymbolComparator);
                break;
            case "Price":
                Collections.sort(favInfoFromPreference,FavoritesInfo.PriceComparator);
                break;
            case "Change":
                Collections.sort(favInfoFromPreference,FavoritesInfo.ChangeComparator);
                break;
        }
    }

    private void refreshFavList() {
        ArrayList<FavoritesInfo> listFavItems = UIUtils.getAllItemsfromSharedPreference();
        for (int i = 0; i < listFavItems.size(); i++) {
            pgbRefresh.setVisibility(View.VISIBLE);
            requestTimeSeriesData(listFavItems.get(i).getSymbol());
        }
    }

    private void requestTimeSeriesData(String symbol) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, TIME_SERIES_URL + symbol, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                FavoritesInfo favoritesInfo = parseAndSetFavItemsFromResponse(response);
                Gson gson = new Gson();
                //Updating the existing values
                UIUtils.addFavToSharedPreference(favoritesInfo.getSymbol(), gson.toJson(favoritesInfo));
                favoriteAutoRefreshedCount++;
                if (favoriteAutoRefreshedCount == UIUtils.getSharedPreferenceSize()) {
                    pgbRefresh.setVisibility(View.GONE);
                    redrawFavList();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error);
                pgbRefresh.setVisibility(View.GONE);
                error.printStackTrace();
                EventBus.getDefault().post(new StockDataReceivedEvent("Server Timeout. Refresh error."));
            }
        });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(getRequest);
    }

    private float roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        return Float.valueOf(twoDForm.format(d));
    }

    private FavoritesInfo parseAndSetFavItemsFromResponse(JSONObject response) {
        FavoritesInfo favoritesInfo = new FavoritesInfo();
        try {
            JSONArray jsonArray = response.getJSONObject("Time Series (Daily)").names();
            JSONObject jsonObject = response.getJSONObject("Time Series (Daily)");
            favoritesInfo.setSymbol(response.getJSONObject("Meta Data").getString("2. Symbol"));
            float lastPrice = roundTwoDecimals(Float.parseFloat(jsonObject.getJSONObject(jsonArray.getString(0)).getString("4. close")));
            favoritesInfo.setPrice(lastPrice + "");
            float change = Float.parseFloat(jsonObject.getJSONObject(jsonArray.getString(0)).getString("4. close")) - Float.parseFloat(jsonObject.getJSONObject(jsonArray.getString(1)).getString("4. close"));
            favoritesInfo.setChange(roundTwoDecimals(change) + "");
            float changeperc = (change / Float.parseFloat(jsonObject.getJSONObject(jsonArray.getString(1)).getString("4. close"))) * 100;
            favoritesInfo.setChangeperc(roundTwoDecimals(changeperc) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return favoritesInfo;
    }

    //Context Menu for delete in favorite list
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.favoriteslist) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Remove from Favorties?");
            menu.add("No");
            menu.add("Yes");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        if (item.toString().equals("Yes")) {
            UIUtils.removeFavToSharedPreference(favInfoFromPreference.get((int) info.id).getSymbol());
            Log.d(TAG, "<<onContextItemSelected: Deleted " + favInfoFromPreference.get((int) info.id).getSymbol() + ">>");
            Toast.makeText(getApplicationContext(), "Deleted " + favInfoFromPreference.get((int) info.id).getSymbol(), Toast.LENGTH_SHORT).show();
            redrawFavList();
        }
        return true;
    }

    private class CustomFavListAdapter extends ArrayAdapter<FavoritesInfo> {
        public CustomFavListAdapter() {
            super(MainActivity.this, R.layout.favlist, favInfoFromPreference);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.favlist, parent, false);
            }
            final FavoritesInfo favinfo = favInfoFromPreference.get(position);
            TextView symbol = (TextView) itemView.findViewById(R.id.favsymbol);
            symbol.setText(favinfo.getSymbol());
            TextView price = (TextView) itemView.findViewById(R.id.favprice);
            price.setText(favinfo.getPrice());
            TextView change = (TextView) itemView.findViewById(R.id.favchange);
            change.setText(favinfo.getChange() + " (" + favinfo.getChangeperc() + "%)");
            if (Float.parseFloat(favinfo.getChange()) < 0) {
                change.setTextColor(Color.RED);
            } else {
                change.setTextColor(Color.GREEN);
            }
            symbol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadDetailsActivity(favinfo.getSymbol());
                }
            });
            return itemView;
        }
    }
}
