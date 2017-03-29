package com.gpstrace.dlrc.handler;

import android.content.Context;

import com.gpstrace.dlrc.config.AppConfig;
import com.gpstrace.dlrc.provider.CacheHelper;
import com.gpstrace.dlrc.provider.HttpProvider;

/**
 * @初始加载类
 * @author kofirainie
 */
public class LoadHandler
{
	// region field

	private static LoadHandler sInstance;

	// endregion

	/**
	 * @单件方法
	 * @return
	 */
	public static LoadHandler getInstance()
	{
		if (sInstance == null)
		{
			sInstance = new LoadHandler();
		}
		return sInstance;
	}
	
	/**
	 * @加载方法
	 * @param context
	 */
	public void Load(Context context)
	{
		AppConfig.getInstance().init();
		CrashHandler.getInstance().init(context);
		AppHandler.getInstance().setContext(context);
		AppHandler.getInstance().setImageLoader();				
		HttpProvider.setAgentString(AppHandler.getInstance().getAgentString(
				context));
		CacheHelper.getInstance().setContext(context);
		AppHandler.getInstance().initUmengConfig();
	}
}
