package com.gpstrace.dlrc.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.gpstrace.dlrc.provider.Utils;

import android.util.Log;

/**
 * @下载器
 * @author LC125
 *
 */
public class Downloader
{
	//region fields
	
    private static Downloader mInstance;
	
	//endregion
    
    //region methods
    
	/**
	 * @单件模式
	 * @return
	 */
    public static Downloader getInstance()
    {
    	if (mInstance==null)
		{
			mInstance=new Downloader();
		}
    	return mInstance;
    }
    
	/**
	 * @下载文件
	 * @param fileStr
	 * @param urlString
	 * @return
	 */
	public Boolean downFile(String fileStr, String urlString)
    {
		final File cacheFile = new File(fileStr);

		// 判断文件是否存在，若存在，则删除

		if (cacheFile.exists())
		{
			cacheFile.delete();
		}

		Utils.disableConnectionReuseIfNecessary();
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;

		try
		{
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			final InputStream in = new BufferedInputStream(
					urlConnection.getInputStream(), Utils.IO_BUFFER_SIZE);
			out = new BufferedOutputStream(new FileOutputStream(cacheFile),
					Utils.IO_BUFFER_SIZE);

			int b;
			while ((b = in.read()) != -1)
			{
				out.write(b);
			}

			return true;

		} catch (final Exception e)
		{

		} finally
		{
			if (urlConnection != null)
			{
				urlConnection.disconnect();
			}
			if (out != null)
			{
				try
				{
					out.close();
				} catch (final IOException e)
				{

				}
			}
		}

		return false;
	}
	
	
	/**
	 * @下载文件
	 * @param fileStr
	 * @param urlString 检查urlString 是否以https开头 ，兼容http和https两种协议
	 * @return
	 */
	public Boolean downloadFile(String fileStr, String urlString)
    {
		final File cacheFile = new File(fileStr);

		// 判断文件是否存在，若存在，则删除

		if (cacheFile.exists())
		{
			cacheFile.delete();
		}

		Utils.disableConnectionReuseIfNecessary();
		HttpURLConnection httpConnection = null;
		HttpsURLConnection httpsConnection = null;
		InputStream in;
		BufferedOutputStream out = null;

		try
		{
			final URL url = new URL(urlString);
			if(urlString.startsWith("https")){
				httpsConnection=(HttpsURLConnection) url.openConnection();
				in = new BufferedInputStream(	httpsConnection.getInputStream(), Utils.IO_BUFFER_SIZE);
			}else{
				httpConnection = (HttpURLConnection) url.openConnection();
				in = new BufferedInputStream(	httpConnection.getInputStream(), Utils.IO_BUFFER_SIZE);
			}

			out = new BufferedOutputStream(new FileOutputStream(cacheFile),	Utils.IO_BUFFER_SIZE);

			int b;
			while ((b = in.read()) != -1)
			{
				out.write(b);
			}

			return true;

		} catch (final Exception e)
		{
			Log.d("XXX", "  ");

		} finally
		{
			if (httpConnection != null)
			{
				httpConnection.disconnect();
			}
			if(httpsConnection!=null){
				httpsConnection.disconnect();
			}
			if (out != null)
			{
				try
				{
					out.close();
				} catch (final IOException e)
				{

				}
			}
		}

		return false;
	}

	// endregion

}
