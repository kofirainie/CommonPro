package com.gpstrace.dlrc.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.adapter.GridViewAdapter;
import com.gpstrace.dlrc.adapter.GridViewAdapter.OnGridItemClickListener;
import com.gpstrace.dlrc.adapter.SchooleNoticeAdapter;
import com.gpstrace.dlrc.base.ActivityBase;
import com.gpstrace.dlrc.listener.OnNewsListener;
import com.gpstrace.dlrc.model.TResponseBase;
import com.gpstrace.dlrc.view.WaterfallListView;
import com.gpstrace.dlrc.view.WaterfallListView.IWaterfallListViewListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActivityBase implements
		IWaterfallListViewListener {
	// region fields
	private String[] mapTitleArr;
	private List<List<TResponseBase>> mapItemLists;

	//作废
	private List<TResponseBase> notices;
	private List<TResponseBase> curNotices;
	private SchooleNoticeAdapter noticeAdapter;

	//九宫格控件
	private View viewPageLayout;
	private ViewPager viewPage;
	private LinearLayout mPointLayout;
	private List<View> mMenuBannerViewList;
	private PagerAdapter mPagerAdapter;
	private List<TResponseBase> viewPagerDatas;
	private int mpageCount;
	private ImageView[] mGuideIViews = null;// 滚动图片指示器-视图列表
	private ImageView mOneGuideIView = null;// 图片轮播指示器-个图
	private Boolean isNoticeNew = false; //true有未转发,false没有未转发

	private List<Boolean> isNewInits;
	private List<GridView> grids;
	private List<GridViewAdapter> GridViewAdapters;

	private int maxPage = 0;    //viewpage最大的页面数
	private int viewPageItemSize;
	private int MAX_PAGE_NUM = 1;    //默认viewpager初始化最大的页数

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
		setContentView(R.layout.activity_main_layout);
		super.init();

		initBaseData();
		setHeader();
		setMapview();
		loadView();
	}

	/**
	 * 初始化基本数据
	 */
	private void initBaseData() {
		curNotices = new ArrayList<TResponseBase>();

		curNotices.add(new TResponseBase(1, "多页切换TabHost", "下载"));
		curNotices.add(new TResponseBase(2, "对话框(dialog)", "下载中"));
		curNotices.add(new TResponseBase(3, "按钮(Button)", "完成"));
		curNotices.add(new TResponseBase(4, "日历(Calendar)", "下载"));
		curNotices.add(new TResponseBase(5, "相机 (Camera)", "下载中"));
		curNotices.add(new TResponseBase(6, "图片高斯模糊（Blur）", "完成"));
		curNotices.add(new TResponseBase(7, "图像 (Image)", "下载"));
		curNotices.add(new TResponseBase(8, "自定义RecyclerView", "下载中"));
		curNotices.add(new TResponseBase(9, "下拉列表和自动提示", "完成"));
		curNotices.add(new TResponseBase(10, "地图 (Map)", "下载"));
		curNotices.add(new TResponseBase(11, "菜单 (Menu)", "下载中"));
		curNotices.add(new TResponseBase(12, "导航条 (actionbar)", "完成"));
		curNotices.add(new TResponseBase(13, "选择器 (Picker)", "下载"));
		curNotices.add(new TResponseBase(14, "进度条 (ProgressBar)", "下载中"));
		curNotices.add(new TResponseBase(15, "滚动视图 (ScrollView)", "完成"));
		curNotices.add(new TResponseBase(16, "ViewPager和ViewGroup", "下载"));
		curNotices.add(new TResponseBase(17, "拖动条（SeekBar）", "下载中"));
		curNotices.add(new TResponseBase(18, "网格(GridView)", "完成"));
		curNotices.add(new TResponseBase(19, "开关 (Switch)", "下载"));
		curNotices.add(new TResponseBase(20, "Gallery和ImageSwitcher", "下载中"));
		curNotices.add(new TResponseBase(21, "列表 (ListView)", "完成"));
		curNotices.add(new TResponseBase(22, "文字输入框 (EditText)", "下载"));
		curNotices.add(new TResponseBase(23, "文本显示 (TextView)", "下载中"));
		curNotices.add(new TResponseBase(24, "网页 (Webview)", "完成"));
		curNotices.add(new TResponseBase(25, "动画 (Animation)", "下载"));
		curNotices.add(new TResponseBase(26, "视图效果 (View Effects)", "下载中"));
		curNotices.add(new TResponseBase(27, "视图布局 (View Layout)", "完成"));
		curNotices.add(new TResponseBase(28, "视图切换 (View Transition)", "下载"));
		curNotices.add(new TResponseBase(29, "多媒体Media", "下载中"));
		curNotices.add(new TResponseBase(30, "图表 (Chart)", "完成"));
		curNotices.add(new TResponseBase(31, "游戏引擎 (game)", "下载"));
		curNotices.add(new TResponseBase(32, "重力感应 (CoreMotion)", "下载中"));
		curNotices.add(new TResponseBase(33, "数据库 (Database)", "完成"));
		curNotices.add(new TResponseBase(34, "绘图 (canvas)", "下载"));
		curNotices.add(new TResponseBase(35, "电子书 (eBook)", "下载中"));
		curNotices.add(new TResponseBase(36, "手势交互 (Gesture)", "完成"));
		curNotices.add(new TResponseBase(37, "引导页 (Intro&Guide View)", "下载"));
		curNotices.add(new TResponseBase(38, "网络 (Networking)", "下载中"));
		curNotices.add(new TResponseBase(39, "弹出视图 (Popup View)", "完成"));
		curNotices.add(new TResponseBase(40, "社交分享 (Socialization)", "下载"));
		curNotices.add(new TResponseBase(41, "其他 (Others)", "下载中"));
		curNotices.add(new TResponseBase(42, "插件机制", "完成"));
		curNotices.add(new TResponseBase(43, "开发框架", "下载"));
		curNotices.add(new TResponseBase(44, "完整源码", "下载中"));
		curNotices.add(new TResponseBase(45, "以上为全部，已经结束了", "完成"));
	}

	/**
	 * @初始化activity顶部Header
	 */
	private void setHeader() {
		mFirstHeader.setVisibility(View.VISIBLE);

		mFirstTvTitle.setVisibility(View.VISIBLE);
		mFirstTvTitle.setText(R.string.app_name);

		mFirstTvBack.setText("");
	}

	/**
	 * @function 设置地图页面
	 */
	private void setMapview() {
		mWaterfallView = (WaterfallListView) findViewById(R.id.main_list_view);
		mWaterfallView.setPullLoadEnable(true);
		mWaterfallView.setWaterfallListViewListener(this, 0);

		addGridView();
//		addGridView2();

		notices = new ArrayList<TResponseBase>();
		noticeAdapter = new SchooleNoticeAdapter(this, notices,
				R.layout.waterfall_notice_item_layout);
		noticeAdapter.setOnNewsListener(mOnNoticeListener);

		mWaterfallView.setAdapter(noticeAdapter);
	}

	/**
	 * @function 添加格子视图
	 */
	private void addGridView() {
		initViewPagerData();

		viewPageLayout = LayoutInflater.from(this).inflate(
				R.layout.common_viewpager_layout, null, false);

		//viewpager初始化
		viewPage = (ViewPager) viewPageLayout.findViewById(R.id.common_viewpager);
		viewPage.setOnPageChangeListener(mOnPageChangeListener);

		// 以下为添加底端的页面指示器小圆点
		LinearLayout mGuideLayout = (LinearLayout) viewPageLayout.findViewById(
				R.id.common_guide_group_layout); // 滚动图片右下指示器视图
		mPointLayout = (LinearLayout) viewPageLayout.findViewById(R.id.common_guide_points_layout);
		if(mPointLayout.getChildCount() > 0){
			mPointLayout.removeAllViews(); // 清除所有子视图
		}

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

		mWaterfallView.addHeaderView(viewPageLayout, null, false);
	}

	/**
	 * @function 初始化viewpager基本数据
	 */
	private void initViewPagerData() {
		viewPagerDatas = new ArrayList<TResponseBase>();

		viewPagerDatas.add(new TResponseBase(R.drawable.a101, "FragmentTabHost的使用", true));
		viewPagerDatas.add(new TResponseBase(R.drawable.a102, "一个模仿iOS中3D Touch效果的库", true));
		viewPagerDatas.add(new TResponseBase(R.drawable.a103, "高仿网易云音乐客户端的Home页面切换Tabhost", true));
		viewPagerDatas.add(new TResponseBase(R.drawable.a104, "SpringIndicator", true));
		viewPagerDatas.add(new TResponseBase(R.drawable.a105, "Fragment加入Pagerview效果", true));
		viewPagerDatas.add(new TResponseBase(R.drawable.a106, "实现二级导航菜单栏效果，FragmentTabHost嵌套", true));
		viewPagerDatas.add(new TResponseBase(R.drawable.a107, "MusicPlayerView", true));
		viewPagerDatas.add(new TResponseBase(R.drawable.a108, "AndroidRubberIndicator-master 滑动指示器", true));
		viewPagerDatas.add(new TResponseBase(R.drawable.a109, "android-floating-action-button-master", true));
		viewPagerDatas.add(new TResponseBase(R.drawable.a110, "labelview-master", true));
		viewPagerDatas.add(new TResponseBase(R.drawable.a111, "RippleEffect 点击渐变效果", true));

		viewPageItemSize = viewPagerDatas.size();

		isNewInits = new ArrayList<Boolean>();
		grids = new ArrayList<GridView>();
		GridViewAdapters = new ArrayList<GridViewAdapter>();
		for (int i = 0; i < MAX_PAGE_NUM; i++) {
			isNewInits.add(i, false);
			grids.add(i, null);
			GridViewAdapters.add(i, null);
		}
	}

	/**
	 * @param isVisible
	 * @设置menu Banner显示
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
	 */
	private void setViewPage() {
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
						for (int curPage = mpageCount; curPage < maxPage; curPage++) {
							GridViewAdapters.remove(curPage);
							isNewInits.remove(curPage);
							grids.remove(curPage);
							--maxPage;
							--MAX_PAGE_NUM;
						}
					}
				} else {
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
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
	 * @function 添加格子视图
	 */
	private void addGridView2() {
		View tipView = LayoutInflater.from(this).inflate(
				R.layout.delect_button_layout, null, false);

		Button mButton = (Button) tipView.findViewById(R.id.button_for_test_can_delect);
		mButton.setOnClickListener(mClickListener);


		mWaterfallView.addHeaderView(tipView, null, false);
	}

	/**
	 * @function 加载视图
	 */
	private void loadView() {
		if (null != notices) {
			notices.clear();
		}
		notices.addAll(curNotices);
		noticeAdapter.notifyDataSetChanged();
	}

	/**
	 * @function 跳转到特定页面
	 * */
	private void gotoDiffPage(Class<?> cls){
		Intent mIntent = new Intent(MainActivity.this, cls);
		startActivity(mIntent);
	}

	private void uploadFile() {

	}

	/**
	 * @function item点击事件
	 */
	OnNewsListener mOnNoticeListener = new OnNewsListener() {
		@Override
		public void onOpenNews(Object item, int position, Object arg) {
			switch (position) {
				case 0:
					gotoDiffPage(TabHostActivity.class);
					break;
				default:
					break;
			}
		}
	};

	/**
	 * @function 控件点击事件
	 */
	OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch ((Integer) v.getTag()) {
				case R.id.button_for_test_can_delect:
					uploadFile();
					break;
				default:
					break;
			}
		}
	};

	/**
	 * @function viewpaper的监听事件, 主要是监听小圆点的变化
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

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	OnGridItemClickListener mMenuBannerItemListener = new OnGridItemClickListener() {

		@Override
		public void onItemClick(View view, int position, Object arg) {
			showToastMessage(String.valueOf(position));
		}
	};
}
