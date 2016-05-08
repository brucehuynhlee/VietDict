package com.example.mydictionary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by huynhpro on 5/5/2016.
 */
public class PlayList extends ActionBarActivity {

    ListView lv;
    private ProgressDialog progress ;
    Handler handler = new Handler();


    //phai lay hinh trong doinbackground, neu cu lay duong dan va
    //den getView moi lay thi lai bi loi truy xuat network trong mainthread
    ArrayList<Bitmap> manghinh = new ArrayList<Bitmap>();
    ArrayList<HashMap<String,String>> menuitems = new ArrayList<HashMap<String,String>>();
    boolean loop = true ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.playlist_youtube);

        lv = (ListView)findViewById(R.id.playlist);

        progress = new ProgressDialog(PlayList.this);
        progress.setCancelable(true);
        // Đặt tiêu đề cho ProgressBar
        progress.setMessage("Đang xử lý, vui lòng đợi...");
        // Đặt giao diện cho ProgressBar
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progress.setProgress(0);

        progress.setMax(100);

        progress.show();
        //xử lí cong việc
        new MyThread().start();

        new ParseVideoYoutube().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                YoutubeActivity.VIDEO_ID = menuitems.get(arg2).get("videoId").toString();
                Intent intent = new Intent(getApplicationContext(), YoutubeActivity.class);
                startActivity(intent);
            }
        });





    }

    class MyThread extends Thread{
        int i = 0;
        @Override
        public void run(){

            while(loop) {
                i = menuitems.size();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // tính xem công việc đã hoàn thành bao nhiêu phần trăm và đưa lên progressbar
                progress.setProgress((i * 100) / 50);


            }
            progress.dismiss();
        }


    }

    public class ParseVideoYoutube extends AsyncTask<Void, Void, Void> {

        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            lv.setAdapter(new PlayListAdapter(getApplicationContext(), R.layout.item_playlist,menuitems,manghinh));
        }
        @Override
        protected Void doInBackground(Void... params) {
            URL jsonURL = null ;
            URLConnection jc = null;
            try {
                jsonURL = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=UUbW18JZRgko_mOGm5er8Yzg&key=AIzaSyDPIh5Tm2DIKac5TAkUsVfed2uipW0mBRs");


                jc = jsonURL.openConnection();

                InputStream is = jc.getInputStream();

                //doc du lieu
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(is,"UTF-8"),8192);//iso-8859-1
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null){

                    sb.append(line + "\n");

                }
                is.close();
                String jsonTxt = sb.toString(); //doc StringBuilder vao chuoi*/
                /////////////////////
                Log.d("xemday",jsonTxt.toString());
                JSONObject jj = new JSONObject(jsonTxt);
                //JSONObject jj = readJsonFromUrl(jsonURL.toString());
                JSONArray aitems = jj.getJSONArray("items");Log.d("pass",jj.toString());

                for (int i=0;i<aitems.length();i++)
                {
                    JSONObject item = aitems.getJSONObject(i);
                    JSONObject video = item.getJSONObject("snippet");
                    String title = video.getString("title");
                    JSONObject resource = video.getJSONObject("resourceId");
                    String videoId = resource.getString("videoId");
                    String length = video.getString("publishedAt");
                    JSONObject thumbnail = video.getJSONObject("thumbnails");
                    String thumbnailUrl = thumbnail.getJSONObject("high").getString("url");

                    HashMap<String,String> hashmap=new HashMap<String,String>();
                    hashmap.put("title",title);
                    hashmap.put("thumbnailUrl", thumbnailUrl);
                    hashmap.put("length",length);
                    hashmap.put("videoId", videoId);

                    menuitems.add(hashmap);

                    //lay hinh bo vao mang hinh truoc, khong doi den getView duoc

                    URL url = new URL(thumbnailUrl);
                    HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
                    InputStream ishinh = connection.getInputStream();
                    Bitmap img = BitmapFactory.decodeStream(ishinh);
                    manghinh.add(img);


                    Log.d("dulieu", title);
                    Log.d("duongdan",videoId);
                    Log.d("hinh",thumbnailUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Vui lòng kiểm tra kết nối mạng và thử lại sau",Toast.LENGTH_LONG).show();
                Log.d("loiday",  e.toString());
            }finally {
                loop = false ;

            }


            return null;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

}
