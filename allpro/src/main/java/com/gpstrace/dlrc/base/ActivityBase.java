package com.gpstrace.dlrc.base;

import java.util.List;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.finals.NotifyFinals;
import com.gpstrace.dlrc.handler.AppHandler;
import com.gpstrace.dlrc.handler.ConnectionChangeReceiver;
import com.gpstrace.dlrc.provider.NetworkHelper;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityBase extends Activity
{
	// region fields

	protected LinearLayout mHeaderLayout;// 标题栏容器
	protected RelativeLayout mHeaderNetwork;// 标题栏网络提示

	protected RelativeLayout mFirstHeader;// 一级标题栏
	protected LinearLayout mFirstLlytBack;// 左带箭头的文字
	protected ImageView mFirstIvBack;// 左带箭头图标
	protected TextView mFirstTvBack;// 左带箭头时的文字内容
	protected TextView mFirstTvTitle;// 标题内容
	protected TextView mFirstTvDo;// 右Do内容,右只有文字
	protected ImageView mFirstIvDo;// 右Do内容，右只有图片
	protected ImageView mFirstIvExtra;// 右Do增加内容
	protected LinearLayout mFirstLlytDo;// 右Do内容，右带箭头的文字
	protected TextView mFirstTvDoTip;// 右Do内容，右带箭头时的文字内容

	protected ImageView mNewsFooter;
	protected ImageView mCourseFooter;
	protected ImageView mCommunityFooter;
	protected ImageView mAdvancedFooter;
	protected ImageView mUserFooter;

	protected TextView mTxtNewsFooter;
	protected TextView mTxtCourseFooter;
	protected TextView mTxtCommunityFooter;
	protected TextView mTxtAdvancedFooter;
	protected TextView mTxtUserFooter;

	protected RelativeLayout mRlytNewsFooter;
	protected RelativeLayout mRlytCourseFooter;
	protected RelativeLayout mRlytCommunityFooter;
	protected RelativeLayout mRlytAdvancedFooter;
	protected RelativeLayout mRlytUserFooter;

	protected RelativeLayout mRlytFooter;

	private Boolean isActive = false;// 当前界面是否激活
	private Boolean isForeground = true;// 是否是前台，默认是前台
	private ConnectionChangeReceiver mReceiver = new ConnectionChangeReceiver(
			this);// 网络监听广播

	// endregion

	// region override
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    if (newConfig.fontScale != 1)//非默认值
	        getResources();    
	    super.onConfigurationChanged(newConfig);
	}

	@Override
	public Resources getResources() {
	     Resources res = super.getResources();
	     if (res.getConfiguration().fontScale != 1) {//非默认值
	        Configuration newConfig = new Configuration();       
	        newConfig.setToDefaults();//设置默认        
	        res.updateConfiguration(newConfig, res.getDisplayMetrics()); 
	     }    
	     return res;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		isActive = true;
		init();
		registListener();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		isActive = true;
		MobclickAgent.onResume(this);

		if (!isForeground)
		{
			isForeground = true;
			// 进入到前台，执行app唤醒操作
			Message msgResume = new Message();
			msgResume.arg1 = 4;
			AppHandler.getInstance().enableNotify(msgResume,
					NotifyFinals.MainTabID);
		}

		AppHandler.getInstance().setCurrentActivity(this);

	}

	@Override
	protected void onPause()
	{
		clearReferences();
		super.onPause();
		isActive = false;
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		if (!isCheckAppOnForeground())
		{
			isForeground = false;
		}
	}

	@Override
	protected void onDestroy()
	{
		clearReferences();
		unRegist();
		super.onDestroy();
	}

	// endregion

	// region methods

	/**
	 * @初始化方法，可以被重写,权限至少是对子类可见
	 */
	protected void init()
	{
		initHeader();
		initFooter();
	}

	/**
	 * @加载Header
	 */
	protected void initHeader()
	{
		mHeaderLayout = (LinearLayout) findViewById(R.id.common_header_llyt_layout);
		mHeaderNetwork = (RelativeLayout) findViewById(R.id.common_header_rlyt_network);

		mFirstHeader = (RelativeLayout) findViewById(R.id.common_header_rlyt_first_header);
		mFirstLlytBack = (LinearLayout) findViewById(R.id.common_first_header_llyt_back);
		mFirstIvBack = (ImageView) findViewById(R.id.common_first_header_iv_back);
		mFirstTvBack = (TextView) findViewById(R.id.common_first_header_tv_back);
		mFirstTvTitle = (TextView) findViewById(R.id.common_first_header_tv_title);
		mFirstTvDo = (TextView) findViewById(R.id.common_first_header_tv_do);
		mFirstIvDo = (ImageView) findViewById(R.id.common_first_header_iv_do);
		mFirstIvExtra = (ImageView) findViewById(R.id.common_first_header_iv_extra);
		mFirstLlytDo = (LinearLayout) findViewById(R.id.common_first_header_llyt_do);
		mFirstTvDoTip = (TextView) findViewById(R.id.common_first_header_tv_do_tip);

		if (null != mFirstLlytBack)
		{
			mFirstLlytBack.setOnClickListener(mBackClickListener);
		}

		if (null != mHeaderNetwork)
		{
			mHeaderNetwork.setOnClickListener(mNetworkListener);
		}

	}

	/**
	 * @加载Footer
	 */
	protected void initFooter()
	{
		mNewsFooter = (ImageView) findViewById(R.id.common_footer_iv_news);
		mCourseFooter = (ImageView) findViewById(R.id.common_footer_iv_course);
		mCommunityFooter = (ImageView) findViewById(R.id.common_footer_iv_community);
		mAdvancedFooter = (ImageView) findViewById(R.id.common_footer_iv_advanced);
		mUserFooter = (ImageView) findViewById(R.id.common_footer_iv_user);

		mTxtNewsFooter = (TextView) findViewById(R.id.common_footer_txt_news);
		mTxtCourseFooter = (TextView) findViewById(R.id.common_footer_txt_course);
		mTxtCommunityFooter = (TextView) findViewById(R.id.common_footer_txt_community);
		mTxtAdvancedFooter = (TextView) findViewById(R.id.common_footer_txt_advanced);
		mTxtUserFooter = (TextView) findViewById(R.id.common_footer_txt_user);

		mRlytNewsFooter = (RelativeLayout) findViewById(R.id.common_footer_rlyt_news);
		mRlytCourseFooter = (RelativeLayout) findViewById(R.id.common_footer_rlyt_course);
		mRlytCommunityFooter = (RelativeLayout) findViewById(R.id.common_footer_rlyt_community);
		mRlytAdvancedFooter = (RelativeLayout) findViewById(R.id.common_footer_rlyt_advanced);
		mRlytUserFooter = (RelativeLayout) findViewById(R.id.common_footer_rlyt_user);
	}

	/**
	 * @清除Activity引用
	 */
	private void clearReferences()
	{
		Activity curActivity = AppHandler.getInstance().getCurrentActivity();
		if (this.equals(curActivity))
		{
			AppHandler.getInstance().setCurrentActivity(null);
		}
	}

	/**
	 * @注册网络监听
	 */
	private void registListener()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, filter);
	}

	/**
	 * @注册网络监听
	 */
	private void unRegist()
	{
		unregisterReceiver(mReceiver);
	}

	/**
	 * @检查网络
	 * @修改网络监听 zhaoyun 2015/3/20
	 * @return
	 */
	protected Boolean checkNetwork()
	{
		return NetworkHelper.checkConnection(getApplicationContext());
	}

	/**
	 * @根据网络状态进行视图显示
	 */
	public void changeView(Boolean isCon)
	{
		if (isCon)
		{
			if (mHeaderNetwork != null
					&& mHeaderNetwork.getVisibility() == View.VISIBLE)
			{
				mHeaderNetwork.setVisibility(View.GONE);
				refreshView();// 防止初次加载界面时就触发
			}
		} else
		{
			if (mHeaderNetwork != null
					&& mHeaderNetwork.getVisibility() == View.GONE)
			{
				mHeaderNetwork.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * @刷新视图显示
	 * @网络连接并需要更新界面时触发
	 * @有需要在网络连接时进行逻辑处理的界面需要重写此方法
	 */
	protected void refreshView()
	{
		// if (this instanceof MainActivity)
		// {
		// AppHandler.getInstance().enableUpdate(null, 2);
		// } else if (this instanceof NotesActivity)
		// {
		// AppHandler.getInstance().enableUpdate(null, 0);
		// }
	}

	/**
	 * @显示信息
	 * @param message
	 * @param timespan
	 */
	public void showToast(int messageId, int timespan)
	{
		if (isActive && !AppHandler.getInstance().IsAppDialogActive())
		{
			Toast.makeText(this, getResources().getString(messageId), timespan)
					.show();
		}
	}

	/**
	 * @显示信息
	 * @param message
	 * @param timespan
	 */
	public void showToast(int messageId)
	{
		if (isActive && !AppHandler.getInstance().IsAppDialogActive())
		{
			Toast.makeText(this, getResources().getString(messageId),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * @显示信息
	 * @param message
	 */
	public void showToastMessage(String message)
	{
		if (isActive && !AppHandler.getInstance().IsAppDialogActive())
		{
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * @总是提示信息
	 * @param message
	 * @param timespan
	 */
	public void showAlwaysToast(int messageId)
	{
		if (!AppHandler.getInstance().IsAppDialogActive())
		{
			Toast.makeText(this, getResources().getString(messageId),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * @获取激活状态
	 * @return
	 */
	public Boolean getActiveState()
	{
		return this.isActive;
	}

	/**
	 * @检查应用程序前后台
	 * @return
	 */
	public boolean isCheckAppOnForeground()
	{
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty())
		{
			ComponentName topActivity = tasks.get(0).topActivity;
			if (topActivity.getPackageName().equals(this.getPackageName()))
			{
				return true;
			} else
			{
				return false;
			}
		}
		return true;
	}

	// endregion

	// region events

	/**
	 * @返回事件
	 */
	OnClickListener mBackClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			onBackPressed();
		}
	};

	/**
	 * @打开网络设置事件
	 */
	OnClickListener mNetworkListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			try
			{
				Intent intent = null;
				// 判断手机系统的版本 即API大于10 就是3.0或以上版本
				if (android.os.Build.VERSION.SDK_INT > 10)
				{
					intent = new Intent(
							android.provider.Settings.ACTION_WIFI_SETTINGS);
				} else
				{
					intent = new Intent();
					ComponentName component = new ComponentName(
							"com.android.settings",
							"com.android.settings.WirelessSettings");
					intent.setComponent(component);
					intent.setAction("android.intent.action.VIEW");
				}
				startActivity(intent);
			} catch (Exception e)
			{
				Toast.makeText(ActivityBase.this, "请检查网络设置", Toast.LENGTH_SHORT)
						.show();
			}

		}
	};

	// endregion
}
