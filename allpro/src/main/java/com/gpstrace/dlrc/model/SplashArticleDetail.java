package com.gpstrace.dlrc.model;

/**
 * @闪屏页文章详情
 * @author YunZ
 * 
 */
public class SplashArticleDetail
{
	// region fields

	protected String tag;
	protected String name;
	protected SplashArticle article;

	// endregion

	// region propertys

	/**
	 * @获取文章详情
	 * @return
	 */
	public int getArticleId()
	{
		return article.getId();
	}

	// endregion

}
