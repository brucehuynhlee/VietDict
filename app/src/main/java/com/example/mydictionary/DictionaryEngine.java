package com.example.mydictionary;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.speech.tts.TextToSpeech.Engine;
import android.util.Log;
import android.widget.Toast;

import com.example.mydictionary.Unity.VietdictProvider;


public class DictionaryEngine {
	
	static final private String SQL_TAG = "MyDict-DictionaryEngine";

	private SQLiteDatabase dictDB ;
	
	// tên database
	private String DBName ;
	
	// đường dẫn tới thư mục
	private String DBPath ;
	
	private static final String dbExtensence = ".db" ; 
	
	// list từ tìm kiếm
	public ArrayList<String> listCurrentWord ;
	
	// list nghĩa của từ 
	public ArrayList<String> listCurrentContent ;
	
	
	public DictionaryEngine(){
		listCurrentContent = new ArrayList<String>() ;
		listCurrentWord = new ArrayList<String>() ;
	}
	public DictionaryEngine(String dbName , String basePath ){
		
		   DBName = dbName ;
		   DBPath = basePath ;
		   
		   listCurrentContent = new ArrayList<String>() ;
		   
		   listCurrentWord = new ArrayList<String>() ;
		
	}
	

	public boolean setDatabaseFile(String basePath, String dbName, String dbExtension)
	{
		if (dictDB != null)
		{
			if (dictDB.isOpen() == true) // Database is already opened
			{
				if (basePath.equals(DBPath) && dbName.equals(DBName)) // the opened database has the same name and path -> do nothing
				{
					Log.i(SQL_TAG, "Database is already opened!");
					// if true , database is opened
					return true;
				}
				else
				{
					dictDB.close();
				}
			}
		}

		String fullDbPath="";

		try
		{
			fullDbPath = basePath + dbName + dbExtension;
			dictDB = SQLiteDatabase.openDatabase(fullDbPath, null, SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}
		catch (SQLiteException ex)
		{
			ex.printStackTrace();
			Log.i(SQL_TAG, "There is no valid dictionary database " + dbName +" at path " + basePath);
			return false;
		}

		if (dictDB == null)
		{
			return false;
		}

		this.DBName = dbName;
		this.DBPath = basePath;

		Log.i(SQL_TAG,"Database " + dbName + " is opened!");

		return true;
	}
	
	// dữ liệu tìm kiếm
	public Cursor getCursorWordList(String word){
		String sql = null ;
		
		if(word == null || word.equals("")){
			
			 sql = "Select * from " + DBName + " LIMIT 0, 50" ;
		}
		else {
			
			sql = "Select * from " + DBName + " WHERE word >= '" + word + "' LIMIT 0, 50 " ;
		}
		Cursor cur = null ;
		try {
		    cur = dictDB.rawQuery(sql, null) ;
		   }catch(Exception e){
			   Log.i("MyDict", "kiem tra loi" + e.getMessage());
		   }
		Log.i("MyDict", "thanh cong" );
		return cur ;
	}
	
	// các từ tìm kiếm
	public boolean getWordList(Cursor cur){
		
		//Cursor cur = getCursorWordList(word);
		int indexWord = cur.getColumnIndex("word");
		int indexContent = cur.getColumnIndex("content");
		Log.i("Column", "word and content" + indexContent + " " + indexWord ) ;
		if(cur != null){
			
			int countRow = cur.getCount() ;
			Log.i(SQL_TAG, "count Row " + countRow) ;
			listCurrentContent.clear();
			listCurrentWord.clear();
			
			if(countRow > 0){
				cur.moveToFirst() ;
				int i = 0 ;
				//Log.i("First_Word", "từ đầu tiên là : " + cur.getString(indexWord));
				do{
					String Words = Utility.decodeContent(cur.getString(indexWord));
                	String Content = Utility.decodeContent(cur.getString(indexContent));
                    listCurrentWord.add(i,Words);
                    listCurrentContent.add(i,Content);
                    i++;
                    Log.i("Get_Data", "word" + Words + "mean : " + Content);
				}while(cur.moveToNext());
				
			}

			
		}
		return true ;
		
	}
	


	public Cursor getCursorContentFromWord(String word)
	{
		String query;
		// encode input
		if (word == null || word.equals(" "))
		{
			return null;
		}




		query = "SELECT id,content,word FROM " + DBName + " WHERE word = ? ";

		Cursor result = dictDB.rawQuery(query,new String[]{word});
		Log.d("SQL_TAG", result.getCount() + "");
		return result;
	}



	public String getContentByWord(Cursor cur){
		cur.moveToFirst() ;
		String re = cur.getString(cur.getColumnIndex("content"));
		Log.i(SQL_TAG, "value returned for main : " + re);
		return re ;
	}

	public void closeDatabase(){
		dictDB.close();

	}

	public boolean isOpen(){

		return dictDB.isOpen();
	}

	public boolean isReadOnly(){
		return dictDB.isReadOnly();
	}
}
