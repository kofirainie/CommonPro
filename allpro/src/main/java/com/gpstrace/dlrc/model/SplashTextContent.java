package com.gpstrace.dlrc.model;

import java.io.Serializable;

/**
 * @动态文本结构体
 * @author YunZ
 * 
 */
public class SplashTextContent implements Serializable
{
	// region fields

	protected SplashTextName magazine_name;// 封面笔记名
	protected SplashTextTitle magazine_title;// 封面笔记标题
	protected SplashTextSubTitle content_title;// 封面笔记内页标题
	protected SplashTextSubContent content_text;// 封面笔记内页内容

	// endregion

	// region propertys

	/**
	 * @获取封面笔记名
	 * @return
	 */
	public String[] getNames()
	{
		return this.magazine_name.list;
	}

	/**
	 * @获取封面笔记标题
	 * @return
	 */
	public String[] getTitles()
	{
		return this.magazine_title.list;
	}

	/**
	 * @获取封面笔记内页子标题
	 * @return
	 */
	public String[] getSubTitles()
	{
		return this.content_title.list;
	}

	/**
	 * @获取封面笔记内页子标题
	 * @return
	 */
	public String[] getSubContents()
	{
		return this.content_text.list;
	}

	// endregion
}
