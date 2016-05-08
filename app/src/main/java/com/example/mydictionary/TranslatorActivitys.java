package com.example.mydictionary;

import android.app.ProgressDialog;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import java.util.ArrayList;
import java.util.List;

public class TranslatorActivitys extends AppCompatActivity {


    private ImageButton translate;
    private EditText edit ;
    private Spinner spinFrom , spinTo ;
    private InputMethodManager im;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator_activitys);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        im = (InputMethodManager)getSystemService(this.getApplicationContext().INPUT_METHOD_SERVICE);
        edit = ((EditText) findViewById(R.id.InputText)) ;
        spinFrom = (Spinner)findViewById(R.id.fromLanguage);
        spinTo = (Spinner)findViewById(R.id.toLanguage);
        initLanguage();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        translate = (ImageButton) findViewById(R.id.TranslateButton);
        translate.setOnClickListener(myclick);
        edit.requestFocus();
        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    new bgStuff().execute();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            im.hideSoftInputFromInputMethod(edit.getWindowToken(), 0);
                        }
                    }).start();


                }
                return false;
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initLanguage(){

        List<String> arrLanguageFrom = new ArrayList<String>();
        arrLanguageFrom.add("Auto-detect");
        arrLanguageFrom.add("English");
        arrLanguageFrom.add("Vietnam");
        arrLanguageFrom.add("Japan");
        arrLanguageFrom.add("France");

        List<String> arrLanguageTo = new ArrayList<String>();
        arrLanguageTo.add("English");
        arrLanguageTo.add("Vietnam");
        arrLanguageTo.add("Japan");
        arrLanguageTo.add("France");

        ArrayAdapter<String> adapterFrom = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arrLanguageFrom);
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterTo = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arrLanguageTo);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinFrom.setAdapter(adapterFrom);
        spinTo.setAdapter(adapterTo);

    }

    private Button.OnClickListener myclick = new View.OnClickListener() {


        @Override
        public void onClick(View v) {

            new bgStuff().execute();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    im.hideSoftInputFromInputMethod(edit.getWindowToken(),0);
                }
            }).start();

        }


    };




    public String translate(String text,Language fromLanguage , Language toLanguage) throws Exception {


        // Set the Client ID / Client Secret once per JVM. It is set statically and applies to all services
        Translate.setClientId("brucelee");
        Translate.setClientSecret("jIf1qE8uD9U4UzA5yYJzDuABqY8A6XDDeSshaVeYe/Q="); //change


        String translatedText = "";

        translatedText = Translate.execute(text, fromLanguage, toLanguage);

        return translatedText;
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TranslatorActivitys Page", // TODO: Define a title for the content shown.
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
                "TranslatorActivitys Page", // TODO: Define a title for the content shown.
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

    class bgStuff extends AsyncTask<Void, Void, Void> {

        String translatedText = "";
        String text = edit.getText().toString();
        String from = spinFrom.getSelectedItem().toString() ;
        String to = spinTo.getSelectedItem().toString() ;
        @Override
        protected Void doInBackground(Void... params) {

            // TODO Auto-generated method stub
            try {

                translatedText = translate(text,convertLanguage(from),convertLanguage(to));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                translatedText = e.getMessage();
            }

            return null;
        }

        public Language convertLanguage(String language){

             if(language.equals("English")) return Language.ENGLISH ;
             else if(language.equals("Vietnam")) return Language.VIETNAMESE ;
             else if(language.equals("France")) return Language.FRENCH ;
             else if(language.equals("Japan")) return Language.JAPANESE ;
             else return Language.AUTO_DETECT ;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            ((TextView) findViewById(R.id.OutputText)).setText(translatedText);
            super.onPostExecute(result);
        }

    }
}
