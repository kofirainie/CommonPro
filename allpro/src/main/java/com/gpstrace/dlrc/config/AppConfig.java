package com.gpstrace.dlrc.config;

import java.io.File;

import com.gpstrace.dlrc.finals.ConfigFinals;

import android.os.Environment;
import android.util.Log;

public class AppConfig
{
	// region fields

	private String mFolder = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/" + ConfigFinals.FOLDER_ROOT + "/";// 应用程序配置目录

	private String mCrashFolder = mFolder + ConfigFinals.FOLDER_CRASH + "/";// 异常存储目录

	private String mCacheFolder = mFolder + ConfigFinals.FOLDER_CACHE + "/";// 缓存目录

	private String mDownloadFolder = mFolder + ConfigFinals.FOLDER_DOWNLOAD
			+ "/";// 下载目录
	
	private String mPhotoFolder = mFolder + ConfigFinals.FOLDER_CACHE + "/"
			+ ConfigFinals.FOLDER_PHOTO + "/";// 拍照缓存

	private static AppConfig sInstance;
	
	// endregion

	// region propertys

	/**
	 * @获取应用程序配置目录
	 * @return
	 * @返回应用程序配置目录
	 */
	public String getFolder()
	{
		return mFolder;
	}

	/**
	 * @获取异常存储文件目录
	 * @return
	 */
	public String getCrashFolder()
	{
		return mCrashFolder;
	}

	/**
	 * @获取缓存目录
	 * @return
	 */
	public String getCacheFolder()
	{
		return mCacheFolder;
	}

	/**
	 * @获取缓存目录
	 * @return
	 */
	public String getDownloadFolder()
	{
		return mDownloadFolder;
	}

	/**
	 * @获取拍照缓存目录
	 * @return
	 */
	public String getPhotoFolder()
	{
		return mPhotoFolder;
	}

	// endregion

	// region methods

	/**
	 * @获取该类的单件实例
	 * @return
	 */
	public static AppConfig getInstance()
	{
		if (sInstance == null)
		{
			sInstance = new AppConfig();
		}
		return sInstance;
	}

	/**
	 * @初始化方法
	 */
	public void init()
	{
		createFolder();
	}

	/**
	 * @创建目录
	 */
	private void createFolder()
	{
		isFolderExists(mFolder);
		isFolderExists(mCrashFolder);
		isFolderExists(mCacheFolder);
		isFolderExists(mDownloadFolder);
		isFolderExists(mPhotoFolder);
		
		isfile(mFolder);
		isfile(mCrashFolder);
		isfile(mCacheFolder);
		isfile(mDownloadFolder);
		isfile(mPhotoFolder);
	}

	/**
	 * @检查文件夹是否存在，并创建
	 * @param strFolder
	 * @@目录路径
	 * @return
	 */
	private boolean isFolderExists(String strFolder)
	{
		File file = new File(strFolder);

		if (!file.exists())
		{
			if (file.mkdirs())
				return true;
			else
				return false;
		}
		return true;
	}
	
	private void isfile(String strFolder){
		File file = new File(strFolder);
		boolean isfile = file.exists();
		if(isfile){
			Log.e("jij", "onChannelClosed(): Successfully closed the channel");
		}else {
			Log.e("hu", "onChannelClosed(): Successfully closed the channel");
		}
	}

	/**
	 * @是否是缓存目录
	 * @param folder
	 * @return
	 */
	public Boolean isCacheFolder(String folder)
	{
		if (folder.equals(mFolder + ConfigFinals.FOLDER_CACHE))
		{
			return true;
		} else
		{
			return false;
		}
	}

	// endregion
}
