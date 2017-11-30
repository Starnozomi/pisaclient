package com.supermap.pisaclient.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.supermap.pisaclient.http.HttpHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

public class CommonImageUtil {

	public String filePath;
	private static CommonImageUtil mCommonImageUtil;

	public static Activity mActivity;

	public String mImagePath;

	public CommonImageUtil() {
		filePath = FileManager.getSaveFilePath() + "capture";
	}

	public static String getImageUrl(String name) {
		if (name != null) {
			if (name.startsWith("/")) {
				return HttpHelper.getBaseUrl() + name;
			} 
			else if(name.startsWith("http://"))
			{
				return name;
			}
			else {
				return HttpHelper.getBaseUrl() + "/" + name;
			}
		}
		return "";
	}
	
	public static String getThumbnailImageUrl(String name) {
		if (name != null) {
			if (name.startsWith("/")) 
			{
				return HttpHelper.getBaseUrl() + getThumbnailName(name);
			} 
			else if(name.startsWith("http://"))
			{
				int index=name.lastIndexOf(".");
				if(index!=-1)
				{
					String temp = name.substring(0, index);
					String temp1 = temp+"_thumbnail";
					String temp2 = name.substring(index,name.length());
					return temp1+temp2;
				}
				else
				{
					return name;
				}
			}
			else
			{
				return HttpHelper.getBaseUrl() + "/" + getThumbnailName(name);
			}
		}
		return "";
	}
	
	private static String getThumbnailName(String name){
		int len = name.length();
		int index = name.indexOf('.');
		if(index!=-1){
			String temp = name.substring(0, index);
			String temp1 = temp+"_thumbnail";
			String temp2 = name.substring(index,len);
			return temp1+temp2;
		}
		else {
			return name;
		}
		
	}
	
	/**
	 * 根据指定的图像路径和大小来获取缩略图
	 * 此方法有两点好处：
	 *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
	 *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
	 *        用这个工具生成的图像不会被拉伸。
	 * @param imagePath 图像的路径
	 * @param width 指定输出图像的宽度
	 * @param height 指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	public static CommonImageUtil getInstance(Activity activity) {
		mActivity = activity;
		if (mCommonImageUtil == null) {
			mCommonImageUtil = new CommonImageUtil();
		}
		return mCommonImageUtil;
	}

	public String getPicturePath() {
		return mImagePath;
	}

	/**
	 * 拍照时需要传一个路径，存放图片，如果是从相册选择则不需要地址
	 * @param filepath
	 *        拍照时存放图片地址
	 * 
	 */
	public void getPicture(String name) {
		mImagePath = filePath + name + ".jpg";
		String[] items = new String[] { "拍照", "从手机相册选择" };
		new AlertDialog.Builder(mActivity).setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					File file = new File(mImagePath);
					if (file.exists()) {
						file.delete();
					}
					Uri uri = Uri.fromFile(file);
					Intent intent = new Intent();
					// 指定开启系统相机的Action
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					// // 设置系统相机拍摄照片完成后图片文件的存放地址
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					mActivity.startActivityForResult(intent, Constant.IMAGE_CAPTURE_REQUEST);
					break;
				case 1:
					Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
					getImage.addCategory(Intent.CATEGORY_OPENABLE);
					getImage.setType("image/*");
					mActivity.startActivityForResult(getImage, Constant.IMAGE_GET_REQUEST);
					break;
				}
			}
		}).show();
	}

	public Bitmap decodeFile(String pathName, int targetWidth, int targetHeight) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
		// 得到图片的宽度、高度；
		int imgWidth = opts.outWidth;
		int imgHeight = opts.outHeight;
		if (imgWidth > targetWidth || imgHeight > targetHeight) {
			int widthScale = Math.round((float) imgWidth / (float) targetWidth);
			int heightScale = Math.round((float) imgHeight / (float) targetHeight);
			opts.inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		// 设置好缩放比例后，加载图片进内容；
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(pathName, opts);
		return bitmap;
	}
	
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

	public Bitmap compressByQuality(Bitmap bitmap, int maxSize) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 100;
		bitmap.compress(CompressFormat.JPEG, quality, baos);
//		System.out.println("图片压缩前大小：" + baos.toByteArray().length + "byte");
		while (baos.toByteArray().length / 1024 > maxSize) {
			quality -= 10;
			baos.reset();
			bitmap.compress(CompressFormat.JPEG, quality, baos);
		}
//		System.out.println("图片压缩后大小：" + baos.toByteArray().length + "byte");
		bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
		return bitmap;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
	
	/**
	 * 读取图片的旋转的角度
	 *
	 * @param path   图片绝对路径
	 * @return  图片的旋转角度
	 */
	public static int getBitmapDegree(String path) {
	    int degree = 0;
	    try {
	        // 从指定路径下读取图片，并获取其EXIF信息
	        ExifInterface exifInterface = new ExifInterface(path);
	        // 获取图片的旋转信息
	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
	                ExifInterface.ORIENTATION_NORMAL);
	        switch (orientation) {
	        case ExifInterface.ORIENTATION_ROTATE_90:
	            degree = 90;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_180:
	            degree = 180;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_270:
	            degree = 270;
	            break;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return degree;
	}
	
	public static void saveNewBitmap(String filePath,Bitmap bitmap)
	{
		File file=new File(filePath);
		if(file.exists())
		{
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}	
		FileOutputStream fOut=null;
		try {	
			fOut=new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();
			// 如果图片还没有回收，强制回收
			if (!bitmap.isRecycled()) 
			{
				bitmap.recycle();
				System.gc();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static Bitmap rotateBitmap(Bitmap bm,int degree)
	{
		Matrix m=new Matrix();
		m.setRotate(degree, bm.getWidth()/2, bm.getHeight()/2);
		float targetX,targetY;
		if(degree==90)
		{
			targetX=bm.getHeight();
			targetY=0;
		}
		else
		{
			targetX=bm.getHeight();
			targetY=bm.getWidth();
		}
		final float[] values=new float[9];
		m.getValues(values);
		
		float x1=values[Matrix.MTRANS_X];
		float y1=values[Matrix.MTRANS_Y];
		
		m.postTranslate(targetX-x1,targetY-y1);
		Bitmap bm1=Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
	    Canvas canvas = new Canvas(bm1);
	    canvas.drawBitmap(bm, m, paint);

	    return bm1;
	}
	
	/**
	 * 将图片按照某个角度进行旋转
	 *
	 * @param bm  需要旋转的图片
	 * @param degree  旋转角度
	 * @return 旋转后的图片
	 */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		//旋转图片 动作
		/*Matrix matrix = new Matrix();;
        matrix.postRotate(degree);
        System.out.println("angle2=" + degree);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0,
        		bm.getWidth(), bm.getHeight(), matrix, true);
		return resizedBitmap;*/
	    Bitmap returnBm = null;  
	    // 根据旋转角度，生成旋转矩阵
	    Matrix matrix = new Matrix();
	    //matrix.postRotate(degree,bm.getWidth()/2,bm.getHeight()/2);
	    matrix.postRotate(degree);
	    try {
	        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
	        returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	    	//returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getHeight(),bm.getWidth(),  matrix, true);
	    } catch (OutOfMemoryError e) {
	    }
	    catch(IllegalArgumentException e)
	    {}
	    if (returnBm == null) {
	        returnBm = bm;
	    }
	    if (bm != returnBm) {
	        bm.recycle();
	    }
	    return returnBm;
	}
}
