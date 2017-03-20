package com.gpstrace.dlrc.activity;

import java.util.ArrayList;
import java.util.List;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.adapter.SchooleNoticeAdapter;
import com.gpstrace.dlrc.base.ActivityBase;
import com.gpstrace.dlrc.listener.OnNewsListener;
import com.gpstrace.dlrc.model.TResponseBase;
import com.gpstrace.dlrc.view.WaterfallListView;
import com.gpstrace.dlrc.view.WaterfallListView.IWaterfallListViewListener;

import android.view.View;
import android.view.View.OnClickListener;


public class TabHostFragmentActivity extends ActivityBase implements
		IWaterfallListViewListener{
	// region fields
	private String[] mapTitleArr;
	private List<List<TResponseBase>> mapItemLists;
	
	//作废
	private List<TResponseBase> notices;
	private SchooleNoticeAdapter noticeAdapter;
	
	
	private WaterfallListView mWaterfallView;
	
	// endregion
	
	@Override
	public void onRefresh() {
	}

	@Override
	public void onLoadMore() {
	}

	@Override
	public void onScroll(int scrollY) {
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void init() {
		setContentView(R.layout.activity_tab_host_fragment_layout);
		super.init();
		
		initBaseData();
		setHeader();
		setMapview();
		loadView();
	}
	
	/**
	 * 初始化基本数据
	 * */
	private void initBaseData(){
		notices = new ArrayList<TResponseBase>();
		
		notices.add(new TResponseBase(1,"多页切换TabHost"));
		notices.add(new TResponseBase(2,"对话框(dialog)"));
		notices.add(new TResponseBase(3,"按钮(Button)"));
		notices.add(new TResponseBase(4,"日历(Calendar)"));
		notices.add(new TResponseBase(5,"相机(Camera)"));
	}
	
	/**
	 * @初始化activity顶部Header 
	 */
	private void setHeader(){
		mFirstHeader.setVisibility(View.VISIBLE);
		
		mFirstTvTitle.setVisibility(View.VISIBLE);
		mFirstTvTitle.setText(R.string.app_name);
		
		mFirstTvBack.setText("");	
	}
	
	/**
	 * @function 设置地图页面
	 * */
	private void setMapview(){
		mWaterfallView = (WaterfallListView) findViewById(R.id.main_list_view);
		mWaterfallView.setPullLoadEnable(true);
		mWaterfallView.setWaterfallListViewListener(this, 0);
		
		addGridView();
					
		notices = new ArrayList<TResponseBase>();
		noticeAdapter = new SchooleNoticeAdapter(this, notices, 
				R.layout.waterfall_notice_item_layout);
		noticeAdapter.setOnNewsListener(mOnNoticeListener);
		
		mWaterfallView.setAdapter(noticeAdapter);
	}
	
	/**
	 * @function 添加格子视图
	 * */
	private void addGridView(){
		
	}
	
	/**
	 * @function 加载视图
	 * */
	private void loadView(){
		
	}
	
	private void gotoDiffPage(int position){
		switch (position) {
		case 1:
			break;
		default:
			break;
		}
	}
	
	/**
	 * @function item点击事件
	 */
	OnNewsListener mOnNoticeListener = new OnNewsListener(){
		@Override
		public void onOpenNews(Object item, int position, Object arg){
			gotoDiffPage(position);
		}
	};
	
	/**
	 * @function 控件点击事件
	 * */
	OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch ((Integer)v.getTag()) {
			default:
				break;
			}
		}
	};
}
