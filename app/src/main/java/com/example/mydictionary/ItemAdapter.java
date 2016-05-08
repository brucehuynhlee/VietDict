package com.example.mydictionary;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemAdapter extends BaseAdapter{
	
	private LayoutInflater inflater ;
	private Context context ;
	private ArrayList<ItemNavigation> arr ;
	
	public ItemAdapter(Context context , ArrayList<ItemNavigation> arr) {
		super();
		this.context = context ;
		this.arr = arr ;
		inflater = LayoutInflater.from(context);
		
	}

	@Override
	public int getCount() {
		int count  = 0 ;
		if(arr != null){
			count = arr.size() ;
		}
		return count ;
	}

	@Override
	public Object getItem(int position) {
		return arr.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0 ;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		viewHolder holder ;
		
		if(convertView == null){
			holder = new viewHolder() ;
			convertView = inflater.inflate(R.layout.layout_item, parent,false);
			holder.itemIcon = (ImageView)convertView.findViewById(R.id.itemIcon);
			holder.itemName = (TextView)convertView.findViewById(R.id.itemName);
			convertView.setTag(holder);
			
		}else {
			
			holder = (viewHolder)convertView.getTag();
		}
		
		final ItemNavigation itemNavi = arr.get(position);
		if(itemNavi != null){
			holder.itemIcon.setImageResource(itemNavi.getItemIcon());
			holder.itemName.setText(itemNavi.getItemName());
		}
		
		return convertView ;
	}
	
	public class viewHolder {
		
		public TextView itemName ;
		public ImageView itemIcon ;
		
	}

}

