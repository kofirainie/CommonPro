package com.gpstrace.dlrc.model;

/**
 * @请求响应基类
 * @author YunZ
 * 
 */
public class BaseResponse 
{
	// region fields

	protected int code = -1000;// 响应码,0为成功，-1为连接状态失效，其他为操作错误
	protected String desc;// 响应码描述
	protected long time;// 后台处理时间
	protected String message;

	// endregion

	// region propertys

	/**
	 * @获取服务器返回码
	 * @return
	 */
	public int getCode()
	{
		return this.code;
	}
	/**
	 * @设置服务器返回码
	 * @return
	 */
	public void setCode(int code)
	{
		this.code=code;
	}
	/**
	 * @获取返回描述
	 * @return
	 */
	public String getDesc()
	{
		return this.desc;
	}

	/**
	 * @操作是否成功，若不成功，则需要通过返回码去检测
	 * @return
	 */
	public Boolean isDone()
	{
		return code == 0 ? true : false;
	}

	/**
	 * @徐闯增加
	 * @获取网络访问时间
	 */
	public long getTime()
	{
		return this.time;
	}

	/**
	 * @function 设置描述信息
	 * @暂时用于调试
	 * @author kofirainie添加
	 * @本app已经去掉本函数的仅有一个地方引用
	 * */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	// endregion
}
