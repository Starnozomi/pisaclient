package com.supermap.pisaclient.ui;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.f;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.SubjectDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.entity.Subject;
import com.supermap.pisaclient.polites.GestureImageView;

/**
 * 查看大图的Activity界面。
 * 
 */
public class CropImageActivity extends Activity implements
		OnPageChangeListener {

	/**
	 * 用于管理图片的滑动
	 */
	private ViewPager viewPager;
	
	private ImageLoader mImageLoader;

	/**
	 * 显示当前图片的页数
	 */
	private TextView pageText;
	
	private int imagePosition = 0;
	
	private String [] image_deses = null;
	
	private CustomProgressDialog mPdDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.crop_image_main);
		mImageLoader = new ImageLoader(CropImageActivity.this);
		Intent intent = getIntent();
		imagePosition = intent.getIntExtra("image_position", 0);
		image_deses = intent.getStringArrayExtra("image_deses");
		pageText = (TextView) findViewById(R.id.crop_page_text);
		viewPager = (ViewPager) findViewById(R.id.crop_view_pager);
		ViewPagerAdapter adapter = new ViewPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(imagePosition);
		viewPager.setOnPageChangeListener(this);
		viewPager.setEnabled(false);
		// 设定当前的页数和总页数
		pageText.setText((imagePosition + 1) + "/" + image_deses.length);
		mPdDialog = CustomProgressDialog.createDialog(this);
		mPdDialog.setMessage("加载中...");
	}

	/**
	 * ViewPager的适配器
	 * 
	 */
	class ViewPagerAdapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
//			String imagePath = getImagePath(Images.imageUrls[position]);
//			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//			if (bitmap == null) {
//				bitmap = BitmapFactory.decodeResource(getResources(),
//						R.drawable.empty_photo);
//			}
			View view = LayoutInflater.from(CropImageActivity.this).inflate(
					R.layout.crop_image_pager, null);
			GestureImageView iv = (GestureImageView) view
					.findViewById(R.id.iv_crop_pager);
			
			String url = CommonImageUtil.getThumbnailImageUrl(image_deses[position]);
			mImageLoader.DisplayImage(url, iv, false);
			//没有起提示作用
			new LoadBigPicture(iv, CommonImageUtil.getImageUrl(image_deses[position])).execute();
			container.addView(view);
			return view;
		}
		
		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			// TODO Auto-generated method stub
			
//			View view = (View) object;
//			GestureImageView iv = (GestureImageView) view
//					.findViewById(R.id.iv_crop_pager);
//			new LoadBigPicture(iv, CommonImageUtil.getImageUrl(image_deses[position])).execute();
			super.setPrimaryItem(container, position, object);
		}
			
		@Override
		public int getCount() {
			if (image_deses!=null) {
				return image_deses.length;
			}
			else {
				return 0;
			}
			
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);
		}

	}

	/**
	 * 获取图片的本地存储路径。
	 * 
	 * @param imageUrl
	 *            图片的URL地址。
	 * @return 图片的本地存储路径。
	 */
	private String getImagePath(String imageUrl) {
		int lastSlashIndex = imageUrl.lastIndexOf("/");
		String imageName = imageUrl.substring(lastSlashIndex + 1);
		String imageDir = Environment.getExternalStorageDirectory().getPath()
				+ "/PhotoWallFalls/";
		File file = new File(imageDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String imagePath = imageDir + imageName;
		return imagePath;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int currentPage) {
		// 每当页数发生改变时重新设定一遍当前的页数和总页数
		pageText.setText((currentPage + 1) + "/" + image_deses.length);
	}
	
	
	 private class LoadBigPicture extends AsyncTask<String, Integer, List<Subject>> {
		   private GestureImageView iv;
		   private String url;
		   public LoadBigPicture(View view,String url){
			   this.iv = (GestureImageView) view;
			   this.url = url;
		   }
		 	
			@Override
			protected void onPreExecute() {
				if (!mPdDialog.isShowing()){
					mPdDialog.show();
				}
			}

			@Override
			protected List<Subject> doInBackground(String... arg0) {
				mImageLoader.DisplayImage(url, iv, false);
				return null;
			}

			@Override
			protected void onPostExecute(List<Subject> result) {
				if (mPdDialog.isShowing()){
					mPdDialog.dismiss();
				}
			}

		}

}