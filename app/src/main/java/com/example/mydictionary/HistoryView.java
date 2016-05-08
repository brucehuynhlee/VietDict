package com.example.mydictionary;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DialogTitle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class HistoryView extends ActionBarActivity {
    private FloatingActionButton fab ;
	private ImageButton back ;
	private ArrayAdapter<String> adapter ;  
	private ArrayList<String> listword  ;
	ArrayList<String> temp ;
	private ListView list ;
	private SearchView mSearchView ;
	private DialogTitle dialog ;

	private static final String TAG = "[VietDict_TAG]";

	private void getContent(){
		Intent intent = this.getIntent();
		temp = intent.getStringArrayListExtra("list");
		//Toast.makeText(getApplicationContext(), "đo dài list gui den : " + temp.size(), Toast.LENGTH_LONG).show();
		listword = new ArrayList<String>();
		int size = temp.size();
		int  i = 0 ;
		while(i < size ){
			String subStr = temp.get(i);
			String word = subStr.substring(subStr.indexOf(":") + 1, subStr.length()) ;
			listword.add(word);
			Log.d(TAG,"từ : "+ subStr + " batdau : " + (subStr.indexOf(":") + 1) + " ketthuc : " + (subStr.length() - 1));
			i++ ;
		}

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_view);

		list = (ListView)findViewById(R.id.listWord);
		fab = (FloatingActionButton)findViewById(R.id.fab);


		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLUE));

		getContent();
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listword);
		list.setAdapter(adapter);

		adapter.notifyDataSetChanged();

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				  

				Intent data = new Intent();
				data.putExtra("word",temp.get(position) );
				data.putStringArrayListExtra("list", temp);
				setResult(3,data);
				finish();
				
				
			}});
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final int pos = position;
				new AlertDialog.Builder(HistoryView.this)
						.setTitle("Bạn đang xóa lịch sử")
						.setMessage("Bạn muốn xóa từ :" + adapter.getItem(position) + " ?")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int whichButton) {
								listword.remove(pos);
								temp.remove(pos);
								adapter.notifyDataSetChanged();
							}
						})
						.setNegativeButton(android.R.string.no, null).show();
				return false;
			}
		});

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplication(),"This is FAB",Toast.LENGTH_LONG).show();
				Intent data = new Intent();
				data.putStringArrayListExtra("list", temp);
				setResult(4,data);
				finish();
			}
		});
	}
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent data = new Intent();
			data.putStringArrayListExtra("list", temp);
			if(listword.size()!=0){
				setResult(2,data);
			}
			else {
				setResult(4, data);
				Toast.makeText(this.getApplication(),"this is history view",Toast.LENGTH_LONG).show();
			}
			finish();
			return true ;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history_view, menu);
		MenuItem itemSearch = menu.findItem(R.id.action_search);
		MenuItem itemClear = menu.findItem(R.id.action_delete);
		itemClear.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				new AlertDialog.Builder(HistoryView.this)
				.setTitle("Delete")
				.setMessage("Bạn có muốn xóa hết các từ trong lịch sử ? ")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

				    public void onClick(DialogInterface dialog, int whichButton) {
				    	listword.clear();
						temp.clear();
						adapter.notifyDataSetChanged();			
				    }})
				 .setNegativeButton(android.R.string.no, null).show();
				return false ;
			}
		});
		mSearchView = (SearchView)itemSearch.getActionView();
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String text) {
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String text) {
				
				if(TextUtils.isEmpty(text)){
					adapter.getFilter().filter("");
					list.clearTextFilter();
				}else{
					adapter.getFilter().filter(text.toString());
				}
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

			Intent data = new Intent();
			data.putStringArrayListExtra("list", temp);
			if(listword.size()!=0){//xóa 1 số hoặc không
				setResult(ContentView.FIX_HISTORY,data);
			}
			else {//xóa hết
				setResult(ContentView.REMOVE_HISTORY, data);
				Toast.makeText(this.getApplication(),"this is history view",Toast.LENGTH_LONG).show();
			}
			finish();
			return true ;

		}
		return super.onOptionsItemSelected(item);
	}
}
