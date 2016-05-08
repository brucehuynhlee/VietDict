package com.example.mydictionary;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by brucelee on 3/17/2016.
 */
public class FavoriteEngine {

    private SQLiteDatabase sqlDic ;

    private String pathFavo ;

    private ArrayList<String> favoArray ;

    public boolean getAccess(){
        StringBuilder str = new StringBuilder();
        if(ExternalStorage.isAvailable()){
            str.append(ExternalStorage.getSdCardPath());
            str.append("Andict/Favourite");
        }
        else return false ;
       if(sqlDic != null){
           if(sqlDic.isOpen()){
               sqlDic.close();
           }

       }
        //Kiem tra co dương dan chua
        File f = new File(str.toString());
        if(!f.isDirectory()){
            f.mkdir();
        }
        str.append("/favo.db");
        pathFavo = str.toString() ;
        try{
            sqlDic = SQLiteDatabase.openOrCreateDatabase(pathFavo,null);
            // Log.i(SQL_TAG, "database found") ;
        }catch(Exception e){
            e.printStackTrace();
            Log.i("SQL_TAG", "There is no valid dictionary database " + pathFavo  + "with error" + e.getMessage());
            return false;

        }


        return false ;

    }
}
