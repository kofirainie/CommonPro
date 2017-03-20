package com.gpstrace.dlrc.model;

/**
 * @闪屏内容结构体
 * @author YunZ
 * 
 */
public class SplashContent
{
	// region fields

	protected SplashArticleContent content;

	// endregion

	// region propertys

	/**
	 * @获取文章对应的图片的url
	 * @return
	 */
	public String getImageUrl()
	{
		return content.getArticleImage().getImage().getUrl();
	}
	
	/**
	 * @获取文章对应的图片的格式
	 * @return
	 */
	public String getImageFormat()
	{
		return content.getArticleImage().getImage().getFormat();
	}

	/**
	 * @获取文章对应的图片的更新时间
	 * @return
	 */
	public long getImageTime()
	{
		return content.getArticleImage().getImage().getTime();
	}

	// endregion
}
