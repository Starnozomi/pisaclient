package com.supermap.pisaclient.adapter;

import java.util.List;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.grouplist.ContactItemInterface;
import com.supermap.pisaclient.grouplist.ContactListAdapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class CityAdapter extends ContactListAdapter
{

	public CityAdapter(Context _context, int _resource,
			List<ContactItemInterface> _items)
	{
		super(_context, _resource, _items);
	}

	public void populateDataForRow(View parentView, ContactItemInterface item,
			int position)
	{
		View infoView = parentView.findViewById(R.id.infoRowContainer);
		TextView nicknameView = (TextView) infoView
				.findViewById(R.id.cityName);

		nicknameView.setText(item.getDisplayInfo());
	}

}
