package com.supermap.pisaclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.DateUtiles;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.WeatherDataUtil;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.Weather;

public class DragAdapter extends BaseAdapter{
	private List<Weather> mWeathers;
	private List<City> mCities;
	private LayoutInflater mInflater;
	private int mHidePosition = -1;
	private int itemHeight = 250;
	private int  w;
	private int  h;
	private boolean isEditing = false;
	private int mPositon = 0;
	
	public DragAdapter(Context context, List<Weather> list,List<City> cities){
		this.mWeathers = list;
		this.mCities = cities;
		mInflater = LayoutInflater.from(context);
	}

	public void setWeathers(List<Weather> weathers){
		if (mWeathers!=null) {
			this.mWeathers = weathers;
		}
		notifyDataSetChanged();
	}
	
	public void setCitys(List<City> cities){
		if (cities!=null) {
			this.mCities = cities;
		}
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return mCities.size()+1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		convertView = mInflater.inflate(R.layout.grid_item, null);
//		ImageView mImageView = (ImageView) convertView.findViewById(R.id.item_image);
//		TextView mTextView = (TextView) convertView.findViewById(R.id.item_text);
//		
//		mImageView.setImageResource((Integer) list.get(position).get("item_image"));
//		mTextView.setText((CharSequence) list.get(position).get("item_text"));
//		
//		if(position == mHidePosition){
//			convertView.setVisibility(View.INVISIBLE);
//		}
		mPositon = position;
		convertView = mInflater.inflate(R.layout.city_item, null);
		ImageView mImageView = (ImageView) convertView.findViewById(R.id.iv_tepr_ic);
		TextView mtv_cityname = (TextView) convertView.findViewById(R.id.tv_cityname);
		TextView mtv_teprrange = (TextView) convertView.findViewById(R.id.tv_temp_range);
		TextView mtv_setdefault = (TextView) convertView.findViewById(R.id.tv_default);
		ImageView miv_delete = (ImageView)convertView.findViewById(R.id.iv_delete_city);
		
		if (convertView!=null) {
			w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			convertView.measure(w, h);
			itemHeight =convertView.getMeasuredHeight();
		}
		
		String mDefaltcity = ExitApplication.getInstance().mDefaultCity;
		
//		if (position==0) {//第一个自动定位view
//			mtv_cityname.setText("自动定位");
//			if ((mDefaltcity==null)||("自动定位".equals(mDefaltcity))) {
//				mtv_setdefault.setText("默认");
//				mtv_setdefault.setPressed(true);
////				mtv_setdefault.setBackgroundColor(Color.RED);
//			}
//			else {
//				mtv_setdefault.setText("设为默认");
////				mtv_setdefault.setBackgroundColor(Color.BLUE);
//				mtv_setdefault.setPressed(false);
//			}
//			mImageView.setImageResource(R.drawable.wt_sun);
//			mtv_teprrange.setText("--~--");
//		}
		
		if(position<mCities.size()){
			miv_delete.setVisibility(isEditing?View.VISIBLE:View.INVISIBLE);
//			mImageView.setFocusable(isEditing);
//			mtv_setdefault.setFocusable(isEditing);
//			mtv_teprrange.setFocusable(isEditing);
			
			String cityName = mCities.get(position).name;
		    mtv_cityname.setText(cityName );
			if((mDefaltcity!=null)&&mDefaltcity.equals(cityName)){
				mtv_setdefault.setText("默认");
				mtv_setdefault.setPressed(true);
//				mtv_setdefault.setBackgroundColor(Color.RED);
			}
			else {
				mtv_setdefault.setText("设为默认");
				mtv_setdefault.setPressed(false);
//				mtv_setdefault.setBackgroundColor(Color.BLUE);
			}
			//Weather nowWeather = null;
			
			/*for(Weather weather:mWeathers){
				if(weather==null){
					continue;
				}
				
				String wethercity = weather.city;
				if (wethercity.equals("沙坪坝区")) {
					wethercity ="云南省";
				}
				if(wethercity.equals(cityName)){
					nowWeather = weather;
					break;
				}
			}*/
			
			/*if (nowWeather ==null) {//默认天气状况
				mImageView.setBackgroundResource(R.drawable.no_weather);
				mtv_teprrange.setText("--/--");
			}
			else {
				int r_id = WeatherDataUtil.getWeatherDrawbleID(WeatherDataUtil.nowToForcast(nowWeather.WeatherState), DateUtiles.isNignt(nowWeather.ObservTimes));
				if (r_id!=-1) {
					mImageView.setBackgroundResource(r_id);
				}
				mtv_teprrange.setText(nowWeather.MinTemperature+"~"+nowWeather.MaxTemperature);
			}*/
		}
		
		if (position == mCities.size()) {//最后一个添加城市view
			convertView = mInflater.inflate(R.layout.ciy_add_item, null);
			convertView.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, itemHeight));//
			//ImageView mtv_cityadd = (ImageView) convertView.findViewById(R.id.iv_tepr_ic);
		}
		//交换view
		if(position == mHidePosition){
			if(position==mCities.size()){
				
			}
			else {
				convertView.setVisibility(View.INVISIBLE);
			}
			
		}
		
		
		return convertView;
	}

	
	/**
	 * ����ĳ������
	 * @param position
	 */
	public void setItemHide(int position){
		this.mHidePosition = position; 
		notifyDataSetChanged();
	}
	
	public void setEdit(boolean isEdit){
		this.isEditing = isEdit;
	}
}
