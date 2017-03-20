package com.gpstrace.dlrc.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gpstrace.dlrc.view.EmojiParser;
import com.gpstrace.dlrc.view.EmojiUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @工具类
 * @author YunZ
 * 
 */
public class Utils
{
	public static final int IO_BUFFER_SIZE = 8 * 1024;
	private static long lastClickTime;// 最近一次点击的时间

	private Utils()
	{
	};

	/**
	 * Workaround for bug pre-Froyo, see here for more info:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 */
	public static void disableConnectionReuseIfNecessary()
	{
		// HTTP connection reuse which was buggy pre-froyo
		if (hasHttpConnectionBug())
		{
			System.setProperty("http.keepAlive", "false");
		}
	}

	/**
	 * Get the size in bytes of a bitmap.
	 * 
	 * @param bitmap
	 * @return size in bytes
	 */
	@SuppressLint("NewApi")
	public static int getBitmapSize(Bitmap bitmap)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
		{
			return bitmap.getByteCount();
		}
		// Pre HC-MR1
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * Check if external storage is built-in or removable.
	 * 
	 * @return True if external storage is removable (like an SD card), false
	 *         otherwise.
	 */
	@SuppressLint("NewApi")
	public static boolean isExternalStorageRemovable()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
		{
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	/**
	 * Get the external app cache directory.
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	@SuppressLint("NewApi")
	public static File getExternalCacheDir(Context context)
	{
		if (hasExternalCacheDir())
		{
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(
				Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}

	/**
	 * Check how much usable space is available at a given path.
	 * 
	 * @param path
	 *            The path to check
	 * @return The space available in bytes
	 */
	@SuppressLint("NewApi")
	public static long getUsableSpace(File path)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
		{
			return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	/**
	 * Get the memory class of this device (approx. per-app memory limit)
	 * 
	 * @param context
	 * @return
	 */
	public static int getMemoryClass(Context context)
	{
		return ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
	}

	/**
	 * Check if OS version has a http URLConnection bug. See here for more
	 * information:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 * 
	 * @return
	 */
	public static boolean hasHttpConnectionBug()
	{
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO;
	}

	/**
	 * Check if OS version has built-in external cache dir method.
	 * 
	 * @return
	 */
	public static boolean hasExternalCacheDir()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	/**
	 * Check if ActionBar is available.
	 * 
	 * @return
	 */
	public static boolean hasActionBar()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * @是否双击判断
	 * @保证Click只被执行一次
	 * @return
	 */
	public static boolean isFastDoubleClick()
	{
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800)
		{
			return true;
		}
		lastClickTime = time;
		return false;
	}
	
	/**
	 * @function 删除file文件夹(包括file文件下面的文件)
	 * @param absolutePath 绝对路径
	 * */
	public static void clearAndDelectFile(String absolutePath){
		File file = new File(absolutePath);
		if(file.exists()){
			Utils.deleteFile(file);
		}
	}
	
	/**
	 * @function 删除file文件夹(包括file文件下面的文件)
	 * @param file为绝对路径
	 * */
	public static void deleteFile(File file){
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(int i=0; i<files.length; i++){
				deleteFile(files[i]);
			}
		}
		file.delete();
	}

	/**
	 * 获取压缩后的图片
	 */
	public static Bitmap getScaleImage(Bitmap image, int reqWidth,
			int reqHeight)
	{

		int w = image.getWidth();
		int h = image.getHeight();
		double ratio = 1.0f;

		// ImageSize targetSize = null;

		if (h > reqHeight || w > reqWidth)
		{
			double heightRatio = reqHeight / (double) h;
			double widthRatio = reqWidth / (double) w;
			ratio = heightRatio < widthRatio ? heightRatio : widthRatio;
			Matrix m = new Matrix();
			m.postScale((float) (ratio), (float) (ratio));
			Bitmap bitmap = Bitmap.createBitmap(image, 0, 0, w, h, m, true);
			return bitmap;

		} else
		{
			return image;
			// targetSize = new ImageSize(w, h);
		}

	}

	/**
	 * 
	 * @获取状态栏高度
	 * @return > 0 success; <= 0 fail
	 */
	public static int getStatusHeight(Context context)
	{
		int statusHeight = 0;
		Rect localRect = new Rect();
		((Activity) context).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight)
		{
			Class<?> localClass;
			try
			{
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer
						.parseInt(localClass.getField("status_bar_height")
								.get(localObject).toString());
				statusHeight = context.getResources().getDimensionPixelSize(i5);
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			} catch (InstantiationException e)
			{
				e.printStackTrace();
			} catch (NumberFormatException e)
			{
				e.printStackTrace();
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			} catch (SecurityException e)
			{
				e.printStackTrace();
			} catch (NoSuchFieldException e)
			{
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

	public static int getScreenHeight(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		// int width = dm.widthPixels;
		int height = dm.heightPixels;
		return height;
	}

	public static int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		// int height2 = dm.heightPixels;
		return width;
	}

	/**
	 * @将px值转换为dip或dp值，保证尺寸大小不变
	 * @param pxValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * @将dip或dp值转换为px值，保证尺寸大小不变
	 * @param dipValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue)
	{
		final float fontScale = context.getResources()
				.getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * @将sp值转换为px值，保证文字大小不变
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue)
	{
		final float fontScale = context.getResources()
				.getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * @表情屏蔽
	 */
	public static InputFilter emojiFilter = new InputFilter()
	{
		Pattern emoji = Pattern.compile(
				"[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
				Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend)
		{

			Matcher emojiMatcher = emoji.matcher(source);

			if (emojiMatcher.find())
			{

				return "";

			}
			return null;

		}
	};
	
	/**
	 * @ function 返回标准的json格式
	 * @ 本函数主要是检测是否返回-3的状态，如例子所示，data返回的是字符串，而data的的数据结构可能是任何值
	 * @ 例子：假如传进来是{"code":-3,"desc":"","time":1479892823,"data":"所在学校尚未初始化该模块！"}
	 * @ 结果：{"code":-3,"data":,"time":1479892823,"desc":"所在学校尚未初始化该模块！"}
	 * @param longStr:搜索元字符串
	 * @param strucctStyle:data的实体类型来决定data后加:1.{};2.[];3."";4."\"\"";5.false;6.true
	 * @return
	 */
	public static String getMessageStr(String msgStr, int strucctStyle)
	{
		if (null != msgStr && msgStr.length() > 0) {
			if (-3 == getJsonCode(msgStr, "code")) {
				//code为-3时，data内容不为空时执行，此时一般data为字符串，因为黄科大返回字符串。
				String dataStr = getErrorJsonData(msgStr, "data");
				if (null != dataStr && !"".equals(dataStr)) {
					msgStr = getErrorJsonAfterReplace(msgStr, strucctStyle);
				}
			}
		}
		return msgStr;
	}
	
	/**
	 * @ function 在longStr中查找findStr+:"到第一个逗号的字符串的内容
	 * @ 本函数主要用于查找code的值，因为黄科大那边data返回的是字符串，而data的的数据结构可能是任何值
	 * @ 不适合查找data，因为data最后没有逗号了，本程序段没有考虑data的情形
	 * @ 例子：{"code":-3,"desc":"","time":1479892823,"data":"所在学校尚未初始化该模块！"}
	 * @ 说明：比如查找code+:"到第一个逗号的值为-3
	 * @param longStr:搜索元字符串
	 * @param indexStr:查找字符串
	 * @return
	 */
	public static int getJsonCode(String longStr, String indexStr)
	{
		int codeValue = 0;
		if (null != longStr && longStr.length() > 0
				&& null != indexStr && indexStr.length() > 0) {
			int indexPos = longStr.indexOf(indexStr);
			//返回从indexPos + indexStr.length() + 2开始查找到第一个出现的逗号的位置
			//比如说返回-3之后的逗号的位置
			int commaPos = longStr.indexOf(",", indexPos + indexStr.length() + 1);
			String codeStr = longStr.substring(indexPos + indexStr.length() + 2, commaPos);
			codeValue = Integer.valueOf(codeStr);
		}
		
		return codeValue;
	}
	
	/**
	 * @ function 在longStr中查找findStr+":"到第一个双引号的字符串的内容
	 * @ 本函数主要用于查找data的值，因为黄科大那边data返回的是字符串，而data的的数据结构可能是任何值
	 * @ 例子：{"code":-3,"desc":"","time":1479892823,"data":"所在学校尚未初始化该模块！"}
	 * @ 说明：比如查找data+":"到第一个双引号的值为:所在学校尚未初始化该模块！
	 * @param longStr:搜索元字符串
	 * @param indexStr:查找字符串
	 * @return
	 */
	public static String getErrorJsonData(String longStr, String indexStr)
	{
		String codeValue = null;
		if (null != longStr && longStr.length() > 0
				&& null != indexStr && indexStr.length() > 0) {
			int indexPos = longStr.indexOf(indexStr);
			//返回从indexPos + indexStr.length() + 2开始查找到第一个出现的逗号的位置
			//比如说返回英文字符双引号"之后的的位置
			int quotationPos = longStr.indexOf("\"", indexPos + indexStr.length() + 3);
			codeValue = longStr.substring(indexPos + indexStr.length() + 3, quotationPos);
		}
		
		return codeValue;
	}
	
	/**
	 * @ function 在longStr中查找desc和data字符串。替换desc为data，data为desc
	 * @ 本函数主要用于查找data的值，因为黄科大那边data返回的是字符串，而data的的数据结构可能是任何值
	 * @ 例子：{"code":-3,"desc":"","time":1479892823,"data":"所在学校尚未初始化该模块！"}
	 * @ 逻辑：首先替换data字符为temp字符，将desc字符改为linshi，将temp字符改为desc字符，将linshi字符改为data
	 * @ 说明：替换之后为：{"code":-3,"data":"","time":1479892823,"desc":"所在学校尚未初始化该模块！"}
	 * @param longStr:搜索元字符串
	 * @param indexStr:查找字符串
	 * @param strucctStyle:data的实体类型来决定data后加:1.{};2.[];3."";4."\"\"";5.false;6.true
	 * @return
	 */
	public static String getErrorJsonAfterReplace(String longStr, int strucctStyle)
	{
		if (null != longStr && longStr.length() > 0) {
			longStr = longStr.replace("desc", "linshi");	//替换desc字符改为linshi
			longStr = longStr.replace("data", "temp");		//替换data字符为temp字符
			longStr = longStr.replace("linshi", "data");		//替换linshi字符改为data字符
			longStr = longStr.replace("temp", "desc");		//替换temp字符改为desc
			longStr = getQuotationReplace(longStr, "data", strucctStyle);
		}
		return longStr;
	}
	
	/**
	 * @ function 将""替换为空格
	 * @ 例子：{"code":-3,"data":"","time":1479892823,"desc":"所在学校尚未初始化该模块！"}
	 * @ 说明：替换之后为：{"code":-3,"data":,"time":1479892823,"desc":"所在学校尚未初始化该模块！"}
	 * @ 变化 :"data":""变为"data":
	 * @param longStr:搜索元字符串
	 * @param indexStr:查找字符串
	 * @param strucctStyle:data的实体类型来决定data后加:1.{};2.[];3."";4."\"\"";5.false;6.true
	 * @return
	 */
	public static String getQuotationReplace(String msgStr, String indexStr, int strucctStyle)
	{
		String temp = null;
		StringBuffer newMsg = new StringBuffer();
		if (null != msgStr && msgStr.length() > 0 
				&& null != indexStr && indexStr.length() > 0) {
			//将data:冒号以及冒号之前的字符放到StringBuffer中
			int indexPos = msgStr.indexOf(indexStr);
			temp = msgStr.substring(0, indexPos + indexStr.length() + 2);
			newMsg.append(temp);				
			switch (strucctStyle) {
			case 1:
				newMsg.append("{}");
				break;
			case 2:
				newMsg.append("[]");
				break;
			case 3:
				newMsg.append("");
				break;
			case 4:
				newMsg.append("\"\"");
				break;
			case 5:
				newMsg.append("false");
				break;
			case 6:
				newMsg.append("true");
				break;
			default:
				break;
			}
			
			//将"data":"",逗号以及逗号之后的字符放到StringBuffer中
			int secondQuotationPos = msgStr.indexOf("\"", indexPos + indexStr.length() + 3);	//"data":""冒号之后的第二个双引号的位置
			temp = msgStr.substring(secondQuotationPos + 1, msgStr.length());
			newMsg.append(temp);
		}
		return newMsg.toString();
	}
	
	/**
	 * @检查是否全为空白
	 * @return
	 */
	public static Boolean isBlank(String str)
	{
		if (str != null && str.length() > 0)
		{
			return str.trim().length() == 0;
		} else
		{
			return true;
		}
	}
	
	/**
	 * @检查GPS是否已经开启
	 * @return
	 */
	public static Boolean getGpsState1(Context context){
		LocationManager locationManager = (LocationManager)context
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	/** 
	 * Gets the state of GPS location. 
	 * @param context 
	 * @return true if enabled. 
	 */  
	public static Boolean getGpsState2(Context context) {  
		ContentResolver resolver = context.getContentResolver();  
		return Settings.Secure.isLocationProviderEnabled(resolver,  
	            LocationManager.GPS_PROVIDER);  
	}
	
	/** 
     * @function 强制帮用户打开GPS 
     * @param context 
     */  
    public static final void openGPS(Context context) {  
        Intent GPSIntent = new Intent();  
        GPSIntent.setClassName("com.android.settings",  
                "com.android.settings.widget.SettingsAppWidgetProvider");  
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");  
        GPSIntent.setData(Uri.parse("custom:3"));  
        try {  
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();  
        } catch (CanceledException e) {  
            e.printStackTrace();  
        }  
    }

	/**
	 * @判断输入框是否为空
	 * @param editText
	 * @return
	 */
	public static boolean isNull(EditText editText)
	{
		String text = editText.getText().toString().trim();
		if (text != null && text.length() > 0)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * @判断文字框是否为空
	 * @param textView
	 * @return
	 */
	public static boolean isTextViewNull(TextView textView)
	{
		String text = textView.getText().toString().trim();
		if (text != null && text.length() > 0)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * @检查EditText是否有非空/空格之外的文字
	 * @return
	 */
	public static boolean isEditTextHasText(EditText mEditText){
		if (Utils.isNull(mEditText) || Utils
				.isBlank(mEditText.getText().toString().replaceAll(" ", ""))) {
			return false;
		}
		return true;
	}
	
	/**
	 * @检查字符串中是否有空格
	 * @return
	 */
	public static boolean isHasBalnk(String str){		
		return str.indexOf(" ") >= 0 ? true : false; 
	}
	
	/**
	 * @检查TextView是否有非空/空格之外的文字
	 * @return
	 */
	public static boolean isTextViewHasText(TextView mTextView){
		if (Utils.isTextViewNull(mTextView) || Utils
				.isBlank(mTextView.getText().toString().replaceAll(" ", ""))) {
			return false;
		}
		return true;
	}
	
	/**
	 * @判断是否是数字字符串
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str)
	{
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
//		return isNum.find();
		if (!isNum.matches())
		{
			return false;
		}
		return true;
	}
	
	/** 
     * @ 判断 String 是否是以0开头的正整数 
     * @合法的数字：123之类的，不合法的数字：0123,0012，-12，--12，++12
     * @param str 
     * @return 
     */  
    public static boolean isNomalNumber(String str){
    	if("0".equals(str.substring(0,1))){
    		return false;
    	}else{
    		Pattern pattern = Pattern.compile("[0-9]*");
    		Matcher isNum = pattern.matcher(str);
//    		return isNum.find();
    		if (!isNum.matches())
    		{
    			return false;
    		}
    		return true;
    	}
    }
    
	/*** 
     * @ 判断 String 是否是正整数，包括带+符号的字符 
     * @ +表示1个或多个（如"3"或"225"
     * @ *表示0个或多个（[0-9]*）（如""或"1"或"22")
     * @ ?表示0个或1个([0-9]?)(如""或"7") 
     * @param str 
     * @return 
     */  
    public static boolean isPositiveNumber(String str){ 
    	Pattern pattern = Pattern.compile("^[+]?[0-9]*$");
        Matcher mer = pattern.matcher(str);  
        return mer.find();  
    }
    
	/*** 
     * @ 判断 String 是否是正整数 
     * @param str 
     * @return 
     */  
    public static boolean isPositiveNumber2(String str){ 
    	try {   
    	    //把字符串强制转换为数字   
    	    int num=Integer.valueOf(str);  
    	    //如果是数字，返回True  
    	    if (num >= 0) {
    	    	return true;
			}
    	    return false;
    	} catch (Exception e) {  
    	    //如果抛出异常，返回False   
    	    return false;  
    	}  
    }
    
	/*** 
     * @ 判断 String 是否是正整数 
     * @param str 
     * @return 
     */  
    public static boolean isPositiveNumber3(String str){ 
    	if (!"".equals(str)) {   
    	    char num[] = str.toCharArray();  //把字符串转换为字符数组 
    	    for (int i = 0; i < num.length; i++) {   
    	        if (!Character.isDigit(num[i])) {
					return false;
				}  
    	        return true;
    	    }   
    	} 
    	return false;
    }

	/**
	 * @检查浮点数
	 * @param num
	 * @param type
	 * 			@"0+":非负浮点数 "+":正浮点数 "-0":非正浮点数 "-":负浮点数 "":浮点数
	 * @return
	 */
	public static boolean checkFloat(String num, String type)
	{
		String eL = "";
		if (type.equals("0+"))
			eL = "^\\d+(\\.\\d+)?$";// 非负浮点数
		else if (type.equals("+"))
			eL = "^((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*))$";// 正浮点数
		else if (type.equals("-0"))
			eL = "^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";// 非正浮点数
		else if (type.equals("-"))
			eL = "^(-((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*)))$";// 负浮点数
		else
			eL = "^(-?\\d+)(\\.\\d+)?$";// 浮点数
		Pattern p = Pattern.compile(eL);
		Matcher m = p.matcher(num);
		boolean b = m.matches();
		return b;
	}

	/**
	 * @判断是否是邮箱
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email)
	{
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();

		// region
		// if (Pattern.compile("\\w[\\w.-]*@[\\w.]+\\.\\w+").matcher(text)
		// .matches())
		// {
		// return true;
		// }
		// return false;
		// endregion
	}

	/**
	 * @判断手机号码是否合理
	 * @param phoneNums
	 */
	public static boolean isPhoneNums(String phoneNums)
	{
		//原来是if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums))
		//由于电话号码的样式变化，由服务器来判断电话号码是否正确
		if (isMatchLength(phoneNums, 11) && isMobileNO2(phoneNums))
		{
			return true;
		}
		return false;

		// region

		// if
		// (Pattern.compile("(\\d{11})|(\\+\\d{3,})").matcher(text).matches())
		// {
		// return true;
		// }
		// return false;

		// endregion
	}
	
	/**
	 * @判断图片验证码是否正确
	 * @param codeStr
	 */
	public static boolean isSecurityCode(String codeStr)
	{
		if (isMatchLength(codeStr, 4))
		{
			return true;
		}
		return false;
	}

	/**
	 * @判断验证码是否正确
	 * @param codeStr
	 */
	public static boolean isVerifyCode(String codeStr)
	{
		if (isMatchLength(codeStr, 6) && isNumeric(codeStr))
		{
			return true;
		}
		return false;
	}

	/**
	 * @判断数字码是否正确
	 * @param codeStr
	 */
	public static boolean isMatchNumberCode(String codeStr, int length)
	{
		if (isMatchLength(codeStr, length) && isNumeric(codeStr))
		{
			return true;
		}
		return false;
	}

	/**
	 * @判断一个字符串的位数
	 * @param str
	 * @param length
	 * @return
	 */
	public static boolean isMatchLength(String str, int length)
	{
		if (str.isEmpty())
		{
			return false;
		} else
		{
			return str.length() == length ? true : false;
		}
	}

	/**
	 * @判断一个字符串的位数是否符合最小值范围
	 * @param str
	 * @param length
	 * @return
	 */
	public static boolean isLengthRange(String str, int min)
	{
		if (str.isEmpty())
		{
			return false;
		} else
		{
			return str.length() >= min ? true : false;
		}
	}

	/**
	 * 验证手机格式
	 */
	private static boolean isMobileNO(String mobileNums)
	{
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][3578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobileNums))
			return false;
		else
			return mobileNums.matches(telRegex);

		// region

		// Pattern p = Pattern
		// .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		// Matcher m = p.matcher(mobiles);
		//
		// return m.matches();

		// endregion
	}
	
	/**
	 * 验证手机格式
	 */
	private static boolean isMobileNO2(String mobileNums)
	{
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1]\\d{10}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobileNums))
			return false;
		else
			return mobileNums.matches(telRegex);

		// region

		// Pattern p = Pattern
		// .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		// Matcher m = p.matcher(mobiles);
		//
		// return m.matches();

		// endregion
	}

	/**
	 * @电话号码验证
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isTelephone(String str)
	{

		if (str == null || str.length() == 0)
		{
			return false;
		}
		Pattern p1 = null, p2 = null;
		Matcher m = null;
		boolean b = false;
		p1 = Pattern.compile("^[0][0-9]{2,3}[0-9]{5,10}$"); // 验证带区号的
		p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // 验证没有区号的
		if (str.length() > 9)
		{
			m = p1.matcher(str);
			b = m.matches();
		} else
		{
			m = p2.matcher(str);
			b = m.matches();
		}
		return b;
	}

	/**
	 * @从本地资源读取图片
	 * @图片的分辨率不是比例，分辨率过大的话，在某些手机上会产生内存问题
	 * @将图片放入对应的分辨率文件夹可以减少内存问题的产生，但不能避免该问题，而且需要将资源拷贝到不同的文件夹
	 * @使用资源读取的方式可以进一步优化高分辨率资源的加载问题，同时可以按比例降低分辨率
	 * @设置largeHeap
	 * @param content
	 * @param resId
	 * @return
	 */
	public static BitmapDrawable readBitmap(Context content, int resId)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片

		try
		{
			InputStream is = content.getResources().openRawResource(resId);
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
			is.close();
			return new BitmapDrawable(content.getResources(), bitmap);
		} catch (IOException e)
		{
			return null;
		}
	}

	/**
	 * @从本地资源读取图片
	 * @图片的分辨率不是比例，分辨率过大的话，在某些手机上会产生内存问题
	 * @将图片放入对应的分辨率文件夹可以减少内存问题的产生，但不能避免该问题，而且需要将资源拷贝到不同的文件夹
	 * @使用资源读取的方式可以进一步优化高分辨率资源的加载问题，同时可以按比例降低分辨率
	 * @设置largeHeap
	 * @param content
	 * @param resId
	 * @return
	 */
	public static BitmapDrawable readBitmap(Context content, String fileName)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片

		try
		{
			InputStream is = new FileInputStream(fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
			is.close();
			return new BitmapDrawable(content.getResources(), bitmap);
		} catch (IOException e)
		{
			return null;
		}
	}

	/**
	 * @字节数组转16进制
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src)
	{
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0)
		{
			return null;
		}
		for (int i = 0; i < src.length; i++)
		{
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2)
			{
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * @判断手机是否安装了浏览器
	 * @return
	 */
	public static boolean hasBrowser(Context context)
	{
		PackageManager pm = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_BROWSABLE);
		intent.setData(Uri.parse("http://"));

		List<ResolveInfo> list = pm.queryIntentActivities(intent,
				PackageManager.GET_INTENT_FILTERS);
		final int size = (list == null) ? 0 : list.size();
		return size > 0;
	}

	/**
	 * @获取Emoji显示字符串
	 * @param context
	 * @param emojiStr
	 * @emoji字符串
	 * @return
	 */
	public static SpannableString getEmojiStr(Context context, String emojiStr)
	{
		String unicode = EmojiParser.getInstance(context)
				.parseEmojiStr(emojiStr);
		SpannableString spannableString = EmojiUtil.getExpressionString(context,
				unicode);

		return spannableString;
	}

	/**
	 * @获取整值对应的二进制位标识
	 * @param value
	 * @对应的整值
	 * @param index
	 * @要取的位序列
	 * @return
	 * @1返回true，0返回false
	 */
	public static Boolean IntToBinaryBit(int value, int index)
	{
		String binaryStr = Integer.toBinaryString(value);
		if (index >= binaryStr.length())
		{
			return false;
		} else
		{
			return binaryStr.charAt(binaryStr.length() - 1 - index) == '1';
		}
	}

	/**
	 * @二进制字符串转化为对应的整值
	 * @param binary
	 * @return
	 */
	public static int BinaryStringToInt(String binary)
	{
		return Integer.parseInt(binary, 2);
	}

	/**
	 * @获取设备唯一标识，若获取不到，则获取imei
	 * @param context
	 * @return
	 */
	public static String getSerial(Context context)
	{
		try
		{
			String serial = Build.SERIAL;
			if (serial != null && !serial.equals(""))
			{
				return serial;
			} else
			{
				return getImei(context);
			}
		} catch (Exception e)
		{
			return getImei(context);
		}
	}

	/**
	 * @获取imei值
	 * @param context
	 * @return
	 */
	public static String getImei(Context context)
	{
		String imei = "000000000000000";
		try
		{
			TelephonyManager phoneMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = phoneMgr.getDeviceId();
		} catch (Exception e)
		{
			imei = null;
		}
		return imei != null ? imei : "000000000000000";
	}
	
	/**
	 * @获取版本号
	 * @return 当前应用的版本号
	 */
	public static String getVersion(Context mContext)
	{
		String version = "";
		try
		{
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			version = info.versionName;
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return version;
	}
	
	/*
	 * @验证身份证号是否符合规则
	 * 
	 * @param text 身份证号
	 * 
	 * @return
	 */
	public static boolean isIdentityId(String text)
	{
		String regx = "[0-9]{17}X";
		String reg1 = "[0-9]{15}";
		String regex = "[0-9]{18}";
		return text.matches(regx) || text.matches(reg1) || text.matches(regex);
	}
	
	/**
	 * @function 指定字符串是否包含了指定正则表达式
	 * @param sourse 
	 * */
	public static boolean isContainsSpecifiedCharater(String sourse, String specified ){
		
		Pattern pattern = Pattern.compile("^[0-9]*+[X]{1}$");
//		Pattern pattern = Pattern.compile(specified);
		Matcher isNum = pattern.matcher(sourse);
		if(!isNum.matches()){
			return false;
		}
		
		return true;
	}
	
	/**
	 * @function 生成时间戳
	 * @由yyyy/MM/dd/00/00/00的格式生成时间戳
	 * */
	public static long makeTimeStr(String dateStr, String indexStr){
		StringBuilder timeStr = new StringBuilder();
		
		timeStr.append(getIndexPreviousPartStr(dateStr, indexStr));
		timeStr.append("/00");
		timeStr.append("/00");
		timeStr.append("/00");
		
		return Utils.getStrToUnixTime(4, timeStr.toString());
	}
	
	/**
	 * @function 主要用于获取某个字符之前的，
	 * @举例：获取2016/12/12 11:05空格之前的部分，结果：2016/12/12
	 * @param sourseStr 被搜索字符串
	 * @param indexStr 在sourseStr中要寻找的第一个字符串，允许为空字符串
	 * */
	public static String getIndexPreviousPartStr(String sourseStr, String indexStr){
		StringBuilder sBuilder = new StringBuilder();
		
		if (null != sourseStr && sourseStr.length() > 0
				&& null != indexStr) {
			int indexPox = sourseStr.indexOf(indexStr);
			sBuilder.append(sourseStr.substring(0, indexPox));
		}
		
		return sBuilder.toString();
	}
	
	/**
	 * @function 将字符串转为时间戳
	 * @param style 字符串格式
	 * @1:yyyy年MM月dd日HH时mm分ss秒
	 * @2:yyyy/MM/dd HH:mm:ss
	 * @3:yyyy-MM-dd HH:mm:ss
	 * @4:yyyy/MM/dd/HH/mm/ss
	 * */  
	public static long getStrToUnixTime(int style, String user_time) {  
		long unixTime = 0l; 
		
		SimpleDateFormat sdf = null;
		switch (style) {
		case 1:
			sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");  
			break;
		case 2:
			sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");  
			break;
		case 3:
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			break;
		case 4:
			sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");  
			break;
		default:
			break;
		}
		
		try {  
			unixTime = sdf.parse(user_time).getTime(); 
		} catch (ParseException e) {  
			e.printStackTrace();  
		}
		
		return unixTime;  
	}
	
	/**
	 * @获取字体对应的行高
	 * @param fontSize
	 * @return
	 */
	public static int getFontHeight(float fontSize)
	{
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top);
	}

	/**
	 * @版本比较
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static int compareVersion(String version1, String version2)
	{
		if (version1.equals(version2))
		{
			return 0;
		}

		String[] version1Array = version1.split("\\.");
		String[] version2Array = version2.split("\\.");

		int index = 0;
		int minLen = Math.min(version1Array.length, version2Array.length);
		int diff = 0;

		while (index < minLen && (diff = Integer.parseInt(version1Array[index])
				- Integer.parseInt(version2Array[index])) == 0)
		{
			index++;
		}

		if (diff == 0)
		{
			for (int i = index; i < version1Array.length; i++)
			{
				if (Integer.parseInt(version1Array[i]) > 0)
				{
					return 1;
				}
			}

			for (int i = index; i < version2Array.length; i++)
			{
				if (Integer.parseInt(version2Array[i]) > 0)
				{
					return -1;
				}
			}

			return 0;
		} else
		{
			return diff > 0 ? 1 : -1;
		}
	}
	
	/**
	 * @param <T>
	 * @function 生成List的唯一标识.数据发生变化时，唯一标识才发生变化
	 * */
	public static <T> String makeListIndex(List<T> listData){
		StringBuilder index = new StringBuilder();
		if (null != listData && listData.size() > 0) {
			for(T oneMenu : listData){
				if (null != oneMenu) {
					index = index.append(oneMenu.hashCode());
				}
			}
		}else {
			index.append("");
		}
		return index.toString();
	}
	
	/**
	 * @function 设置文字的不同的颜色
	 * @function 通过获取上下文来设置字体大小
	 * @通过获取上下文来设置字体大小
	 * @param transmitedStr 字符串
	 * @param context 上下文信息
	 * @param size 要设置的字体大小
	 * @param color 改变的颜色
	 * */
	public static SpannableString setBottonTextSameStyle(Button mButton,
			Context context, float size, ColorStateList mColor){
		StringBuilder sb = new StringBuilder();
		sb.append(mButton.getText().toString().trim());
		SpannableString mSpannableString = new SpannableString(sb);
		TextAppearanceSpan mTextAppearanceSpan = new TextAppearanceSpan(null,
				0, Utils.dip2px(context, size), mColor, null);
		mSpannableString.setSpan(mTextAppearanceSpan, 0, mSpannableString.length(), 
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		return mSpannableString;
	}
	
	/**
	 * @function 设置文字的不同的颜色
	 * @function 通过获取上下文来设置字体大小
	 * @通过获取上下文来设置字体大小
	 * @param transmitedStr 字符串
	 * @param context 上下文信息
	 * @param size 要设置的字体大小
	 * @param color 改变的颜色
	 * */
	public static SpannableString setTextSameStyle(String transmitedStr,
			Context context, Float size, ColorStateList mColor){
		StringBuilder sb = new StringBuilder();
		sb.append(transmitedStr);
		SpannableString mSpannableString = new SpannableString(sb);
		TextAppearanceSpan mTextAppearanceSpan = new TextAppearanceSpan(null,
				0, Utils.dip2px(context, size), mColor, null);
		mSpannableString.setSpan(mTextAppearanceSpan, 0, mSpannableString.length(), 
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		return mSpannableString;
	}
	
	/**
	 * @function 设置文字的不同的颜色
	 * @function 通过获取上下文来设置字体大小
	 * @通过获取上下文来设置字体大小
	 * @param transmitedStr 字符串
	 * @param context 上下文信息
	 * @param size 要设置的字体大小
	 * @param color 改变的颜色
	 * */
	public static SpannableString setTextsSameStyle(String str1, String str2,
			Context context, float size, ColorStateList mColor){
		StringBuilder sb = new StringBuilder();
		sb.append(str1).append(str2);
		SpannableString mSpannableString = new SpannableString(sb);
		TextAppearanceSpan mTextAppearanceSpan = new TextAppearanceSpan(null,
				0, Utils.dip2px(context, size), mColor, null);
		mSpannableString.setSpan(mTextAppearanceSpan, 0, mSpannableString.length(), 
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		
		return mSpannableString;
	}
	
	
	
	/**
	 * @function 设置全部文字的相同的颜色
	 * @function 此方法直接设置文字大小，这个在不同手机的显示可能不准
	 * @通过获取上下文来设置字体大小
	 * @param transmitedStr 字符串
	 * @param size 要设置的字体大小
	 * @param color 改变的颜色
	 * */
	public static SpannableString setTextStyle2(String transmitedStr,
			int size, ColorStateList mColor){
		StringBuilder sb = new StringBuilder();
		sb.append(transmitedStr);
		SpannableString spanBuilder1 = new SpannableString(sb);
		TextAppearanceSpan mTextAppearanceSpan = new TextAppearanceSpan(null,
				0, size, mColor, null);
		spanBuilder1.setSpan(mTextAppearanceSpan, 0, spanBuilder1.length(), 
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		return spanBuilder1;
	}
	
	/**
	 * @function 设置文字的不同的颜色,此方法设置str1颜色与str2颜色不一样
	 * @通过获取上下文来设置字体大小
	 * @param str1 字符串1
	 * @param str2 字符串2
	 * @param context 上下文信息
	 * @param size 要设置的字体大小
	 * @param color1 改变的颜色1
	 * @param color2 改变的颜色2
	 * */
	public static SpannableString setTextDiffColorStyle(String str1, 
			String str2, Context context, float size,
			ColorStateList color1, ColorStateList color2){
		int length = str1.length();
		StringBuilder sb = new StringBuilder();
		sb.append(str1).append(str2);
		SpannableString mSpannableString = new SpannableString(sb);
		
		TextAppearanceSpan mTextAppearanceSpan1 = new TextAppearanceSpan(null,
				0, Utils.dip2px(context, size), color1, null);
		TextAppearanceSpan mTextAppearanceSpan2 = new TextAppearanceSpan(null,
				0, Utils.dip2px(context, size), color2, null);
		
		mSpannableString.setSpan(mTextAppearanceSpan1, 0, length, 
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		mSpannableString.setSpan(mTextAppearanceSpan2, length, 
				mSpannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		
		return mSpannableString;
	}
	
	/**
	 * @function 获取
	 * @return
	 * */
	public static boolean isLayoutRtl(View v) {
	    return ViewCompat.getLayoutDirection(v) == ViewCompat.LAYOUT_DIRECTION_RTL;
	}
	
	/**
	 * @function 获取
	 * @return
	 * */
	public static int getStart(View v) {
		return getStart(v, false);
	}

	/**
	 * @function 获取
	 * @return
	 * */
	public static int getStart(View v, boolean withoutPadding) {
		if (v == null) {
			return 0;
		}
	    if (isLayoutRtl(v)) {
	      return (withoutPadding) ? v.getRight() - getPaddingStart(v) : v.getRight();
	    } else {
	      return (withoutPadding) ? v.getLeft() + getPaddingStart(v) : v.getLeft();
	    }
	}

	/**
	 * @function 获取
	 * @return
	 * */
	public static int getEnd(View v) {
	    return getEnd(v, false);
	}

	/**
	 * @function 获取
	 * @return
	 * */
	public static int getEnd(View v, boolean withoutPadding) {
		if (v == null) {
			return 0;
		}
		if (isLayoutRtl(v)) {
			return (withoutPadding) ? v.getLeft() + getPaddingEnd(v) : v.getLeft();
		} else {
			return (withoutPadding) ? v.getRight() - getPaddingEnd(v) : v.getRight();
		}
	}

	/**
	 * @function 获取
	 * @return
	 * */
	public static int getPaddingStart(View v) {
		if (v == null) {
			return 0;
		}
		return ViewCompat.getPaddingStart(v);
	}

	/**
	 * @function 获取
	 * @return
	 * */
	public static int getPaddingEnd(View v) {
		if (v == null) {
			return 0;
	    }
	    return ViewCompat.getPaddingEnd(v);
	}
	
	/**
	 * @function 获取
	 * @return
	 * */
	public static int getMarginEnd(View v) {
		if (v == null) {
			return 0;
	    }
	    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
	    return MarginLayoutParamsCompat.getMarginEnd(lp);
	}
  
	/**
	 * @function 获取
	 * @return
	 * */
	public static int getMeasuredWidth(View v) {
	    return (v == null) ? 0 : v.getMeasuredWidth();
	}
  
	/**
	 * @function 获取
	 * @return
	 * */
	public static int getMarginStart(View v) {
		if (v == null) {
			return 0;
		}
		ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
		return MarginLayoutParamsCompat.getMarginStart(lp);
	}
	
	/**
	 * @function 获取
	 * @return
	 * */
	public static int getWidth(View v) {
		return (v == null) ? 0 : v.getWidth();
	}
	
	/**
	 * @function 获取
	 * @return
	 * */
	public static int getMarginHorizontally(View v) {
	    if (v == null) {
	      return 0;
	    }
	    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
	    return MarginLayoutParamsCompat.getMarginStart(lp) + MarginLayoutParamsCompat.getMarginEnd(lp);
	}
	
	/**
	 * @function 获取
	 * @return
	 * */
	public static int getWidthWithMargin(View v) {
		return getWidth(v) + getMarginHorizontally(v);
	}

	/**
	 * @function 获取
	 * @return
	 * */
	public static int getPaddingHorizontally(View v) {
	    if (v == null) {
	      return 0;
	    }
	    return v.getPaddingLeft() + v.getPaddingRight();
	}
	
	//获得画干扰直线的位置
	public static int[] getLine(int height, int width)
	{
		int [] tempCheckNum = {0, 0 ,0, 0, 0};
		for(int i = 0; i < 4; i+=2)
			{
			tempCheckNum[i] = (int) (Math.random() * width);
			tempCheckNum[i + 1] = (int) (Math.random() * height);
			}
		return tempCheckNum;
	}
	
	//获得干扰点的位置
	public static int[] getPoint(int height, int width)
	{
		int [] tempCheckNum = {0, 0, 0, 0, 0};
		tempCheckNum[0] = (int) (Math.random() * width);
		tempCheckNum[1] = (int) (Math.random() * height);
		return tempCheckNum;
	}
  
}
