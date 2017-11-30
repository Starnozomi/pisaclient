/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @LocalTile.java - 2014-10-13 下午2:34:08
 */

package com.supermap.pisaclient.common;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class LocalTile {
	
	private WriteThread mCache;
	private HashMap<String, String> mFileMap;
	private Boolean mIsExit = false;
	private String mDir;
	private Map<String, String> cache = Collections
			.synchronizedMap(new LinkedHashMap<String, String>(10, 1.5f, true));
	public LocalTile(){
//		mCache = new WriteThread();
	}
	
	public void setExit(){
		mIsExit = true;
	}

	public String getTileFile(String url, String dir, int col, int row, long scale, String ext) {
		this.mDir = dir;
		int intCol = col / 128;
		int intRow = row / 128;
		String file = FileManager.getSaveMapCacheDir() + dir + "/" + scale + "/" + intRow + "/" + intCol + "/" + row + "x"
				+ col + "." + ext;
		
		url += "dir=" + dir + "&scale=" + scale + "&row=" + row + "&col=" + col + "&format=" + ext;
		if ((dir!=null)&&(dir.equals("Sattelite"))) {
			System.out.println(url);
		}
		return url;
//		if (!FileHelper.fileIsExist(file)) {
//			System.out.println("no file");
//			url += "dir=" + dir + "&scale=" + scale + "&row=" + row + "&col=" + col + "&format=" + ext;
//			mapCache.addData(file, url);
////			mFileMap = new HashMap<String, String>();
////			mFileMap.put(file,url);
////			mCache.addFile(mFileMap);
//			return url;
//		} else {
//			System.out.println("yes file");
//			return file;
//		}
	}
	
	/**
	 * 根据路径获得图片并压缩返回bitmap
	 * 
	 * @param filePath	
	 * @return
	 */
	public  boolean isDowned(String filePath) {
		boolean result=false;
		Bitmap mBitmap = null;
		
		mBitmap = BitmapFactory.decodeFile(filePath);
		if (mBitmap!=null) {
			mBitmap.recycle();
			mBitmap = null;
			result = true;
		}
		return result;
	}
	

	private class WriteThread extends Thread {

		private ConcurrentLinkedQueue<HashMap<String, String>> mFileQueue	= new ConcurrentLinkedQueue<HashMap<String,String>>();
		private String file;
		private String url;

		public WriteThread() {
			this.start();
		}

		public	void addFile(HashMap<String, String> fileMap){
			synchronized(mFileQueue){
				mFileQueue.add(fileMap);
			}
			
		}
		
		public	HashMap<String, String> getFile(){
			synchronized (mFileQueue) {
				return mFileQueue.poll();
			}
			
		}
		
		public boolean isEmpty(){
			synchronized (mFileQueue) {
				return mFileQueue.isEmpty();
			}
		}
		@Override
		public void run() {
			HashMap<String, String> temp;
			Iterator<Entry<String, String>> iterator;
			String file = null;
			String url	= null;
			while(!mIsExit){
				
				while((!mIsExit)&&(!isEmpty())){
					temp = getFile();
					iterator =  temp.entrySet().iterator();
					if (iterator.hasNext()) {
						file = iterator.next().getKey();
						url = temp.get(file);
					}
					writeFile(file,url);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			System.out.println("exit write "+mDir+"Thread");
		}

	}

	public boolean writeFile(String file, String url) {
		String tempFileName = getTempFileName(file);
		File tempFile = new File(tempFileName);
		File toFile = new File(file);
		try {
			URL u = new URL(url);
			URLConnection conn = u.openConnection();
			InputStream is = conn.getInputStream();
			
			FileHelper.writeFile(tempFileName, is);
			tempFile.renameTo(toFile);
			return true;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return false;
	}
	
	public String getTempFileName(String file){
		String temp = null;
		String filePath = file.split("\\.")[0];
		temp = filePath+".temp";
		return temp;
	}

}
