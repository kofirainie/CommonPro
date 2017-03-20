package com.gpstrace.dlrc.model;

import java.io.Serializable;

/**
 * @author kofirainie
 * @function 
 * @date 日期
 * @param 参数
 */
public class TResponseNoticeListData implements Serializable{
	// region fields
	private Boolean is_send;	//是否已发送
	private String code;	//code向下传递的字段
	private String send_time;	//通知发送时间
	private String document_no;	//文案号
	private String title;	//通知标题
	// endregion
	
	// region property
	/**
	 * @function 获取是否已发送
	 * */
	public Boolean getIs_send() {
		return is_send;
	}
	
	/**
	 * @function 设置是否已发送
	 * */
	public void setIs_send(Boolean is_send) {
		this.is_send = is_send;
	}
	
	/**
	 * @function 获取code向下传递的字段
	 * */
	public String getCode() {
		return code;
	}
	
	/**
	 * @function 设置code向下传递的字段
	 * */
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * @function 获取通知发送时间
	 * */
	public String getSend_time() {
		return send_time;
	}
	
	/**
	 * @function 设置通知发送时间
	 * */
	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}
	
	/**
	 * @function 获取文案号
	 * */
	public String getDocument_no() {
		return document_no;
	}
	
	/**
	 * @function 设置文案号
	 * */
	public void setDocument_no(String document_no) {
		this.document_no = document_no;
	}
	
	/**
	 * @function 获取通知标题
	 * */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @function 设置通知标题
	 * */
	public void setTitle(String title) {
		this.title = title;
	}
	// endregion
	
	// region method
	
	// endregion
}
