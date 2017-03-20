package com.gpstrace.dlrc.activity;

import java.util.ArrayList;
import java.util.List;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.adapter.GridViewAdapter;
import com.gpstrace.dlrc.adapter.GridViewAdapter.OnGridItemClickListener;
import com.gpstrace.dlrc.base.ActivityBase;
import com.gpstrace.dlrc.model.TResponseBase;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class MapType1Create extends ActivityBase{
	// region fields
	private int MAX_MAP_PAGE = 7;
	
	//ViewPager部分
	private ViewPager viewPage;
	private LinearLayout mPointLayout;
	private List<View> mMenuBannerViewList;
	private PagerAdapter mPagerAdapter;
	private List<TResponseBase> viewPagerDatas;
	private int mpageCount;
	private ImageView[] mGuideIViews = null;// 滚动图片指示器-视图列表
	private ImageView mOneGuideIView = null;// 图片轮播指示器-个图
	
	
	private List<Boolean> isNewInits;
	private List<GridView> grids;
	private List<GridViewAdapter> GridViewAdapters;
	
	private int maxPage = 0;	//viewpage最大的页面数
	private int viewPageItemSize;
	
	private int MAX_PAGE_NUM = 1;  	//默认viewpager初始化最大的页数
	// endregion

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
		setContentView(R.layout.activity_main_layout);
		super.init();
		
		initBaseData();
		setHeader();
		iniViewPagerView();
	}
	
	/**
	 * 初始化基本数据
	 * */
	private void initBaseData(){
		String[] mapItem1 = getApplicationContext().getResources()
				.getStringArray(R.array.map_stypes_1);
		List<TResponseBase> TResponseBase1 = new ArrayList<TResponseBase>();
		TResponseBase1.add(new TResponseBase(R.drawable.a11, mapItem1[0], true));
		TResponseBase1.add(new TResponseBase(R.drawable.a12, mapItem1[1], true));
		TResponseBase1.add(new TResponseBase(R.drawable.a13, mapItem1[2], true));
		TResponseBase1.add(new TResponseBase(R.drawable.a14, mapItem1[3], true));
		TResponseBase1.add(new TResponseBase(R.drawable.a15, mapItem1[4], true));
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
	
	private void iniViewPagerView(){
		initViewPagerData();
		
		//viewpager初始化
		viewPage = (ViewPager)findViewById(R.id.common_viewpager);
		viewPage.setOnPageChangeListener(mOnPageChangeListener);
		
		// 以下为添加底端的页面指示器小圆点
		LinearLayout mGuideLayout = (LinearLayout) findViewById(
				R.id.common_guide_group_layout); // 滚动图片右下指示器视图
		mPointLayout = (LinearLayout) findViewById(
				R.id.common_guide_points_layout);
		mPointLayout.removeAllViews(); // 清除所有子视图
		
		mMenuBannerViewList = new ArrayList<View>();
		
		mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return mMenuBannerViewList.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(mMenuBannerViewList.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(mMenuBannerViewList.get(position));
				return mMenuBannerViewList.get(position);
			}
		};
		
		viewPage.setAdapter(mPagerAdapter);
		setMenuBannerVisible(false);
		
		setViewPage();
	}
	
	/**
	 * @function 初始化viewpager基本数据
	 * */
	private void initViewPagerData(){
		viewPagerDatas = new ArrayList<TResponseBase>();
		
		viewPageItemSize = viewPagerDatas.size();
		
		isNewInits = new ArrayList<Boolean>();
		grids = new ArrayList<GridView>();
		GridViewAdapters = new ArrayList<GridViewAdapter>();
		for(int i = 0; i < MAX_PAGE_NUM; i++){
			isNewInits.add(i, false);
			grids.add(i, null);
			GridViewAdapters.add(i, null);
		}
		
	}
	
	/**
	 * @设置menu Banner显示
	 * @param isVisible
	 * @param isStart
	 * @是否启动滚动显示
	 */
	private void setMenuBannerVisible(boolean isVisible) {
		if (isVisible) {
			viewPage.setVisibility(View.VISIBLE);
			viewPage.setCurrentItem(0);
		} else {
			viewPage.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * @menus 初始化九宫格，以及数据源变化时调用
	 * */
	private void setViewPage(){
		if (null != viewPagerDatas && viewPagerDatas.size() > 0) {
			viewPageItemSize = viewPagerDatas.size();
			// 一共的页数等于 总数/每页数量，并取整。
			mpageCount = (int) Math.ceil(viewPagerDatas.size() * 1.0 / 8);
			
			//假如页面数大于当前页面设置最大数：添加操作
			if (mpageCount > MAX_PAGE_NUM) {
				isNewInits.add(MAX_PAGE_NUM, false);
				grids.add(MAX_PAGE_NUM, null);
				GridViewAdapters.add(MAX_PAGE_NUM, null);
				++MAX_PAGE_NUM;
			}			

			mMenuBannerViewList.clear();
			
			for (int index = 0; index < mpageCount; index++) {
				if (isNewInits.get(index)) {
					GridViewAdapters.get(index).addItemLast(viewPagerDatas);
					mMenuBannerViewList.add(grids.get(index));
					
					//移除数据操作
					if (mpageCount < maxPage) {
						for(int curPage = mpageCount; curPage < maxPage; curPage++){
							GridViewAdapters.remove(curPage);
							isNewInits.remove(curPage);
							grids.remove(curPage);
							--maxPage;
							--MAX_PAGE_NUM;
						}
					}
				}else {
					// 每个页面都是inflate出一个新实例
					grids.set(index, (GridView) LayoutInflater.from(this)
							.inflate(R.layout.common_gridview_layout, null));
					GridViewAdapters.set(index, new GridViewAdapter(this, viewPagerDatas, index));
					GridViewAdapters.get(index).setOnGridItemClickListener(mMenuBannerItemListener);
					grids.get(index).setAdapter(GridViewAdapters.get(index));
					mMenuBannerViewList.add(grids.get(index));
					isNewInits.set(index, true);
					++maxPage;
				}
				GridViewAdapters.get(index).notifyDataSetChanged();
			}

			mPagerAdapter.notifyDataSetChanged();
			
			setIndatorPointView();
			setMenuBannerVisible(true);

		} else {
			viewPageItemSize = 0;
			mpageCount = 0;
			setIndatorPointView();
			setMenuBannerVisible(false);
		}
	}
	
	/**
	 * @function 生成页面指示小圆点视图
	 */
	private void setIndatorPointView() {
		mPointLayout.removeAllViews(); // 清除所有子视图
		
		mGuideIViews = new ImageView[mpageCount];
		for (int i = 0; i < mpageCount; i++) {
			mOneGuideIView = new ImageView(this);
			LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			layout.setMargins(8, 0, 8, 0);
			mOneGuideIView.setLayoutParams(layout);
			mGuideIViews[i] = mOneGuideIView;
			if (i == 0) {
				mGuideIViews[i].setBackgroundResource(R.drawable.banner_selected_ico);
			} else {
				mGuideIViews[i].setBackgroundResource(R.drawable.banner_normal_ico);
			}
			mPointLayout.addView(mGuideIViews[i]);
		}
	}
	
	/**
	 * @function viewpaper的监听事件,主要是监听小圆点的变化
	 */
	OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < mpageCount; i++) {
				if (arg0 == i) {
					mGuideIViews[arg0].setBackgroundResource(R.drawable.banner_selected_ico);
				} else {
					mGuideIViews[i].setBackgroundResource(R.drawable.banner_normal_ico);
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};
	
	OnGridItemClickListener mMenuBannerItemListener = new OnGridItemClickListener() {

		@Override
		public void onItemClick(View view, int position, Object arg) {
//			switch (viewPageItemSize) {
//			case 8:
//				clickCommonTeacherModeView(position);
//				break;
//			case 9:
//				clickStudentModeView(position);
//				break;
//			case 10:
//				clickInstructorModeView(position);
//				break;		
//			default:
//				break;
//			}
		}
	};
}
