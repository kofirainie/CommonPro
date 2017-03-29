package com.gpstrace.dlrc.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.listener.OnNewsListener;
import com.gpstrace.dlrc.model.TResponseBase;
import com.gpstrace.dlrc.provider.Utils;

import java.util.List;

/**
 * @咨询适配器
 * @author YunZ
 * 
 */
public class SchooleNoticeAdapter extends BaseAdapter
{
	// region
	private final int STATUS_STR_ARR_SIZE = 3;
	private LayoutInflater mInflater;
	private int mItemResource;
	private Context mContext;
	private int maxItemHeight;
	private List<TResponseBase> mItems;
	private ViewHolder viewHolder = null;

	private OnNewsListener mOnNewsListener;
	private String transmitedStr;
	private String untransmit;
	private ColorStateList blueColor;
	private ColorStateList blackColor;

	private Drawable yellowStyle;
	private Drawable blueStyle;
	private Drawable redStyle;
	//下载,下载中，完成
	private String[] statusStrArr = {"下载", "下载中", "完成" };

	// endregion

	// region class

	private class ViewHolder
	{
		TextView index;
		TextView project;
		TextView noticeTime;
		TextView status;
		RelativeLayout noticeLayout;
	}

	// endregion

	/**
	 * @构造函数
	 * @param context
	 * @param data
	 * @param resource
	 */
	public SchooleNoticeAdapter(Context context, List<TResponseBase> data,
			int resource)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mItemResource = resource;
		this.mItems = data;
		this.maxItemHeight = getItemHeight();
		transmitedStr = mContext.getResources()
        		.getString(R.string.notice_content_transmited_tip);
		untransmit = mContext.getResources()
        		.getString(R.string.notice_content_untransmit_tip);	     
		this.blueColor = ColorStateList.valueOf(0xFF45BCE3);
	    this.blackColor = ColorStateList.valueOf(0xFF000000);

//		yellowStyle =this.mContext.getResources().getDrawable(
//				R.drawable.common_item_text_go_download_color_background_selector);
//		blueStyle =this.mContext.getResources().getDrawable(
//						R.drawable.common_item_text_downloading_color_background_selector);
//		redStyle = this.mContext.getResources().getDrawable(
//						R.drawable.common_item_text_finish_color_background_selector);

//		yellowStyle =this.mContext.getResources().getDrawable(
//				R.drawable.common_item_text_go_download_color_background_selector);
//		blueStyle =this.mContext.getResources().getDrawable(
//				R.drawable.common_item_text_go_download_color_background_selector);
//		redStyle = this.mContext.getResources().getDrawable(
//				R.drawable.common_item_text_go_download_color_background_selector);

//		yellowStyle =this.mContext.getResources().getDrawable(
//				R.drawable.dialog_button_left_background_selector);
//		blueStyle =this.mContext.getResources().getDrawable(
//				R.drawable.dialog_button_left_background_selector);
//		redStyle = this.mContext.getResources().getDrawable(
//				R.drawable.dialog_button_left_background_selector);

	}

	// region override

	@Override
	public int getCount()
	{
		if (mItems == null)
		{
			return 0;
		}

		return mItems.size();
	}

	@Override
	public Object getItem(int position)
	{
		if (position < 0 || position >= mItems.size())
		{
			return null;
		}
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(mItemResource, null);
			viewHolder.index = (TextView) convertView.findViewById(R.id.trace_tag_tv);
			viewHolder.project = (TextView) convertView.findViewById(R.id.trace_date_tv);
			viewHolder.noticeTime = (TextView) convertView.findViewById(R.id.trace_time_tv);
			viewHolder.status = (TextView)convertView.findViewById(R.id.trace_status_tv) ;
			viewHolder.noticeLayout = (RelativeLayout) convertView.findViewById(R.id.trace_date_time_whole_rl);
//			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder
//					.noticeLayout.getLayoutParams();
//			params.height = maxItemHeight;
//			viewHolder.noticeLayout.setLayoutParams(params);

			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final TResponseBase notice = mItems.get(position);
		int statusTag =getTraceStatus(notice.getStatus());
		final int mPosition = position;

		viewHolder.noticeLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (null != mOnNewsListener)
				{
					mOnNewsListener.onOpenNews(v, mPosition, notice);
				}

			}
		});	 
		
		viewHolder.index.setText(String.valueOf(notice.getId()));
		viewHolder.project.setText(notice.getName());
		viewHolder.noticeTime.setText("");
//		viewHolder.status.setText(notice.getStatus());
		setStatusFontStyle(statusTag, notice.getStatus());

		return convertView;
	}

	// endregion

	/**
	 * @function 获取item当前的状态
	 * 0下载,1下载中，2完成
	 * */
	private int getTraceStatus(String statusStr){
		int statusTag = -1;

		for(int i = 0; i <STATUS_STR_ARR_SIZE; i++){
			if(statusStrArr[i].equals(statusStr))
				statusTag = i;
		}

		return statusTag;
	}

	/**
	 * @function 设置状态显示的样式：1.下载中，2.完成，3.下载
	 * case 3:
	 * */
	private void setStatusFontStyle(int statusTag, String statusStr){
		viewHolder.status.setText(statusStr);
		//设置状态显示的样式：0下载,1下载中，2完成
		switch (statusTag){
			case -1:    //假如获取错误时的显示
				viewHolder.status.setTextColor(0xFF232323);
//				viewHolder.status.setBackgroundDrawable(this.mContext.getResources().getDrawable(
//						R.drawable.common_item_text_color_background_selector));
//				viewHolder.status.setBackgroundResource(
//						R.drawable.common_item_text_color_background_selector);
				break;
			case 0:
				viewHolder.status.setTextColor(0xFF258dcd);
//				viewHolder.status.setBackgroundDrawable(this.mContext.getResources().getDrawable(
//						R.drawable.common_item_text_go_download_color_background_selector));
//				viewHolder.status.setBackgroundDrawable(yellowStyle);
//				viewHolder.status.setBackgroundResource(
//						R.drawable.common_item_text_go_download_color_background_selector);
				break;
			case 1:
				viewHolder.status.setTextColor(0xFFe4a942);
//				viewHolder.status.setBackgroundDrawable(this.mContext.getResources().getDrawable(
//						R.drawable.common_item_text_downloading_color_background_selector));
//				viewHolder.status.setBackgroundDrawable(blueStyle);
//				viewHolder.status.setBackgroundResource(
//						R.drawable.common_item_text_downloading_color_background_selector);
				break;
			case 2:
				viewHolder.status.setTextColor(0xFFf0658f);
//				viewHolder.status.setBackgroundDrawable(this.mContext.getResources().getDrawable(
//						R.drawable.common_item_text_finish_color_background_selector));
//				viewHolder.status.setBackgroundDrawable(redStyle);
//				viewHolder.status.setBackgroundResource(
//						R.drawable.common_item_text_finish_color_background_selector);
				break;
			default:
				break;
		}
	}

	// region methods

	/**
	 * @获取item的高度
	 * @return
	 */
	private int getItemHeight()
	{
		int titleHeight = Utils.dip2px(mContext, Utils.getFontHeight(14));
		int schemeHeight = Utils.dip2px(mContext, Utils.getFontHeight(10));
		int timeHeight = Utils.dip2px(mContext, Utils.getFontHeight(10));
		int otherpadding = Utils.dip2px(mContext, 1f);

		return titleHeight + schemeHeight + timeHeight + otherpadding;
	}

	/**
	 * @添加最新数据
	 * @param datas
	 */
	public void addItemLast(List<TResponseBase> datas){
		for (int i = 0; i < datas.size(); i++){
			boolean isExist = false;
			for (TResponseBase newModel : mItems){
				if (newModel.getCode().equals(datas.get(i).getCode())){
					isExist = true;
					break;
				}
			}
			if (!isExist){
				mItems.add(datas.get(i));
			}
		}		
	}
	
	public int getmItemsSize(){
		return mItems.size();
	}

	/**
	 * @设置门店打开界面
	 * @门店就是街友圈
	 * @param listener
	 */
	public void setOnNewsListener(OnNewsListener listener)
	{
		this.mOnNewsListener = listener;
	}

	// endregion

}
