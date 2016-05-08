package com.example.mydictionary;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by huynhpro on 5/7/2016.
 */
public class DatabaseFileList {

    private  static final String FILE_LIST = "VietDict" ;
    private  static final String DBExtension = ".db" ;

    public ArrayList<DatabaseFile> items ;

    public DatabaseFileList(){

           items = new ArrayList<DatabaseFile>();
           getDatabaseFileList(ExternalStorage.getSdCardPath() + "Andict/db/" ,DBExtension);

    }

    public void getDatabaseFileList(String dbPath , String dbExtension){

        items.clear();

        File dataDirectory = new File(dbPath);

        if(!dataDirectory.exists()){

            if(!dataDirectory.mkdirs()){

                Log.e(FILE_LIST,"can not create directory");
            }else {

                Log.e(FILE_LIST,"Create directory success");
            }
        }

        FileFilter ffDir = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                Log.d("xemthu",pathname.toString());
                return pathname.isDirectory();
            }
        };

        File[] listDir = dataDirectory.listFiles(ffDir);


        if(listDir != null && listDir.length > 0){

            for (File curentDir : listDir){
                //if(!curentDir.getName().contains(DBExtension)) continue ;
                DatabaseFile db = new DatabaseFile();
                // currentDir là đường dẫn đến file cần tìm
                String path = curentDir.getAbsolutePath() + "/" + curentDir.getName();

                db.fileName = curentDir.getName();

                db.path = curentDir.getPath();
                // file thông tin
                File ifoFile = new File(path + ".ifo");

                Log.i(FILE_LIST,curentDir.getName().toString());
                if (ifoFile.exists())
                {

                    String data;
                    String arrData[] = null;
                    try
                    {
                        BufferedReader brIfoFile = new BufferedReader(new FileReader(ifoFile));
                        while (brIfoFile.ready())
                        {
                            data = brIfoFile.readLine();
                            arrData = data.split("=");
                            arrData[0] = arrData[0].trim();
                            if (arrData[0].equals("name"))
                            {
                                db.dictionaryName = arrData[1].trim();
                                Log.i(FILE_LIST, "dictionaryName = " + arrData[1]);
                            }
                            else if (arrData[0].equals("from"))
                            {
                                db.sourceLanguage = arrData[1].trim();
                                Log.i(FILE_LIST, "from = " + arrData[1]);
                            }
                            else if (arrData[0].equals("to"))
                            {
                                db.destinationLanguage= arrData[1].trim();
                                Log.i(FILE_LIST, "to = " + arrData[1]);
                            }
                            else if (arrData[0].equals("style"))
                            {
                                db.style= arrData[1].trim();
                                Log.i(FILE_LIST, "style = " + arrData[1]);
                            }
                        }
                    }
                    catch (Exception ex){
                        db.dictionaryName = db.fileName;
                        Log.e(FILE_LIST, "Can not read ifo file!");
                    }

                }
                else
                {
                    db.dictionaryName = db.fileName;
                    Log.i(FILE_LIST, "No ifo file found, set dictionary name to file name");
                }
                //add to list
                items.add(db);

            }

            Log.i(FILE_LIST,"Found " + items.size() + " dictionaries");
        }else
        {
            Log.i(FILE_LIST,"Do not find any valid dictionary");
        }




    }

}
