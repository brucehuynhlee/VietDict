package com.example.mydictionary.Unity;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;


import com.example.mydictionary.DictionaryEngine;
import com.example.mydictionary.ExternalStorage;

/**
 * Created by huynhpro on 5/7/2016.
 */
public class VietdictProvider extends ContentProvider{

    public static final String PROVIDER_TAG = "[VietDictProvider]";
    public static final String PROVIDER_NAME = "com.example.mydictionary.Unity.VietdictProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME);

    public static final String _ID = "id";
    public static final String COLUMN_WORD = "DatabaseFile";
    public static final String COLUMN_CONTENT = "Content";

    private String mDBExtension ;
    private String mDBPath ;
    private String mCurrentDB = null ;
    private DictionaryEngine mDBEngine ;

    private static final int CODE_LIST = 1;
    private static final int CODE_CONTENT_FROM_ID = 2;
    private static final int CODE_CONTENT_FROM_WORD = 3;

    private static  final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "dict/*/list/*", CODE_LIST);
        uriMatcher.addURI(PROVIDER_NAME, "dict/*/contentId/#", CODE_CONTENT_FROM_ID);
        uriMatcher.addURI(PROVIDER_NAME, "dict/*/contentWord/*", CODE_CONTENT_FROM_WORD);

    }

    @Override
    public boolean onCreate() {

        mDBExtension = ".db" ;
        mDBPath = ExternalStorage.getSdCardPath() + "Andict/db/" ;
        mDBEngine = new DictionaryEngine();

        return true ;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.e(PROVIDER_TAG, "vi du");
        // segment là tính từ 0
        String strDB = uri.getPathSegments().get(1);

        if (mCurrentDB == null || !mCurrentDB.equals(strDB))
        {
            mCurrentDB = strDB;
            mDBEngine.setDatabaseFile(mDBPath, strDB, mDBExtension);
        }
        if (mDBEngine == null)
        {
            Log.e(PROVIDER_TAG, "Can not create database engine");
            return null;
        }

        String word ;
        Cursor cur ;
        //Log.d("XEMDAY","Loi day?");
        switch(uriMatcher.match(uri)){

            case CODE_LIST :
                word = uri.getPathSegments().get(3);
                Log.d("URIMATCHER",word);
                cur = mDBEngine.getCursorWordList(word);
                cur.setNotificationUri(getContext().getContentResolver(),uri);
                Log.d("XEM","Loi day?");
                return cur ;
            case CODE_CONTENT_FROM_WORD :
                word = uri.getPathSegments().get(3);
                cur = mDBEngine.getCursorContentFromWord(word);
                cur.setNotificationUri(getContext().getContentResolver(),uri);
                return cur ;
            default:
                Log.e(PROVIDER_TAG,"Unsuport URI");
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
