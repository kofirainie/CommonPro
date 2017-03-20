package com.gpstrace.dlrc.model;

import java.io.Serializable;

/**
 * @function 同学派基本返回数据
 * @author kofirainie
 * @param null
 */
public class TResponseBase implements Serializable {
	// region fields

	private int id;
	private String name;



	private String status;
	private String image;
	private int localImg;	//本地图片资源
	private String localText; 	//本地文字资源
	private boolean isLocalRes = false; // 是否为本地资源，一般不是，true时为本地资源
	private String code;	//本地文件标识
	// endregion

	// region property
	/**
	 * @function 获取
	 * @return
	 * */
	public int getId() {
		return id;
	}

	public TResponseBase() {
		super();
	}

	public TResponseBase(int localImg, String localText, boolean isLocalRes) {
//		super();
		this.localImg = localImg;
		this.localText = localText;
		this.isLocalRes = isLocalRes;
		this.code = String.valueOf((new StringBuilder().append(localImg).append(localText)).toString());
	}

	public TResponseBase(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public TResponseBase(int id,String name, String status) {
		this.id = id;
		this.status = status;
		this.name = name;
	}

	/**
	 * @function 设置
	 * @param 
	 * */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @function 获取
	 * @return
	 * */
	public String getName() {
		return name;
	}

	/**
	 * @function 设置
	 * @param 
	 * */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @function 获取状态
	 * @param
	 * */
	public String getStatus() {
		return status;
	}

	/**
	 * @function 设置状态
	 * @param
	 * */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @function 获取
	 * @return
	 * */
	public String getImage() {
		return image;
	}

	/**
	 * @function 设置
	 * @param 
	 * */
	public void setImage(String image) {
		this.image = image;
	}
	
	/**
	 * @function 获取本地图片资源
	 * @return
	 * */
	public int getLocalImg() {
		return localImg;
	}

	/**
	 * @function 设置本地图片资源
	 * @param 
	 * */
	public void setLocalImg(int localImg) {
		this.localImg = localImg;
	}
	
	/**
	 * @function 获取本地文字资源
	 * @return
	 * */
	public String getLocalText() {
		return localText;
	}


	/**
	 * @function 设置本地文字资源
	 * @param 
	 * */
	public void setLocalText(String localText) {
		this.localText = localText;
	}

	/**
	 * @function 获取是否为本地资源
	 * @return
	 * */
	public boolean isLocalRes() {
		return isLocalRes;
	}


	/**
	 * @function 设置是否为本地资源
	 * @param 
	 * */
	public void setLocalRes(boolean isLocalRes) {
		this.isLocalRes = isLocalRes;
	}
	
	/**
	 * @function 获取本地文件标识
	 * @return
	 * */
	public String getCode() {
		return code;
	}

	/**
	 * @function 设置本本地文件标识
	 * @param 
	 * */
	public void setCode(String code) {
		this.code = code;
	}

	// endregion

	// region method

	// endregion

}
