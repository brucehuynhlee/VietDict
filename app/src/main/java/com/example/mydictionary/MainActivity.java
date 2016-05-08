package com.example.mydictionary;



import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;



@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {


	private static final String TAG = "[MYDICTIONARY_SAMPLE]";
	private final static String LOG_DATA = "[DataFound]";
	private static final int FAVOURITE = 10 ;
	private static final int NONE = 90 ;
	// danh sách từ hiển thị trên giao diện ;
	private ListView listWord = null, leftList;
	private SearchView mSearchView;
	// danh sach từ hiện tại
	private ArrayList<String> currentWord = null;
	private List<String> listLanguage;

	private EditText edit ;

	private FloatingActionButton btnAction ;

	// áp dữ liệu lên listview 
	private ArrayAdapter<String> adapter , languageAdapter ;

    private SharedPreferences pref ;

	private Cursor result ;

	private static final String DBExtension = ".db" ;
	// từ được chọn
	private String word = " " ;

	private InputMethodManager im;
    private Boolean checkInput = false ;

	DatabaseFileList dataLanguage ;
	DictionaryEngine engine;
	Spinner spin ;

	ArrayList<ItemNavigation> arr;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;




	private void initArrayForViewDrawer() {

		arr = new ArrayList<ItemNavigation>();

		ItemNavigation item1 = new ItemNavigation(android.R.drawable.ic_menu_preferences, "Cài đặt");
		arr.add(item1);
		ItemNavigation item2 = new ItemNavigation(R.drawable.memory,"Học từ");
		arr.add(item2);
		ItemNavigation item3 = new ItemNavigation(R.drawable.farvorite, "Yêu thích");
		arr.add(item3);
		ItemNavigation item4 = new ItemNavigation(R.drawable.translateicon,"Dịch câu");
		arr.add(item4);

		ItemNavigation item5 = new ItemNavigation(R.drawable.youtube,"Tìm kiếm video youtube");
		arr.add(item5);

		ItemNavigation item6 = new ItemNavigation(R.drawable.youtubeshop,"Danh sách video học TA");
		arr.add(item6);

		ItemNavigation item7 = new ItemNavigation(R.drawable.aboutsmall,"Giới thiệu");
		arr.add(item7);

		ItemAdapter adapter = new ItemAdapter(this, arr);
		leftList.setAdapter(adapter);
	}


	private void mulLanguage(){
		spin = (Spinner)findViewById(R.id.language);
		Log.d(LOG_DATA,"timkiem");
		listLanguage = new ArrayList<String>();
		dataLanguage = new DatabaseFileList();
		int size = dataLanguage.items.size();
		Log.d(LOG_DATA,size + "");
		int  i = 0 ;
		/*while(i < size){
			listLanguage.add(dataLanguage.items.get(i).destinationLanguage);
			Log.d(LOG_DATA, listLanguage.get(i));
			i++ ;
		}*/
		listLanguage.add("ve");
		languageAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listLanguage);
		languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		try{
		     spin.setAdapter(languageAdapter);
		}catch (Exception e){
			//e.printStackTrace();
			Log.d("Loi ngon ngu","cc");
		}


	}

    /**
	 * Khởi tạo
	 * */
	public void init() {


		mulLanguage();
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		engine = new DictionaryEngine();
		listWord = (ListView) findViewById(R.id.listWord);
		leftList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btnAction = (FloatingActionButton)findViewById(R.id.FA);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		// set Icon for actionbar
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.drawer);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.CYAN));
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.drawer, R.string.open_drawer, R.string.close_drawer) {

			public void onDrawerClosed(View view) {
				// Toast.makeText(MainActivity.this, "Drawer closed",Toast.LENGTH_SHORT).show();
				getSupportActionBar().setTitle(R.string.app_name);
				if(!checkInput) im.showSoftInputFromInputMethod(mDrawerLayout.getWindowToken(),0);
				Log.d("debug", "onDrawerClosed");
				//invalidateOptionsMenu(); // tao lai menu sau khi options menu da duoc thay doi
			}

			public void onDrawerOpened(View drawerView) {
				//Toast.makeText(MainActivity.this, "Drawer Opened",Toast.LENGTH_SHORT).show();
				getSupportActionBar().setTitle("Settings");
				if(im.isActive()) {
					im.hideSoftInputFromWindow(mDrawerLayout.getWindowToken(), 0);
					checkInput = true ;
				}
				Log.d("debug", "onDrawerOpened");
				//invalidateOptionsMenu();
			}
		};

		getSupportActionBar().setDisplayShowCustomEnabled(true);

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		currentWord = new ArrayList<String>();

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, currentWord);
		listWord.setAdapter(adapter);

		//showWordlist();

		// hiển thị từ được chọn 
		listWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				im.hideSoftInputFromWindow(mDrawerLayout.getWindowToken(), 0);

				word = adapter.getItem(position);

				showContentbyClick();


			}
		});

		leftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
					startActivityForResult(intent, 1000);
				} else if (position == 1) {
					LearnWord lw = new LearnWord(MainActivity.this);
					lw.show();

				} else if (position == 2) {
					Intent intent = new Intent(getApplicationContext(), FavouriteActivity.class);
					startActivityForResult(intent, FAVOURITE);

				} else if (position == 3) {
					Intent intent = new Intent(getApplicationContext(), TranslatorActivitys.class);
					startActivityForResult(intent, NONE);
				} else if (position == 4) {
					Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
					startActivityForResult(intent, NONE);

				} else if (position == 5) {
					Intent intent = new Intent(getApplicationContext(), PlayList.class);
					startActivityForResult(intent, NONE);

				}

			}
		});


		btnAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				edit.requestFocus();
				View view = edit.findFocus();
				im.showSoftInput(view, 0);

				//Intent intent = getIntent();
				//intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				//finish();
				//startActivity(intent);

			}
		});




	}

	// hiển thị từ và nghĩa
	public void showContentbyClick() {

		currentWord.clear();
		currentWord.add(word);
		adapter.notifyDataSetChanged();

		showContent();
	}


	// danh sách từ phù hợp với từ đang tìm  OK
	public void showWordlist() {

		Log.i("DATA_LIST", "xem dữ liệu");
		currentWord.clear();

		Log.i(LOG_DATA, "Vào dữ liệu");
		Uri uri = Uri.parse("content://com.example.mydictionary.Unity.VietdictProvider/dict/" + "anh_viet" + "/list/" + word);
		result = managedQuery(uri,null,null,null,null);
		if (engine.getWordList(result)) {

			int i = 0;
			while (engine.listCurrentContent.size() > i) {
				currentWord.add(engine.listCurrentWord.get(i));
				i++;
			}

			adapter.notifyDataSetChanged();
			Log.i("countData", "current has " + currentWord.size() + " word");


		}


	}


	// hiển thị nội dung từ được chọn
	public void showContent() {

		runOnUiThread(new Runnable() {
			public void run() {
				Intent intent = new Intent(getApplicationContext(), ContentView.class);
				intent.putExtra("contentWord", "anh_viet:"+word);
				startActivity(intent);
			}
		});

	}




	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
             super.onActivityResult(requestCode,resultCode,data);
		     switch (requestCode){
				 case FAVOURITE :
					  mDrawerLayout.closeDrawers();

			 }

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		setContentView(R.layout.activity_main);



		init();
		initArrayForViewDrawer();
		//Toast.makeText(getApplicationContext(),"gia trị đây "+ pref.getBoolean("pref_learn",true),Toast.LENGTH_LONG).show();
		if(pref.getBoolean("pref_learn",true)) {
			Toast.makeText(getApplicationContext(),""+ pref.getBoolean("pref_learn",true),Toast.LENGTH_LONG).show();
			LearnWord lw = new LearnWord(MainActivity.this);
			lw.show();
		}


		ActionBar actionBar = getSupportActionBar();
		//actionBar.setLogo(R.drawable.launcher);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);




		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		//Thêm custom actionbar
		getSupportActionBar().setCustomView(R.layout.search_layout);

		MenuItem itemSearch = menu.findItem(R.id.action_search);
		//MenuItem itemClear = menu.findItem(R.id.action_delete);

		//Bắt sự kiện customview
		View v = getSupportActionBar().getCustomView();
		edit = (EditText) v.findViewById(R.id.txt_search);
        edit.requestFocus();
		//im.showSoftInputFromInputMethod(edit.getWindowToken(),0);
		edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){

					word = edit.getText().toString();
					showContent();
					new Thread(new Runnable() {
						@Override
						public void run() {
							im.hideSoftInputFromInputMethod(edit.getWindowToken(),0);
						}
					}).start();


				}
				return false;
			}
		});

		edit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				word = edit.getText().toString();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							//showWordlist();
						}catch (Exception e){
							e.printStackTrace();
							Log.d(TAG,e.getMessage());
						}
					}
				});

			}

			@Override
			public void afterTextChanged(Editable s) {

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
		if (id == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			} else {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}

		}
		/*if(id == R.id.action_search){

			edit.requestFocus();
			View view  = edit.findFocus();
			im.showSoftInput(view, 0);
			Toast.makeText(getApplication(),"click" , Toast.LENGTH_LONG).show();
		}*/
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
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
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();

		result.requery();
	}
	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
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
