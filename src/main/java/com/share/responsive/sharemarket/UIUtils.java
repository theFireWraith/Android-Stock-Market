package com.share.responsive.sharemarket;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Binoy on 11/20/2017.
 */

public class UIUtils {
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }
    public static void initialiseSharedPreference(Context context){
        pref = context.getSharedPreferences("FavoritesPref", Context.MODE_PRIVATE); // 0 - for private mode
        editor = pref.edit();
    }
    public static void addFavToSharedPreference(String key,String value){
        editor.putString(key, value);
        editor.commit();
    }
    public static void removeFavToSharedPreference(String key){
        editor.remove(key);
        editor.commit();
    }
    public static String getFavFromSharedPreference(String key){
       return pref.getString(key,null);

    }
    public static ArrayList<FavoritesInfo> getAllItemsfromSharedPreference(){
        ArrayList<FavoritesInfo> favoritesInfos=new ArrayList<>();
        Map<String, ?> allEntries = pref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
           // Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            Gson gson = new Gson();
            favoritesInfos.add(gson.fromJson(getFavFromSharedPreference(entry.getKey()), FavoritesInfo.class));
        }
        return favoritesInfos;
    }
    public static int getSharedPreferenceSize(){
        return pref.getAll().size();
    }
}
