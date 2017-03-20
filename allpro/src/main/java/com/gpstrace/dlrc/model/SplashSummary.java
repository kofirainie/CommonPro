package com.gpstrace.dlrc.model;

/**
 * @闪屏概要结构体
 * @author YunZ
 * 
 */
public class SplashSummary
{
	// region fields

	protected String format_version;
	protected String static_host;
	protected SplashListArticle list_article;
	protected SplashText list_update_time;

	// endregion

	// region

	/**
	 * @获取闪屏页文章id
	 * @return
	 */
	public int getArticleId()
	{
		return list_article.getDetail().getArticleId();
	}

	/**
	 * @获取动态文本内容
	 * @return
	 */
	public SplashText getSplashText()
	{
		return list_update_time;
	}

	/**
	 * @获取动态文本更新时间
	 * @return
	 */
	public long getTime()
	{
		return list_update_time.getTime();
	}

	// endregion
}
