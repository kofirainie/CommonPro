package com.gpstrace.dlrc.model;

import java.util.List;

/**
 * @author kofirainie
 * @function 辅导员收到的学校通知列表，app端辅导员查看收到的学校通知列表接口（需要辅导员身份）
 * @date 日期
 * @param 参数
 */
public class TResponseNoticeListBean extends BaseResponse{
	// region fields
	private List<TResponseNoticeListData> data;
	// endregion
	
	// region property
	/**
	 * @function 获取辅导员收到的学校通知列表
	 * */
	public List<TResponseNoticeListData> getData() {
		return data;
	}

	/**
	 * @function 设置辅导员收到的学校通知列表
	 * */
	public void setData(List<TResponseNoticeListData> data) {
		this.data = data;
	}
	// endregion
	
	// region method
	
	// endregion
}
