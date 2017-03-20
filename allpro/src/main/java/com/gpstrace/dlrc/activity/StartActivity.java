package com.gpstrace.dlrc.activity;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.base.ActivityBase;
import com.gpstrace.dlrc.handler.AppHandler;
import com.gpstrace.dlrc.model.AppException;
import com.gpstrace.dlrc.model.RequestSplash;
import com.gpstrace.dlrc.model.ResponseCheckVersion;
import com.gpstrace.dlrc.model.SplashContent;
import com.gpstrace.dlrc.model.SplashSummary;
import com.gpstrace.dlrc.provider.NetworkHelper;
import com.gpstrace.dlrc.provider.Utils;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

/**
 * @闪屏启动
 * @author YunZ
 *
 */
public class StartActivity extends ActivityBase
{
	// region fields

	private ImageView splashView;

	// endregion

	// region methods

	/**
	 * @重写初始化方法
	 */
	protected void init()
	{
		final View view = View.inflate(this, R.layout.activity_start_layout,
				null);
		splashView = (ImageView) view.findViewById(R.id.start_iv_splash);
		setSplashView();
		setContentView(view);

		// 渐变展示启动屏
		ResponseCheckVersion version = AppHandler.getInstance().getVersion(false); //安卓版本号信息
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		if (null != version) {
			animation.setDuration(Long.valueOf((int) (version.getMain_img_time() * 1000)));
		} else {
			animation.setDuration(2000);
		}
		splashView.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0)
			{
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationStart(Animation animation)
			{
			}

		});
	}

	/**
	 * 跳转到主界面
	 */
	private void redirectTo()
	{
		startDown();
//		Intent intent = new Intent(this, MainTabActivity.class);
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * @开始加载闪屏页图片
	 */
	private void startDown()
	{
		if (NetworkHelper.checkConnection(getApplicationContext()))
		{
			new Thread()
			{
				public void run()
				{
					downSplash();
				}
			}.start();
		}
	}

	/**
	 * @下载闪屏图片
	 */
	private void downSplash()
	{
		try
		{
			RequestSplash splash = new RequestSplash();

			SplashSummary summary = AppHandler.getInstance()
					.getSplashSummary(splash);
			if (summary != null)
			{
				SplashContent content = AppHandler.getInstance()
						.getSplashArticle(summary.getArticleId());
				if (content != null)
				{
					AppHandler.getInstance().downSplashPhoto(
							content.getImageUrl(), content.getImageTime());
				}

				if (summary.getSplashText() != null)
				{
					AppHandler.getInstance().downDynamicText(summary.getTime());
				}
			}

		} catch (Error e)
		{
			e.printStackTrace();
		} catch (AppException e)
		{
			e.printStackTrace();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @设置闪屏显示
	 */
	private void setSplashView()
	{
		String fileName = AppHandler.getInstance().getSplashFileName();
		if (fileName.equals(""))
		{
			splashView.setImageDrawable(
					Utils.readBitmap(this, R.drawable.splash_screen));
		} else
		{
			try
			{
				Drawable splashBitmap = Utils.readBitmap(this, fileName);
				if (splashBitmap != null)
				{
					splashView.setImageDrawable(splashBitmap);
				} else
				{
					splashView.setImageDrawable(
							Utils.readBitmap(this, R.drawable.splash_screen));
				}
			} catch (Exception e)
			{
				splashView.setImageDrawable(
						Utils.readBitmap(this, R.drawable.splash_screen));
			}
		}
	}

	// endregion
}
