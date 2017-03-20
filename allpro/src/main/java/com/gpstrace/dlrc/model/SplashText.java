package com.gpstrace.dlrc.model;

/**
 * @动态文本结构体
 * @author YunZ
 * 
 */
public class SplashText
{
	// region fields

	protected SplashTextDetial magazine_text;// 动态文本详情

	// endregion

	// region propertys

	/**
	 * @获取文本更新时间
	 * @return
	 */
	public long getTime()
	{
		return magazine_text.update_time;
	}

	// endregion

}
