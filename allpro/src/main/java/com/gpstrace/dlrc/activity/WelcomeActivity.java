package com.gpstrace.dlrc.activity;

import com.gpstrace.dlrc.base.ActivityBase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @启动界面
 * @author kofirainie
 */
public class WelcomeActivity extends ActivityBase
{
	// region methods

	/**
	 * @重写初始化方法
	 */
	protected void init()
	{
		SharedPreferences preferences = getSharedPreferences("configInfo", 0);

		boolean isFirstUse = preferences.getBoolean("isFirstUse", true);

		/**
		 * 如果用户不是第一次使用则直接调转到显示界面,否则调转到引导界面
		 */
		if (isFirstUse)
		{
			startActivity(new Intent(WelcomeActivity.this, StartActivity.class));
		} else
		{
			startActivity(new Intent(WelcomeActivity.this, StartActivity.class));
		}
		finish();

		Editor editor = preferences.edit();
		editor.putBoolean("isFirstUse", false);
		editor.commit();
	}

	// endregion
}
