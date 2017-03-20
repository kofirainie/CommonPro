package com.gpstrace.dlrc.model;

import com.gpstrace.dlrc.finals.UrlFinals;

/**
 * @闪屏页照片结构体
 * @author YunZ
 * 
 */
public class SplashImage
{
	// region fields

	protected int id;// 闪屏页图片id
	protected String type;// 闪屏页图片的类型
	protected long update_time;// 闪屏页图片的更新时间
	protected String format;

	// endregion

	// region propertys

	/**
	 * @获取闪屏页图片URL
	 * @return
	 */
	public String getUrl()
	{
		return String.format(UrlFinals.SPLASH_IMAGE_URL_STR, id % 100, id,
				format);// 由id转化产生url
	}
	
	/**
	 * @获取图片格式
	 * @return
	 */
	public String getFormat()
	{
		return this.format;
	}

	/**
	 * @获取闪屏页更新时间
	 * @return
	 */
	public long getTime()
	{
		return update_time;
	}

	// endregion
}
