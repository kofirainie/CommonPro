package com.gpstrace.dlrc;

import android.app.Application;

import com.gpstrace.dlrc.handler.LoadHandler;

/**
 * @应用程序类
 * @author kofirainie
 */
public class CrashApplication extends Application
{
	@Override
	public void onCreate()
	{
		try
		{
			Class.forName("android.os.AsyncTask");
		} catch (Throwable ignore)
		{
		}

		super.onCreate();
		this.start();
	}

	/**
	 * @应用程序启动类，在该方法中加载应用程序配置
	 */
	private void start()
	{
		LoadHandler.getInstance().Load(getApplicationContext());
	}

//	@Override
//	protected void attachBaseContext(Context base) {
//		super.attachBaseContext(base);
//		MultiDex.install(this) ;
//	}
}
