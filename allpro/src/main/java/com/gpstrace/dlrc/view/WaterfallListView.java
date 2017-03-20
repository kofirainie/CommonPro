package com.gpstrace.dlrc.view;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.viewhelper.PLA_AbsListView;
import com.gpstrace.dlrc.viewhelper.PLA_AbsListView.OnScrollListener;
import com.gpstrace.dlrc.viewhelper.PLA_MultiColumnListView;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

public class WaterfallListView extends PLA_MultiColumnListView
		implements OnScrollListener
{

	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private IWaterfallListViewListener mListViewListener;

	// -- header view
	private WaterfallListViewHeader mHeaderView;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // header view's height
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // is refreashing.
	private boolean mPullRefreshScroll = true;// 是否允许刷新下拉空白区域显示
	private boolean isAutoLoadMore = true; //当前屏幕的数据量过少，无法填充满整个屏幕的时候，是否需要执行startLoadMore,默认为需要

	// -- footer view
	private WaterfallListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mPullLoadScroll = true;// 是否允许上拉加载空白区域显示
	private boolean mIsFooterReady = false;

	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
														// at bottom, trigger
														// load more.
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.

	private SharedPreferences preferences;// 用于存储上次更新时间

	/**
	 * 一分钟的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_MINUTE = 60 * 1000;

	/**
	 * 一小时的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_HOUR = 60 * ONE_MINUTE;

	/**
	 * 一天的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_DAY = 24 * ONE_HOUR;

	/**
	 * 一月的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_MONTH = 30 * ONE_DAY;

	/**
	 * 一年的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_YEAR = 12 * ONE_MONTH;

	/**
	 * 上次更新时间的字符串常量，用于作为SharedPreferences的键值
	 */
	private static final String UPDATED_AT = "updated_at";

	/**
	 * 为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分
	 */
	private int mId = -1;

	/**
	 * @param context
	 */
	public WaterfallListView(Context context)
	{
		super(context);
		initWithContext(context);
	}

	public WaterfallListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initWithContext(context);
	}

	public WaterfallListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context)
	{
		mScroller = new Scroller(context, new DecelerateInterpolator());
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// init header view
		mHeaderView = new WaterfallListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.waterfall_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.waterfall_header_time);
		addHeaderView(mHeaderView);

		// init footer view
		mFooterView = new WaterfallListViewFooter(context);

		// init header height
		mHeaderView.getViewTreeObserver()
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
				{
					@Override
					public void onGlobalLayout()
					{
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		refreshUpdatedAtValue();
	}

	@Override
	public void setAdapter(ListAdapter adapter)
	{
		// make sure XListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false)
		{
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable)
	{
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh)
		{ // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else
		{
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * @是否允许下拉刷新Scroll
	 * @param enable
	 */
	public void setPullRefreshScroll(boolean enable)
	{
		this.mPullRefreshScroll = enable;
	}

	/**
	 * enable or disable pull up load more feature.
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable)
	{
		mEnablePullLoad = enable;
		if (!mEnablePullLoad)
		{
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else
		{
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(WaterfallListViewFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					startLoadMore();
				}
			});
		}
	}
	
	/**
	 * @function 当当前屏幕的数据量过少，无法填充满整个屏幕的时候，是否需要执行startLoadMore
	 * @param enable true时，当数据没有填充满屏幕的时候自动执行startLoadMore
	 * @     		 false时，无须执行
	 * */
	public void setLastPositionForLoadMore(boolean enable){
		this.isAutoLoadMore = enable;
	}

	/**
	 * @是否允许上拉加载Scroll
	 * @param enable
	 */
	public void setPullLoadScroll(boolean enable)
	{
		this.mPullLoadScroll = enable;
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh()
	{
		if (mPullRefreshing == true)
		{
			mPullRefreshing = false;
			resetHeaderHeight();
			preferences.edit()
					.putLong(UPDATED_AT + mId, System.currentTimeMillis())
					.commit();
		}
	}

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore()
	{
		if (mPullLoading == true)
		{
			mPullLoading = false;

			mFooterView.setState(WaterfallListViewFooter.STATE_NORMAL);
			mFooterView.hide();// 增加拉载完后的自动影藏FooterView
		}
	}

	/**
	 * stop load more, reset footer view with height.
	 */
	public void stopLoadMore(int height)
	{
		if (mPullLoading == true)
		{
			mPullLoading = false;

			mFooterView.setState(WaterfallListViewFooter.STATE_NORMAL);
			mFooterView.hideInvisible(height);// 增加拉载完后的自动影藏FooterView
		}
	}

	/**
	 * set last refresh time
	 * 
	 * @param time
	 */
	public void setRefreshTime(String time)
	{
		mHeaderTimeView.setText(time);
	}

	private void invokeOnScrolling()
	{
		if (mScrollListener instanceof OnWaterfallScrollListener)
		{
			OnWaterfallScrollListener l = (OnWaterfallScrollListener) mScrollListener;
			l.onWaterfallScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta)
	{
		mHeaderView.setVisiableHeight(
				(int) delta + mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing)
		{ // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight)
			{
				mHeaderView.setState(WaterfallListViewHeader.STATE_READY);
			} else
			{
				mHeaderView.setState(WaterfallListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
		refreshUpdatedAtValue();
	}

	/**
	 * 刷新下拉头中上次更新时间的文字描述。
	 */
	private void refreshUpdatedAtValue()
	{
		long lastUpdateTime = preferences.getLong(UPDATED_AT + mId, -1);
		long currentTime = System.currentTimeMillis();
		long timePassed = currentTime - lastUpdateTime;
		long timeIntoFormat;
		String updateAtValue;
		if (lastUpdateTime == -1)
		{
			updateAtValue = getResources()
					.getString(R.string.waterfall_header_noupdated_str);
		} else if (timePassed < 0)
		{
			updateAtValue = getResources()
					.getString(R.string.waterfall_header_time_error_str);
		} else if (timePassed < ONE_MINUTE)
		{
			updateAtValue = getResources()
					.getString(R.string.waterfall_header_updated_justnow_str);
		} else if (timePassed < ONE_HOUR)
		{
			timeIntoFormat = timePassed / ONE_MINUTE;
			String value = timeIntoFormat + "分钟";
			updateAtValue = String.format(getResources()
					.getString(R.string.waterfall_header_updated_str), value);
		} else if (timePassed < ONE_DAY)
		{
			timeIntoFormat = timePassed / ONE_HOUR;
			String value = timeIntoFormat + "小时";
			updateAtValue = String.format(getResources()
					.getString(R.string.waterfall_header_updated_str), value);
		} else if (timePassed < ONE_MONTH)
		{
			timeIntoFormat = timePassed / ONE_DAY;
			String value = timeIntoFormat + "天";
			updateAtValue = String.format(getResources()
					.getString(R.string.waterfall_header_updated_str), value);
		} else if (timePassed < ONE_YEAR)
		{
			timeIntoFormat = timePassed / ONE_MONTH;
			String value = timeIntoFormat + "个月";
			updateAtValue = String.format(getResources()
					.getString(R.string.waterfall_header_updated_str), value);
		} else
		{
			timeIntoFormat = timePassed / ONE_YEAR;
			String value = timeIntoFormat + "年";
			updateAtValue = String.format(getResources()
					.getString(R.string.waterfall_header_updated_str), value);
		}
		setRefreshTime(updateAtValue);
	}

	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight()
	{
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderViewHeight)
		{
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight)
		{
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	private void updateFooterHeight(float delta)
	{
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading)
		{
			if (height > PULL_LOAD_MORE_DELTA)
			{ // height enough to invoke load
				// more.
				mFooterView.show();// 增加拉载完后的自动影藏FooterView
				mFooterView.setState(WaterfallListViewFooter.STATE_READY);
			} else
			{
				mFooterView.setState(WaterfallListViewFooter.STATE_NORMAL);
			}

			mFooterView.setBottomMargin(height);
		}

		// setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	private void resetFooterHeight()
	{
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0)
		{
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}

		if (!mPullLoading)
		{
			mFooterView.hide();
		}
	}

	public void startRefresh()
	{
		if (mEnablePullRefresh && !mPullRefreshing && !mPullLoading)
		{
			mPullRefreshing = true;
			mHeaderView.setState(WaterfallListViewHeader.STATE_REFRESHING);
			if (mListViewListener != null)
			{
				mListViewListener.onRefresh();
			}
		}
	}

	public void startLoadMore()
	{
		if (mEnablePullLoad && !mPullLoading && !mPullRefreshing)// 当为true时表示已经触发，不需要在触发
		{
			mPullLoading = true;
			mFooterView.setState(WaterfallListViewFooter.STATE_LOADING);
			if (mListViewListener != null)
			{
				mListViewListener.onLoadMore();
			}
		}

	}

	/**
	 * @获取当前加载状态
	 * @return
	 * @true表示正在加载中，false表示没有加载
	 */
	public boolean getLoadStatus()
	{
		if (mPullLoading || mPullRefreshing)
		{
			return true;
		} else
		{
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if (mLastY == -1)
		{
			mLastY = ev.getRawY();
		}

		switch (ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0))
			{
				// the first item is showing, header has shown or pull down.
				if (mPullRefreshScroll)
				{
					updateHeaderHeight(deltaY / OFFSET_RADIO);
					invokeOnScrolling();
				}
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0))
			{
				// last item, already pulled up or want to pull up.
				if (mPullLoadScroll)
				{
					updateFooterHeight(-deltaY / OFFSET_RADIO);
				}
			}
			break;
		default:
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0)
			{
				// invoke refresh
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight)
				{
					// if (!mPullRefreshing && !mPullLoading)
					// {
					// mPullRefreshing = true;
					// mHeaderView
					// .setState(WaterfallListViewHeader.STATE_REFRESHING);
					// if (mListViewListener != null)
					// {
					// mListViewListener.onRefresh();
					// }
					// }
					startRefresh();
				}
				resetHeaderHeight();
				if (getLastVisiblePosition() == mTotalItemCount - 1)
				{
					// invoke load more.
					if (mEnablePullLoad && mFooterView
							.getBottomMargin() > PULL_LOAD_MORE_DELTA)
					{
						startLoadMore();
					}
					resetFooterHeight();

				}
			} else if (getLastVisiblePosition() == mTotalItemCount - 1)
			{
				// invoke load more.
				if (mEnablePullLoad
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA)
				{
					startLoadMore();
				}
				resetFooterHeight();
			}

			// 增加拉载完后的自动影藏FooterView
			if (mEnablePullLoad
					&& mFooterView.getBottomMargin() <= PULL_LOAD_MORE_DELTA)
			{
				mFooterView.hide();
			}

			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll()
	{
		// Log.e("scrollLen111", "" + getCurrentScrollY());//该方法在重绘渲染时会被调用
		if (mScroller.computeScrollOffset())
		{
			if (mScrollBack == SCROLLBACK_HEADER)
			{
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else
			{
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	/**
	 * @获取瀑布流滚动的高度
	 * @当HeaderView部分被遮盖时，会从adapter项开始加载，所以需要单独对HeaderView部分进行处理
	 * @当有HeaderView且HeaderView不可见时，应该加上HeaderView的高度，当没有HeaderView时，该返回结果是正确的
	 * @return
	 */
	public int getCurrentScrollY()
	{
		View c = this.getChildAt(0);
		if (c == null)
		{
			return 0;
		}
		int firstVisiblePosition = this.getFirstVisiblePosition();
		int top = c.getTop();

		return -top + firstVisiblePosition * c.getHeight();
	}

	// @Override
	// public void setOnScrollListener(OnScrollListener l) {
	// mScrollListener = l;
	// }
	//
	// @Override
	// public void onScrollStateChanged(AbsListView view, int scrollState) {
	// if (mScrollListener != null) {
	// mScrollListener.onScrollStateChanged(view, scrollState);
	// }
	// }
	//
	// @Override
	// public void onScroll(AbsListView view, int firstVisibleItem, int
	// visibleItemCount, int totalItemCount) {

	// }

	public void setWaterfallListViewListener(IWaterfallListViewListener l,
			int id)
	{
		mListViewListener = l;
		mId = id;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnWaterfallScrollListener extends OnScrollListener
	{
		public void onWaterfallScrolling(View view);
	}

	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface IWaterfallListViewListener
	{
		public void onRefresh();

		public void onLoadMore();

		public void onScroll(int scrollY);// 上下滚动距离
	}

	@Override
	public void onScrollStateChanged(PLA_AbsListView view, int scrollState)
	{
		// Log.e("scrollLen333", "" + getCurrentScrollY());//该方法在滚动状态改变时会被调用
		if (mScrollListener != null)
		{
			mScrollListener.onScrollStateChanged(view, scrollState);
		}

		if (!mPullRefreshing && scrollState == SCROLL_STATE_IDLE)// 主要是防止先执行了TOUCH_UP及resetHeaderHeight，而还在Scroll
		{
			resetHeaderHeight();

			if (isAutoLoadMore) {
				if (view.getLastVisiblePosition() == view.getCount() - 1)
				{
					startLoadMore();
				}
			}
		}
	}

	@Override
	public void onScroll(PLA_AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)
	{
		// Log.e("scrollLen222", "" + getCurrentScrollY());//
		// 该方法在手势滚动过程中被调用，该方法执行调用过程最合理

		// send to user's listener
		mTotalItemCount = totalItemCount;

		if (mScrollListener != null)
		{
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}

		if (mListViewListener != null)
		{
			mListViewListener.onScroll(getCurrentScrollY());
		}
	}
}
