package com.supermap.pisaclient.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileManager {

	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			String path = CommonUtil.getRootFilePath() + "supermap_pisa/files/";
			File file = new File(path);
			if (!file.exists())
				file.mkdirs();
			return path;
		} else {
			return CommonUtil.getRootFilePath() + "supermap_pisa/files/";
		}
	}
	
	public static String getSaveImageCacheDir(){
		return getSaveFilePath()+"image/";
	}
	
	public static String getSaveUserCacheDir(){
		return getSaveFilePath()+"user_info/";
	}
	
	public static String getSaveMapCacheDir(){
		return getSaveFilePath()+"MapCache/";
	}
	
	public static String getSaveFarmsCacheDir(){
		return getSaveFilePath()+"Farms/";
	}
	
	public static String getSaveCropsCacheDir(){
		return getSaveFilePath()+"Crops/";
	}
	
	public static String getSaveAdvsCacheDir(){
		return getSaveFilePath()+"Advs/";
	}

	public static String getData(String filePath) {
		try {
			InputStream fis = FileHelper.readFile(filePath);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			br.close();
			isr.close();
			fis.close();
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}
}
