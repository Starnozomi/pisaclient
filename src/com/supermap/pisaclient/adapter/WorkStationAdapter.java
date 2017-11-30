package com.supermap.pisaclient.adapter;

import java.util.List;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.entity.WorkStation;
import com.supermap.pisaclient.grouplist.ContactItemInterface;
import com.supermap.pisaclient.grouplist.ContactListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WorkStationAdapter extends BaseAdapter
{
	private LayoutInflater mInflater;
	private Context mContext;
	private List<WorkStation> workStations;
	public WorkStationAdapter(Context context, List<WorkStation> items)
	{
			this.mContext = context;
			this.mInflater  = LayoutInflater.from(context);
			this.workStations = items;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return workStations.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return workStations.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = (RelativeLayout) mInflater.inflate(R.layout.workstation_item, null);
			holder.stationName = (TextView)convertView.findViewById(R.id.tv_station_name);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		WorkStation ws = workStations.get(position);
		holder.stationName.setText(ws.stationName);
		return convertView;
	}

	
	private class ViewHolder {
		TextView stationName;
	}

}
