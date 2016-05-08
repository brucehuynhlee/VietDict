package com.example.mydictionary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.Toast;

import org.ispeech.SpeechSynthesis;
import org.ispeech.SpeechSynthesisEvent;
import org.ispeech.error.BusyException;
import org.ispeech.error.InvalidApiKeyException;
import org.ispeech.error.NoNetworkException;

@SuppressLint("NewApi")
public class ContentView extends ActionBarActivity {

    private WebView view ;
    private SearchView mSearchView ;
    private SpeechSynthesis synthesis ;
	private static final String TAG = "SPEECH_ERROR" ;

    private ArrayList<String> historyWord , listFavorite ;
    private int CurrentHistoryIndex = -1;

    private static final String MIMETYPE = "text/html";
    private static final String ENCODING = "UTF-8";

	/**
	 * Xem lịch sử
	 * */
    protected static final int SHOW_HISTORY_CODE = 1 ;
	/**
	 * Xóa lịch sử
	 * */
    protected static final int FIX_HISTORY = 2 ;
    protected static final int TAKE_HISTORY = 3 ;
	/**
	 * Xóa hết lịch sử
	 * */
    protected static final int REMOVE_HISTORY = 4 ;
	protected static final int Main_Menu = 5 ;


    private SharedPreferences pref , faPref ;

	private ProgressDialog process ;

	private String SelectDB = "anh_viet" ;
	private String currentWord , contentWord ;

	ImageButton btnBack , btnForward , btnFind , btnHistory , btnPro;

	DictionaryEngine engine ;

	public void initWeb(){

		view = (WebView)findViewById(R.id.webView);

	 	view.setBackgroundColor(Color.argb(255, 222,184,135));

        view.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView view, String url) {
				if (process != null) {
					process.dismiss();
					process = null;
				}
			}
		}) ;

      }

	public void goBack(){
		if(CurrentHistoryIndex < 0) return ;
		String content = historyWord.get(CurrentHistoryIndex);
		String temp = getContent(content);
		showContent(temp);

	}

	public void goForward(){
		if(CurrentHistoryIndex >= historyWord.size()) return ;
		String content = historyWord.get(CurrentHistoryIndex);
		String temp = getContent(content);
		showContent(temp);

	}

	@Override
	public void onPause()
	{
		super.onPause();
		saveHistorytoReference();
	}

	public void saveHistorytoReference(){
		if(pref.getBoolean("saveHistory", true) && historyWord != null && historyWord.size() >= 0 ){

			StringBuilder sbHistory = new StringBuilder();
			for(String item : historyWord){
				sbHistory.append(item);
				sbHistory.append(",");
			}
			String strHistory = null ;
			if(historyWord.size()> 0)
			   strHistory = sbHistory.substring(0,sbHistory.length()-1);
			SharedPreferences.Editor edit = pref.edit();
			edit.putInt("index", CurrentHistoryIndex);
			edit.putString("history", strHistory);
			edit.commit();
		}

	}

	public void loadHistoryfromReference(){

		if (pref.getBoolean("saveHistory", true))
		{
			String strHistory = pref.getString("history", "");
			CurrentHistoryIndex = pref.getInt("index",0);
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

	public void saveHistory(){
		Log.d("VietDict","từ điển :" + SelectDB);
		String item = null ;
		//if(cu)
		  item = SelectDB + ":" + currentWord ;
		Log.d("VIETDICT","Noidung : " + item);
		if(historyWord.indexOf(item) == -1){
			historyWord.add(item);
			Log.d("VietDict","nội dung :" + item);
			Toast.makeText(getApplicationContext(), "history size =  " + historyWord.size(), Toast.LENGTH_LONG).show();
			CurrentHistoryIndex = historyWord.indexOf(item);
		}

	}

	public String getContentfromHistory(){

		return SelectDB + ":" + currentWord ;
	}

	public void showHistory(){
		if(historyWord == null || historyWord.size() == 0) {
			Toast.makeText(getApplicationContext(), "Nothing in history", Toast.LENGTH_LONG).show();
			return ;
		}
		Intent intent = new Intent(ContentView.this,HistoryView.class);
		intent.putStringArrayListExtra("list", historyWord);
		startActivityForResult(intent, SHOW_HISTORY_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){

		   case SHOW_HISTORY_CODE :
			   ArrayList<String> temp = data.getStringArrayListExtra("list");
			   historyWord.clear();
			   int size = temp.size() ;
			   int i = 0 ;
			   while(i < size){
				   historyWord.add(temp.get(i));
				   i++ ;
			   }
			   saveHistorytoReference();
               loadHistoryfromReference();
			   if(resultCode == FIX_HISTORY){
				   if(historyWord.size()==0) finish();
				   while(CurrentHistoryIndex >= historyWord.size()){
					   CurrentHistoryIndex -- ;
				   }



				   //Toast.makeText(getApplicationContext(), "size : " + historyWord.size() , Toast.LENGTH_LONG).show();
                   //Toast.makeText(getApplicationContext(), "size : " + historyWord.size() , Toast.LENGTH_LONG).show();
			   }
			   else if(resultCode == TAKE_HISTORY) {
				   String reciveWord = data.getCharSequenceExtra("word").toString();
				   CurrentHistoryIndex = historyWord.indexOf(reciveWord);
				   //showContent(getContent(reciveWord));
			   }
			   else if(resultCode == REMOVE_HISTORY){
				   Toast.makeText(this.getApplication(),"this content vew" ,Toast.LENGTH_LONG ).show();
				   Log.d(TAG, "return main");
				   //setResult(Main_Menu, new Intent());
				   finish();
			   }

		}

	}



	private void prepareTTSEngine() {
		try {
			synthesis = SpeechSynthesis.getInstance(this);
			synthesis.setSpeechSynthesisEvent(new SpeechSynthesisEvent() {

				public void onPlaySuccessful() {
					Log.i(TAG, "onPlaySuccessful");
				}

				public void onPlayStopped() {
					Log.i(TAG, "onPlayStopped");
				}

				public void onPlayFailed(Exception e) {
					Log.e(TAG, "onPlayFailed");


					AlertDialog.Builder builder = new AlertDialog.Builder(ContentView.this);
					builder.setMessage("Error[MainActivity]: " + e.toString())
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}

				public void onPlayStart() {
					Log.i(TAG, "onPlayStart");
				}

				@Override
				public void onPlayCanceled() {
					Log.i(TAG, "onPlayCanceled");
				}
			});
			synthesis.setStreamType(AudioManager.STREAM_MUSIC);

		} catch (InvalidApiKeyException e) {
			Log.e(TAG, "Invalid API key\n" + e.getStackTrace());
			Toast.makeText(this.getApplicationContext(), "ERROR: Invalid API key", Toast.LENGTH_LONG).show();
		}
	}


	public void init(){

		btnBack = (ImageButton)findViewById(R.id.back);
		btnForward = (ImageButton)findViewById(R.id.forward);
		btnHistory = (ImageButton)findViewById(R.id.history);
		btnPro = (ImageButton)findViewById(R.id.pronunciation);

		pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		engine = new DictionaryEngine();
		prepareTTSEngine();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_view);

		init();
		initWeb();

		Intent i = this.getIntent();
		CharSequence word = i.getCharSequenceExtra("contentWord");
		String content = getContent(word.toString());
		//load data history into array
		loadHistoryfromReference();
		//save data on array
		saveHistory();
		//Log.i("size arraylist", "Size history is : " + historyWord.size());

		process = ProgressDialog.show(this, "Vui lòng đợi giây lát..", "Đang tải dữ liệu", true,false);
		//engine.getAccessDict();
		showContent(content);

		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			  if(CurrentHistoryIndex != 0){
				CurrentHistoryIndex -- ;
				goBack();
			  }

			}
		});

        btnForward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			   if(CurrentHistoryIndex != historyWord.size()){
				 CurrentHistoryIndex ++ ;
				 goForward();
			   }
			}
		});

        btnHistory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showHistory();

			}
		});
		btnPro.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				proWord();

			}
		});

	}


	public void proWord(){
                String word = null ;
				if (currentWord == null || currentWord == "") {
					word = "please enter some word";
					return;
				}
		        word = currentWord ;
				try {
					synthesis.speak(word);
				} catch (BusyException e) {
					Toast.makeText(getApplicationContext(), "SDK is busy", Toast.LENGTH_LONG).show();
					Log.i(TAG, "SDK is busy");
					e.printStackTrace();
				} catch (NoNetworkException e) {
					Log.i(TAG, "No network connect");
					Toast.makeText(getApplicationContext(), "No Internet Connect", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}


	}




	public void showContent(String myContent){

		final String content = myContent ;
		runOnUiThread(new Runnable() {
		    public void run() {
		    	view.loadDataWithBaseURL (null, content, MIMETYPE, ENCODING,"about:blank");
		    }
		});

	}

	public String getContent(String temp){

       if(temp.contains(":")){
		  currentWord = temp.substring(temp.indexOf(":")+1, temp.length());
		  SelectDB = temp.substring(0,temp.indexOf(":")-1);
	   }else currentWord = "a" ;
		Log.d("CURRENTWORD",currentWord);
		Uri uri = Uri.parse("content://com.example.mydictionary.Unity.VietdictProvider/dict/" + "anh_viet" + "/contentWord/" + currentWord);
		Cursor result = managedQuery(uri,null,null,null,null);

		return engine.getContentByWord(result);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.searchview, menu);
        MenuItem item = menu.findItem(R.id.action_addfavourite);
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				String word = historyWord.get(CurrentHistoryIndex);
				String listFa = pref.getString("favourite", "");
				listFavorite = new ArrayList<String>(Arrays.asList(listFa.split(",")));
				if(listFavorite.contains(word)){
					Toast.makeText(getApplication(),"Từ (" + word + ") đã có trong danh sách ưa thích" ,Toast.LENGTH_SHORT).show();
                    return false ;
				}
				StringBuilder str = new StringBuilder(listFa);
				if (!listFa.equals("")) {
					str.append(",");

				}
				str.append(word);
				SharedPreferences.Editor editor = pref.edit();
				editor.putString("favourite",str.toString());
				editor.commit();
				Toast.makeText(getApplication(),"Đã thêm " + word + " vào danh sách yêu thích",Toast.LENGTH_SHORT).show();

				return true ;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if(id == android.R.id.home){
			Toast.makeText(getApplicationContext(),"this is main", Toast.LENGTH_LONG).show();
			setResult(Main_Menu,new Intent());
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
