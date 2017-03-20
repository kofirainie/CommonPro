package com.gpstrace.dlrc.handler;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.config.AppConfig;
import com.gpstrace.dlrc.finals.CacheFinals;
import com.gpstrace.dlrc.finals.UrlFinals;
import com.gpstrace.dlrc.model.AppException;
import com.gpstrace.dlrc.model.RequestSplash;
import com.gpstrace.dlrc.model.ResponseCheckVersion;
import com.gpstrace.dlrc.model.ResponseSplashSummary;
import com.gpstrace.dlrc.model.SplashContent;
import com.gpstrace.dlrc.model.SplashSummary;
import com.gpstrace.dlrc.model.SplashTextContent;
import com.gpstrace.dlrc.provider.CacheHelper;
import com.gpstrace.dlrc.provider.CustomImageDownloader;
import com.gpstrace.dlrc.provider.HttpProvider;
import com.gpstrace.dlrc.provider.ImageProvider;
import com.gpstrace.dlrc.provider.JsonProvider;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * @author kofirainie
 * @function
 * @date 日期 2017/02/14
 * @param 参数
 */
public class AppHandler {
	// region fields
	private static AppHandler sInstance;
	private Context mContext;// 应用程序上下文
	private SharedPreferences conifgShared;// 与账号相关的参数存储
	private SharedPreferences appConfig;// 应用程序级存储

	private PopupWindow appPopup = null;// 全局对话框存在
	private Activity topActivity = null;// 置顶Activity
	
	private Collection listeners = null;// UI更新事件监听集合
	
	// endregion
	
	// region 接口事件

	/**
	 * @更新事件
	 * @author YunZ
	 * 
	 */
	public interface UpdateListener
	{
		void onUpdate(Object arg, int id);// 更新

		void onReLoad(Object arg, int id);// 重新加载

		void onNotify(Object arg, int id);// 通知
	}

	// endregion

	// region method
	/**
	 * @单件方法
	 * @return
	 */
	public static AppHandler getInstance() {
		if (sInstance == null) {
			sInstance = new AppHandler();
		}
		return sInstance;
	}

	// region 初始化方法

	/**
	 * @设置应用程序Context
	 * @param mContext
	 */
	public void setContext(Context mContext) {
		this.mContext = mContext;
		conifgShared = this.mContext.getSharedPreferences("Config", 
				Context.MODE_PRIVATE);
		appConfig = this.mContext.getSharedPreferences("APPConfig", 
				Context.MODE_PRIVATE);
	}
	
	/**
	 * 带登录态session值，在CustomImageDownloader中设置
	 * @kofirainie更改：现在图片请求统一带上session信息
	 * @设置图片加载器
	 * 
	 */
	public void setImageLoader()
	{
//		ImageProvider.Loader
//				.init(ImageLoaderConfiguration.createDefault(mContext));
		//下面的方式请求图片时带上用户的cookie登录信息
		ImageProvider.Loader
		.init(new ImageLoaderConfiguration.Builder(mContext)
				.imageDownloader(new CustomImageDownloader(mContext))
				.memoryCache(new LRULimitedMemoryCache(2 * 1024 * 1024))
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.build());
	}

	/**
	 * @清除登录信息
	 * @包括连接状态字及用户信息
	 */
	private void clearLogInfo()
	{
		Editor editor = conifgShared.edit();
		editor.clear();
		editor.commit();
	}
	
	/**
	 * @初始化友盟配置
	 */
	public void initUmengConfig()
	{
		// 友盟统计配置
		// MobclickAgent.setDebugMode(true);去掉测试
		MobclickAgent.updateOnlineConfig(mContext);
	}

	/**
	 * @结束友盟统计（进行统计数据保存）
	 */
	public void finishUmengAnalytic()
	{
		MobclickAgent.onKillProcess(mContext);
	}
	
	/**
	 * @获取自组织的User-Agent
	 * @param context
	 * @return
	 */
	public String getAgentString(Context context)
	{
		try
		{
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			String verName = pi.versionName == null ? "null" : pi.versionName;
			String verCode = pi.versionCode + "";
			return String.format(
					context.getResources().getString(R.string.app_agent_str),
					verName, verCode, Build.VERSION.RELEASE, Build.MODEL,
					Build.SERIAL);
		} catch (Exception e)
		{
			return "";
		}
	}

	// endregion
	
	// region 闪屏相关
	
	/**
	 * @保存闪屏页的更新时间
	 */
	public void saveSplashTime(long time)
	{
		Editor editor = appConfig.edit();
		editor.putLong("splashtime", time);
		editor.commit();
	}

	/**
	 * @获取闪屏页的更新时间
	 * @return
	 */
	public long getSplashTime()
	{
		return appConfig.getLong("splashtime", 0L);
	}
	
	/**
	 * @获取闪屏的文件名
	 * @return
	 */
	public String getSplashFileName()
	{
		long time = getSplashTime();
		if (time == 0L)
		{
			return "";
		} else
		{
			return AppConfig.getInstance().getDownloadFolder() + time + ".jpg";
		}
	}

	/**
	 * @获取动态文本更新时间
	 * @return
	 */
	public long getDynamicTextTime()
	{
		return appConfig.getLong("dynamictext", 0L);
	}
	
	/**
	 * @获取动态文本和更新时间
	 */
	public void saveDynamicText(SplashTextContent content, long time)
	{

		Editor editor = appConfig.edit();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			// 将Product对象放到OutputStream中
			oos.writeObject(content);
			// 将Product对象转换成byte数组，并将其进行base64编码
			String productBase64 = new String(
					Base64.encodeBase64(baos.toByteArray()));

			editor.putString("dynamiccontent", productBase64);// 将编码后的字符串写到base64.xml文件中
			editor.putLong("dynamictime", time);
			editor.commit();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @获取闪屏概要
	 * @return
	 */
	public SplashSummary getSplashSummary(RequestSplash request)
			throws AppException
	{
		try
		{
			String tmp = HttpProvider.getMessageWithEncode(
					UrlFinals.SPLASH_URL_STR + request.requestSummary(),
					"UTF-8");

			ResponseSplashSummary response = JsonProvider.parseJson(tmp,
					ResponseSplashSummary.class);

			if (response.isDone())
			{
				return response.getSummary();
			} else
			{
				return null;
			}

		} catch (Exception e)
		{
			if (e instanceof IOException)
				throw AppException.getIoExp(e);
			throw AppException.getNetworkExp(e);
		}
	}
	
	/**
	 * @获取闪屏文章内容
	 * @return
	 */
	public SplashContent getSplashArticle(int id) throws AppException
	{
		try
		{
			String tmp = HttpProvider.getMessageWithEncode(String.format(
					UrlFinals.SPLASH_ARTICLE_URL_STR, id % 10, id), "UTF-8");

			SplashContent response = JsonProvider.parseJson(tmp,
					SplashContent.class);

			return response;

		} catch (Exception e)
		{
			if (e instanceof IOException)
				throw AppException.getIoExp(e);
			throw AppException.getNetworkExp(e);
		}
	}
	
	/**
	 * @下载闪屏页
	 * @param id
	 * @throws AppException
	 */
	public void downSplashPhoto(String url, long time) throws AppException
	{
		try
		{
			long exist = getSplashTime();

			if (exist != time)// 判断是否需要执行下载
			{
				String downFolder = AppConfig.getInstance().getDownloadFolder();

				String downFileStr = downFolder + String.valueOf(time) + ".jpg";
				// 执行下载
				Boolean isDown = Downloader.getInstance().downFile(downFileStr,
						url);
				if (isDown)// 下载成功则执行删除和更新操作，失败则跳过
				{
					if (exist != 0L)// 是否未下载过，不等于0L表示已经下载过，需要删除，等于表示未下载过
					{
						String oldFileStr = downFolder + String.valueOf(exist)
								+ ".jpg";
						File oldFile = new File(oldFileStr);
						if (oldFile.exists())
						{
							oldFile.delete();
						}
					}
					saveSplashTime(time);
				}
			}

		} catch (Exception e)
		{
			if (e instanceof IOException)
				throw AppException.getIoExp(e);
			throw AppException.getNetworkExp(e);
		}
	}
	
	/**
	 * @throws AppException
	 * @下载动态文本
	 */
	public void downDynamicText(long time) throws AppException
	{
		try
		{
			long exist = getDynamicTextTime();

			if (exist != time)// 判断是否需要执行下载
			{
				String tmp = HttpProvider.getMessageWithEncode(
						UrlFinals.SPLASH_TEXT_URL_STR, "UTF-8");
				SplashTextContent response = JsonProvider.parseJson(tmp,
						SplashTextContent.class);
				if (response != null)
				{
					saveDynamicText(response, time);
				}
			}

		} catch (Exception e)
		{
			if (e instanceof IOException)
				throw AppException.getIoExp(e);
			throw AppException.getNetworkExp(e);
		}
	}
	// endregion
	
	// region 内部保存session
	
	/**
	 * @获取连接标识值
	 * @return
	 */
	public String getSessionValue()
	{
		return conifgShared.getString("cookieValue", "");
	}

	/**
	 * @保存连接标识值
	 */
	private void saveSessionValue(String value)
	{
		if (value.length() > 0)
		{
			Editor editor = conifgShared.edit();
			editor.putString("cookieValue", value);
			editor.commit();
		}
	}
	
	// endregion
	
	// region 全局对话框

	/**
	 * @设置顶部Activity
	 * @param top
	 */
	public void setCurrentActivity(Activity top)
	{
		this.topActivity = top;
	}

	/**
	 * @获取当前活动Activity
	 * @return
	 */
	public Activity getCurrentActivity()
	{
		return this.topActivity;
	}
	
	// endregion
	
	// region 其他方法

	/**
	 * @在主Context上显示Toast
	 * @param message
	 */
	public void showToast(int res)
	{
		Looper.prepare();
		String message = mContext.getResources().getString(res);
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
		Looper.loop();
	}
	
	/**
	 * @显示信息
	 * @param message
	 */
	public void showToastMessage(String message)
	{
		Looper.prepare();
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
		Looper.loop();
	}

	// endregion

	// region 全局对话框
	/**
	 * @获取全局对话框状态
	 * @return
	 */
	public boolean IsAppDialogActive()
	{
		if (appPopup != null && appPopup.isShowing())
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	// endregion
	
	// region 事件管理器
	
	/**
	 * @添加事件监听
	 * @param listener
	 */
	public void addListener(UpdateListener listener)
	{
		if (listeners == null)
		{
			listeners = new HashSet();
		}
		listeners.add(listener);
	}

	/**
	 * @通知使能
	 */
	public void enableNotify(Object arg, int id)
	{
		if (listeners != null)
		{
			Iterator iterator = listeners.iterator();
			while (iterator.hasNext())
			{
				UpdateListener listener = (UpdateListener) iterator.next();
				listener.onNotify(arg, id);
			}
		}
	}
	
	/**
	 * @重新加载使能
	 */
	public void enableReload(Object arg, int id)
	{
		if (listeners != null)
		{
			Iterator iterator = listeners.iterator();
			while (iterator.hasNext())
			{
				UpdateListener listener = (UpdateListener) iterator.next();
				listener.onReLoad(arg, id);
			}
		}
	}
	
	/**
	 * @更新使能
	 */
	public void enableUpdate(Object arg, int id)
	{
		if (listeners != null)
		{
			Iterator iterator = listeners.iterator();
			while (iterator.hasNext())
			{
				UpdateListener listener = (UpdateListener) iterator.next();
				listener.onUpdate(arg, id);
			}
		}
	}
	
	/**
	 * @移除事件监听
	 * @param listener
	 */
	public void removeListener(UpdateListener listener)
	{
		if (listeners == null)
			return;
		listeners.remove(listener);
	}
	
	// endregion
	
	// region 本地文件数据读取
	/**
	 * @保存本地的服务器返回的安卓版本以及闪屏页信息
	 * @param version
	 * @return
	 */
	public Boolean saveVersion(ResponseCheckVersion version)
	{
		String file = CacheFinals.ANDROID_VERSION_DATA;

		if (file.equals(""))
		{
			return false;
		} else
		{
			return CacheHelper.getInstance().saveObject(version, file);
		}
	}
	
	/**
	 * @获得本地的服务器返回的安卓版本以及闪屏页信息
	 * @param isFailure
	 * @当为连接状态时，失效使能为true，否则为false
	 * @return
	 */
	public ResponseCheckVersion getVersion(Boolean isFailure)
	{
		String file = CacheFinals.ANDROID_VERSION_DATA;

		if (file.equals(""))
		{
			return null;
		} else
		{
			return (ResponseCheckVersion)CacheHelper.getInstance().readObject(file, isFailure);
		}
	}
	
	
	// endregion
	
	// endregion
}
