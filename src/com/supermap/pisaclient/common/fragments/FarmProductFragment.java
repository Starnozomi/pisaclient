package com.supermap.pisaclient.common.fragments;

import com.supermap.pisaclient.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FarmProductFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return  inflater.inflate(R.layout.farm_product, container, false);
	
	}
}