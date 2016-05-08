package com.example.mydictionary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.util.List;
import android.os.Handler;


/**
 * Created by huynhpro on 4/30/2016.
 */
public class SearchActivity extends ActionBarActivity {

    //private EditText searchInput;
    private ListView videosFound;
    private SearchView searchVideo;
    private FloatingActionButton fab;
    private Handler handler;
    private ProgressDialog progress ;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //searchInput = (EditText)findViewById(R.id.search_input);
        videosFound = (ListView) findViewById(R.id.videos_found);
        fab = (FloatingActionButton) findViewById(R.id.search_video);
        addClickListener();
        handler = new Handler();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }



    private List<VideoItem> searchResults;

    private void searchOnYoutube(final String keywords) {
        new Thread() {
            public void run() {

                YoutubeConnector yc = new YoutubeConnector(SearchActivity.this);
                searchResults = yc.search(keywords);
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            updateVideosFound();
                        } catch (Exception e) {
                            Log.d("ErrorError", e.getMessage());
                        }finally {
                            if(progress != null){
                              progress.dismiss();
                            }
                        }
                    }
                });
            }
        }.start();
    }


    private void updateVideosFound() {
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.video_item, searchResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.video_item, parent, false);
                }
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);

                VideoItem searchResult = searchResults.get(position);

                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        videosFound.setAdapter(adapter);
    }

    private void addClickListener() {
        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
                startActivity(intent);
            }

        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_youtube, menu);
        MenuItem itemSearch = menu.findItem(R.id.searchyoutube);
        searchVideo = (SearchView) itemSearch.getActionView();
        searchVideo.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try{
                    if( query != null ){
                        progress = ProgressDialog.show(SearchActivity.this, "Vui lòng đợi giây lát..", "Đang tải dữ liệu tìm kiếm", true,false);
                        searchOnYoutube(query);

                    }

                }catch (Exception e){
                    e.printStackTrace();

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Search Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.mydictionary/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Search Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.mydictionary/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}