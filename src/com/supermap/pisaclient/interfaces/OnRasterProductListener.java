package com.supermap.pisaclient.interfaces;

import com.supermap.pisaclient.service.RasterProduct;

public interface OnRasterProductListener {
	void AfterProductChanged(RasterProduct rasterProduct);
	void AfterProductShow(RasterProduct rasterProduct);
	void AfterProductHide();
}
