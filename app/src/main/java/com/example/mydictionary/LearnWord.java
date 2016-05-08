package com.example.mydictionary;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by huynhpro on 4/29/2016.
 */
public class LearnWord extends Dialog {

    private ArrayList<String> historyWord ;
    private SharedPreferences pref ;
    private WebView view ;
    private ProgressDialog process ;
    private DictionaryEngine engine ;
    private static final String MIMETYPE = "text/html";
    private static final String ENCODING = "UTF-8";

    public LearnWord(Activity a) {
        super(a);
        this.setTitle("Học từ vựng");
    }

    public void showContent(String mycontent){

        final String content = mycontent ;
        view.loadDataWithBaseURL(null, content, MIMETYPE, ENCODING, "about:blank");


    }

    public void initWeb(){

        view = (WebView)findViewById(R.id.wordlearn);

        view.setBackgroundColor(Color.argb(255, 222, 184, 135));

        view.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (process != null) {
                    process.dismiss();
                    process = null;
                }
            }
        }) ;

    }

    public void LoadHistory(){

        if (pref.getBoolean("saveHistory", true))
        {
            String strHistory = pref.getString("history", "");
            Log.i("CONTENT_TAG", "History loaded");
            if (strHistory != null && !strHistory.equals(""))
            {
                historyWord = new ArrayList<String>(Arrays.asList(strHistory.split(",")));
            }
            else
            {
                if (historyWord == null)
                {
                    historyWord = new ArrayList<String>();
                }
                else
                {
                    historyWord.clear();
                }
            }
        }
        else
        {
            if (historyWord == null)
            {
                historyWord = new ArrayList<String>();
            }
            else
            {
                historyWord.clear();
            }
        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_word);
        initWeb();
        pref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        LoadHistory();
        engine = new DictionaryEngine();
        //engine.getAccessDict();
        //showContent(engine.getContentbyWord(historyWord.get(0).toString()));

    }

}
