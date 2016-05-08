package com.example.mydictionary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class FavouriteActivity extends AppCompatActivity {

    ListView list ;
    ArrayAdapter<String> adapter ;
    ArrayList<String> arr ;
    SharedPreferences pref ;

    public void getArrFromPrefernce(){

        String str = pref.getString("favourite","");
        arr = new ArrayList<String>(Arrays.asList(str.split(",")));
    }
    public void saveArrToPrefernce(){
        if(arr != null ){

            StringBuilder sbHistory = new StringBuilder();
            for(String item : arr){
                sbHistory.append(item);
                sbHistory.append(",");
            }
            String strHistory = null ;
            if(arr.size()> 0)
                strHistory = sbHistory.substring(0,sbHistory.length()-1);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("favourite", strHistory);
            edit.commit();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        getArrFromPrefernce();
        list = (ListView)findViewById(R.id.listFavourite);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arr);
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int temp  = position ;
                new AlertDialog.Builder(FavouriteActivity.this)
                        .setTitle("Delete")
                        .setMessage("Do you really want to remove : " + arr.get(temp) +"")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                arr.remove(temp);
                                adapter.notifyDataSetChanged();
                                try {
                                    saveArrToPrefernce();
                                } catch (Exception e) {
                                    Toast.makeText(getApplication(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true ;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history_view, menu);
        MenuItem itemSearch = menu.findItem(R.id.action_search);
        MenuItem itemClear = menu.findItem(R.id.action_delete);
        /*mSearchView = (SearchView) itemSearch.getActionView();
        mSearchView.requestFocus();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {

                word = text;
                im.hideSoftInputFromWindow(mDrawerLayout.getWindowToken(), 0);
                showContent();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                word = text;
                showWordlist();

                return false;


            }
        });*/
        return true;
    }

}
