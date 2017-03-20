package com.gpstrace.dlrc.model;

import java.io.Serializable;
import java.util.List;

/**
 * @版本检查响应
 * @author LC125
 *
 */
public class ResponseCheckVersion implements Serializable
{
	// region fields

	private String version;// 版本字符串
	private String url;// 版本更新网页字符串
	private float main_img_time;	//闪屏页持续时间
	private List<String> deprecated;	//废弃的版本号列表
	private boolean isDepreceted = false;	//是否在丢弃列表deprecated中，true为在其中，false为不在

	// endregion

	// region propertys

	/**
	 * @获取版本字符串
	 * @return
	 */
	public String getVersion()
	{
		return this.version;
	}
	
	/**
	 * @function 设置版本字符串
	 * @param version 版本号
	 * */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @获取版本更新网页字符串
	 * @return
	 */
	public String getUrl()
	{
		return this.url;
	}
	

	/**
	 * @function 获取闪屏页持续时间
	 * @return
	 * */
	public float getMain_img_time() {
		return main_img_time;
	}

	/**
	 * @function 设置闪屏页持续时间
	 * @param 
	 * */
	public void setMain_img_time(float main_img_time) {
		this.main_img_time = main_img_time;
	}

	/**
	 * @获取废弃的版本号列表
	 * @return
	 */
	public List<String> getDeprecated() {
		return deprecated;
	}

	/**
	 * @获取废弃的版本号列表
	 * @return
	 */
	public void setDeprecated(List<String> deprecated) {
		this.deprecated = deprecated;
	}
	
	/**
	 * @function 获取
	 * @return
	 * */
	public boolean isDepreceted() {
		return isDepreceted;
	}

	/**
	 * @function 设置
	 * @param 
	 * */
	public void setDepreceted(boolean isDepreceted) {
		this.isDepreceted = isDepreceted;
	}

	// endregion
}
