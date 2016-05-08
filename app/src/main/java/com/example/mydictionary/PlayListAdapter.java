package com.example.mydictionary;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by huynhpro on 5/5/2016.
 */
public class PlayListAdapter extends ArrayAdapter {

    ArrayList<HashMap<String,String>> menuitems;
    ArrayList<Bitmap> manghinh;
    Context context;

    public PlayListAdapter(Context context, int textViewResourceId,
                     ArrayList<HashMap<String,String>> menuitems, ArrayList<Bitmap> manghinh) {

        super(context, textViewResourceId, menuitems);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.menuitems=menuitems;
        this.manghinh=manghinh;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inf=(LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowview=inf.inflate(R.layout.item_playlist,parent, false);


        TextView textviewtitle=(TextView)rowview.findViewById(R.id.title);

        TextView textviewlength=(TextView)rowview.findViewById(R.id.length);
        ImageView imageview=(ImageView)rowview.findViewById(R.id.image1);

        textviewtitle.setText(menuitems.get(position).get("title").toString());
        textviewlength.setText(menuitems.get(position).get("length").toString());

        //lay hinh trong mang hinh da chuan bi san
        imageview.setImageBitmap(manghinh.get(position));


        return rowview;
    }

}
