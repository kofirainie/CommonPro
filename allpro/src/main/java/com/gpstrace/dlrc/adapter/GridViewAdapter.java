package com.gpstrace.dlrc.adapter;

import java.util.List;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.adapter.GridViewAdapter.OnGridItemClickListener;
import com.gpstrace.dlrc.adapter.GridViewAdapter.ViewHolder;
import com.gpstrace.dlrc.model.TResponseBase;
import com.gpstrace.dlrc.provider.ImageProvider;
import com.gpstrace.dlrc.provider.Utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author kofirainie
 * @function 【经验区】的八宫格的GridViewAdapter
 * @params null
 */
public class GridViewAdapter extends BaseAdapter {
	
	// region fields	
	private final int WHAT_CHANGE = 10;// 数据变化
	private Context mContext;
    private List<TResponseBase> mDatas;
    private LayoutInflater mLayoutInflater;
    private int mIndex;	//页数下标,从0开始(通俗讲第几页)
    private int mPageSize;	//每页显示最大条目个数 ,默认是dimes.xml里 HomePageHeaderColumn 属性值的两倍(每页多少个图标)
    private OnGridItemClickListener mOnGridItemClickListener;
    private int itemHeight;
    private ViewHolder viewHolder;
    // endregion
    
    // region class
    public interface OnGridItemClickListener{
    	void onItemClick(View view, int position, Object arg);
    };
    
    class ViewHolder {
    	public LinearLayout itemLayout; 
        public TextView itemTitle;
        public ImageView itemImage;
    }    
    // endregion
    
    // region method
    public void setOnGridItemClickListener(OnGridItemClickListener mOnGridItemClickListener){
    	this.mOnGridItemClickListener = mOnGridItemClickListener;
    }

    public GridViewAdapter(Context context, List<TResponseBase> mDatas, int mIndex) {
    	this.mContext = context;
        this.mDatas = mDatas;
        mLayoutInflater = LayoutInflater.from(context);
        this.mIndex = mIndex;
        mPageSize = context.getResources().getInteger(R.integer.HomePageHeaderColumn) * 2;         
    }
    // endregion
    
    
 // region @Override
    /**
     * 先判断数据集的大小是否足够显示满本页？mDatas.size() > (mIndex+1)*mPageSize,
     * 如果够，则直接返回每一页显示的最大条目个数mPageSize,
     * 如果不够，则有几项返回几,(mDatas.size() - mIndex * mPageSize);(说白了 最后一页就显示剩余item)
     */
    @Override
    public int getCount() {
    	if (mDatas == null) {
			return 0;
		}
        return mDatas.size() > (mIndex + 1) * mPageSize ? mPageSize : (mDatas.size() - mIndex * mPageSize);

    }

    @Override
    public Object getItem(int position) {
    	if (position < 0 || position >= mDatas.size()) {
			return null;
		}
        return mDatas.get(position + mIndex * mPageSize);
    }

    @Override
    public long getItemId(int position) {
        return position + mIndex * mPageSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.common_gridview_item_layout, 
            		parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemLayout = (LinearLayout)convertView.findViewById(R.id.common_gridview_item_ll);
            viewHolder.itemTitle = (TextView) convertView.findViewById(R.id.common_gridview_item_text);
            viewHolder.itemImage = (ImageView) convertView.findViewById(R.id.common_gridview_item_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        /**
         * 在给View绑定显示的数据时，计算正确的position = position + mIndex * mPageSize，
         */
        final int pos = position + mIndex * mPageSize;
        final TResponseBase itemData = mDatas.get(pos);
        viewHolder.itemLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				mOnGridItemClickListener.onItemClick(v, pos, itemData);
			}
		});
        itemHeight = viewHolder.itemLayout.getHeight();              
        
        if (itemData.isLocalRes()) {
        	viewHolder.itemImage.setImageResource(itemData.getLocalImg()); 
        	viewHolder.itemTitle.setText(itemData.getLocalText());
        	Utils.setTextSameStyle(viewHolder.itemTitle.getText().toString(), mContext, 
        			Float.valueOf(20), ColorStateList.valueOf(0xFF747474));
//        	viewHolder.itemTitle.setTextColor(ColorStateList.valueOf(0xFF747474));
		} else {
	        if (!"".equals(itemData.getImage())) {
	        	ImageProvider.Loader.displayImage(itemData.getImage(), 
	        			viewHolder.itemImage, ImageProvider.NormalCoverOptions);
			} else {
				viewHolder.itemImage.setImageResource(R.drawable.default_image_while_online_image_empty);
			}
	        viewHolder.itemTitle.setText(itemData.getName());
		}
        
        return convertView;
    }
    
	/**
	 * @添加最新数据
	 * @param datas
	 */
	public void addItemLast(List<TResponseBase> datas)
	{
		for (int i = 0; i < datas.size(); i++)
		{
			boolean isExist = false;
			for (TResponseBase newModel : mDatas)
			{
				if (newModel.getCode().equals(datas.get(i).getCode()))
				{
					isExist = true;
					break;
				}
			}

			if (!isExist)
			{
				mDatas.add(datas.get(i));
			}
		}
	}
	
	/**
	 * @function 清空当前数据
	 * */
	public void clearData(){
		mDatas.clear();
	}
	
	/**
	 * @function 更新整个数据源
	 * */
	public void updateListDatas(List<TResponseBase> listData){
		if (null != listData && listData.size() > 0) {
//			clearData();
			for(int i = 0; i < listData.size(); i++){
//				if (mDatas.size() <= i) {
//					Message msg = new Message();
//					msg.what = WHAT_CHANGE;
//					msg.arg1 = i+10;
//					mDatas.set(i, listData.get(i));
//					mHandler.sendMessage(msg);
//				} else {
//					Message msg = new Message();
//					msg.what = WHAT_CHANGE;
//					msg.arg1 = i+10;
//					mDatas.add(i, listData.get(i));
//					mHandler.sendMessage(msg);
//				}
				Message msg = new Message();
				msg.what = WHAT_CHANGE;
				msg.arg1 = i+10;
				mDatas.set(i, listData.get(i));
				mHandler.sendMessage(msg);
			}
		}
	}
	
	/**
	 * @function 更新指定item
	 * */
	private void updateItem(int index){
		if (null != mDatas && mDatas.size() > 0) {
			View convertView = mLayoutInflater.inflate(R.layout.common_gridview_item_layout, 
            		null, false);;
            viewHolder = new ViewHolder();
            viewHolder.itemLayout = (LinearLayout)convertView.findViewById(R.id.common_gridview_item_ll);
            viewHolder.itemTitle = (TextView) convertView.findViewById(R.id.common_gridview_item_text);
            viewHolder.itemImage = (ImageView) convertView.findViewById(R.id.common_gridview_item_image);
            convertView.setTag(viewHolder);
			
			TResponseBase itemdata = (TResponseBase)getItem(index);
			viewHolder.itemImage.setImageResource(itemdata.getLocalImg()); 
        	viewHolder.itemTitle.setText(itemdata.getLocalText());
        	Utils.setTextSameStyle(viewHolder.itemTitle.getText().toString(), mContext, 
        			Float.valueOf(20), ColorStateList.valueOf(0xFF747474));
		} else {
			return;
		}
	}
	
	/**
	 * @function 获取当前gridview的一个item的高度
	 * */
	public int getRowHeight(){
		return Utils.dip2px(mContext, this.itemHeight) + Utils.dip2px(mContext, 2.5f);//1+1+0.5，主要是padding及行间距;	
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what) {
				case WHAT_CHANGE:
					updateItem(msg.arg1-10);
					break;
				default:
					break;
			}
		}
	};
 // endregion
}