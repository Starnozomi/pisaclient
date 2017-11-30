package com.supermap.pisaclient.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.widget.ImageView;

import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;

public class ImageLoader {

	private MemoryCache memoryCache = new MemoryCache();
	private AbstractFileCache fileCache;
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService;

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	public void DisplayToRoundBitmap(String url, ImageView imageView, boolean isLoadOnlyFromCache) {
		imageViews.put(imageView, url);

		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			imageView.setImageBitmap(CommonUtil.toRoundBitmap(bitmap));
		} else if (!isLoadOnlyFromCache) {
			queueToRoundPhoto(url, imageView);
		}
	}

	public void DisplayImage(String url, ImageView imageView, boolean isLoadOnlyFromCache) {
		imageViews.put(imageView, url);

		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
		{
			/*int degree= CommonImageUtil.getBitmapDegree(url);
			if(degree!=0)
			{
				bitmap= CommonImageUtil.rotateBitmapByDegree(bitmap,degree);
			}		*/
			imageView.setImageBitmap(bitmap);	
		}			
		else if (!isLoadOnlyFromCache) {
			queuePhoto(url, imageView);
		}
	}
	
	private void queueToRoundPhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosToRoundBLoader(p));
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		
		boolean isSmall = isThumbnailImag(url);
		File f = fileCache.getFile(url);
		Bitmap b = null;
		if (f != null && f.exists()) {
//			b = decodeFile(f);
			b = decodeFile(f,isSmall);
		}
		if (b != null) {
			return b;
		}
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
//			bitmap = decodeFile(f);
			bitmap = decodeFile(f,isSmall);
			return bitmap;
		} catch (Exception ex) {
			Log.e("", "getBitmap catch Exception...\nmessage = " + ex.getMessage());
			return null;
		}
	}
	
	private boolean isThumbnailImag(String url){
		if(url.indexOf("thumbnail")==-1){
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		//根据长宽中较大的压缩比 来压缩
		if (width> height && width > reqWidth) {  
			inSampleSize = (int) (width / reqWidth);  
        } else if (width < height && height > reqHeight) {  
        	inSampleSize = (int) (height / reqHeight);  
        }  
        if (inSampleSize <= 0)  
            inSampleSize= 1;  
		
		if (inSampleSize<=1) {
			inSampleSize =1;
		}

		return inSampleSize;
	}
	
	
	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSizeForproduct(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		//横着拍照的时候 压缩失真比较大
		if (height > reqHeight || width > reqWidth) {

			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

//		//根据长宽中较大的压缩比 来压缩
//		if (width> height && width > reqWidth) {  
//			inSampleSize = (int) (width / reqWidth);  
//        } else if (width < height && height > reqHeight) {  
//        	inSampleSize = (int) (height / reqHeight);  
//        }  
//        if (inSampleSize <= 0)  
//            inSampleSize= 1;  
//		
//		if (inSampleSize<=1) {
//			inSampleSize =1;
//		}

		return inSampleSize;
	}
	/**
	 * 根据路径获得图片并压缩返回bitmap
	 * 
	 * @param filePath	
	 * @return
	 */
	public  Bitmap getSmallBitmap(String filePath) {
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, 480, 800);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}
	
	/**
	 * 根据路径获得图片并压缩返回bitmap
	 * 
	 * @param filePath	
	 * @return
	 */
	public  Bitmap getSmallBitmapForProduct(String filePath) {
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSizeForproduct(options, 480, 800);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}
	
	private Bitmap decodeSmallFile(File f) {
		Bitmap bitmap = null;
		try {
			bitmap = cutBmp(BitmapFactory.decodeStream(new FileInputStream(f)));
//			bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
		} catch (Exception e) {
			return null;
		} 
		return bitmap;
	}
	
	private Bitmap decodeBigFile(File f) {
		
		
		return compressByQuality(getSmallBitmap(f.getAbsolutePath()), 60);
	
//		Bitmap bitmap = null;
//		try {
//			bitmap =BitmapFactory.decodeStream(new FileInputStream(f));
//		} catch (Exception e) {
//			return null;
//		} 
//		return bitmap;
	}
	
	/**
	 * 正方形的bmp
	 * @param bmp
	 * @return
	 */
	public Bitmap cutBmp(Bitmap bmp)
    {
		Bitmap result;
		int w = bmp.getWidth();//输入长方形宽
		int h = bmp.getHeight();//输入长方形高
		int nw;//输出正方形宽
		if(w > h)
		{
		    //宽大于高
		    nw = h;
		    result = Bitmap.createBitmap(bmp, (w - nw) / 2, 0, nw, nw);
		}else{
		  //高大于宽
		    nw = w;
		    result = Bitmap.createBitmap(bmp, 0 , (h - nw) / 2, nw, nw);
		}
		return result;
    }
	
	private Bitmap decodeFile(File f) {
		
			try {
					BitmapFactory.Options o = new BitmapFactory.Options();
					o.inJustDecodeBounds = true;
					BitmapFactory.decodeStream(new FileInputStream(f), null, o);
					final int REQUIRED_SIZE = 100;
					int width_tmp = o.outWidth, height_tmp = o.outHeight;
					int scale = 1;
					while (true) {
						if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
							break;
						width_tmp /= 2;
						height_tmp /= 2;
						scale *= 2;
					}
		
					BitmapFactory.Options o2 = new BitmapFactory.Options();
					o2.inSampleSize = scale;
					return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
						
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
//					return compressByQuality(getSmallBitmap(f.getAbsolutePath()), 60);
	}
	
	private Bitmap decodeFile(File f ,Boolean isSmall) {
		if (isSmall) {
			return decodeSmallFile(f);
		}
		else {
			return decodeBigFile(f);
		}
	}
	
	/**
	 * 
	 * @param bitmap
	 * @param maxSize 80K
	 * @return
	 */
	
	private Bitmap compressByQuality(Bitmap bitmap, int maxSize) {
		memoryCache.addCheck(bitmap);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 100;
		bitmap.compress(CompressFormat.JPEG, quality, baos);
		System.out.println("图片压缩前大小：" + baos.toByteArray().length + "byte");
		while (baos.toByteArray().length / 1024 > maxSize) {
			quality -= 10;
			baos.reset();
			bitmap.compress(CompressFormat.JPEG, quality, baos);
		}
		System.out.println("图片压缩后大小：" + baos.toByteArray().length + "byte");
		memoryCache.addCheck(baos.toByteArray().length);
		bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
		return bitmap;
	}

	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}
	
	class PhotosToRoundBLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosToRoundBLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			bmp = CommonUtil.toRoundBitmap(bmp);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}


	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);

		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			Log.e("", "CopyStream catch Exception...");
		}
	}
	
	/**
	 * 图片压缩到200k以内
	 * @param file
	 * @return
	 */
	public InputStream getPicInputStream(File file){
		Bitmap bm;
		InputStream is;
		if (file!=null) {
			bm = getSmallBitmap(file.getAbsolutePath());//压缩分辨率
			is =  compress2InputStream(bm, 200);//压缩质量
			if (bm!=null) {
				bm.recycle();
				bm = null;
			}
			return is;
		}
		else {
			 return null;
		}
	}
	
	private InputStream compress2InputStream(Bitmap bitmap, int maxSize) {
		memoryCache.addCheck(bitmap);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 100;
		bitmap.compress(CompressFormat.JPEG, quality, baos);
		System.out.println("图片压缩前大小：" + baos.toByteArray().length + "byte");
		while (baos.toByteArray().length / 1024 > maxSize) {
			quality -= 10;
			baos.reset();
			bitmap.compress(CompressFormat.JPEG, quality, baos);
		}
		System.out.println("图片压缩后大小：" + baos.toByteArray().length + "byte");
		memoryCache.addCheck(baos.toByteArray().length);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());  
        return is;  
	}
}