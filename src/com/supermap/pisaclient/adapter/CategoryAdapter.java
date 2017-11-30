package com.supermap.pisaclient.adapter;

import java.util.List;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.entity.Category;
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.entity.Product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CategoryAdapter extends BaseAdapter {

	private static final int TYPE_CATEGORY_ITEM = 0;
	private static final int TYPE_ITEM = 1;

	private List<Category> mListData;
	private LayoutInflater mInflater;
	private Context mContext;

	public CategoryAdapter(Context context, List<Category> pData) {
		mListData = pData;
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (null != mListData) {
			for (Category category : mListData) {
				count += category.getItemCount();
			}
		}

		return count;
	}

	public Product getObject(int position) {
		if (null == mListData || mListData.size() == 0 || position < 0 || position > getCount()) {
			return null;
		}
		int categroyFirstIndex = 0;
		for (Category category : mListData) {
			int size = category.getItemCount();
			int categoryIndex = position - categroyFirstIndex;
			if (categoryIndex < size) {
				return category.getObject(categoryIndex);
			}
			categroyFirstIndex += size;
		}

		return null;
	}

	@Override
	public String getItem(int position) {
		if (null == mListData || mListData.size() == 0 || position < 0 || position > getCount()) {
			return null;
		}
		int categroyFirstIndex = 0;
		for (Category category : mListData) {
			int size = category.getItemCount();
			int categoryIndex = position - categroyFirstIndex;
			if (categoryIndex < size) {
				return category.getItem(categoryIndex);
			}
			categroyFirstIndex += size;
		}

		return null;
	}

	@Override
	public int getItemViewType(int position) {
		if (null == mListData || position < 0 || position > getCount()) {
			return TYPE_ITEM;
		}

		int categroyFirstIndex = 0;

		for (Category category : mListData) {
			int size = category.getItemCount();
			int categoryIndex = position - categroyFirstIndex;
			if (categoryIndex == 0) {
				return TYPE_CATEGORY_ITEM;
			}

			categroyFirstIndex += size;
		}

		return TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int itemViewType = getItemViewType(position);
		switch (itemViewType) {
		case TYPE_CATEGORY_ITEM:
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.listview_item_header, null);
			}
			LinearLayout llHeader = (LinearLayout) convertView.findViewById(R.id.layout_header);
			TextView textView = (TextView) convertView.findViewById(R.id.header);
			Object value = getItem(position);
			if (value == null)
				llHeader.setVisibility(View.GONE);
			else {
				String itemValue = (String) getItem(position);
				textView.setText(itemValue);
			}
			break;
		case TYPE_ITEM:
			ViewHolder viewHolder = null;
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.listview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.llItem = (LinearLayout) convertView.findViewById(R.id.ll_item);
				viewHolder.content = (TextView) convertView.findViewById(R.id.content);
				viewHolder.contentIcon = (ImageView) convertView.findViewById(R.id.content_icon);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.content.setText((String) getItem(position));
			if (position % 2 != 0) {
				viewHolder.llItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_odd_color));
				viewHolder.content.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
			} else {
				viewHolder.llItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_even_color));
				viewHolder.content.setTextColor(mContext.getResources().getColor(R.color.feed_even_text_color));
			}
			break;
		}

		return convertView;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) != TYPE_CATEGORY_ITEM;
	}

	private class ViewHolder {
		LinearLayout llItem;
		TextView content;
		ImageView contentIcon;
	}

}
