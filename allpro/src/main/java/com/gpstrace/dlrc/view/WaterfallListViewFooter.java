package com.gpstrace.dlrc.view;

import com.gpstrace.dlrc.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WaterfallListViewFooter extends LinearLayout
{
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;

	private Context mContext;

	private View mContentView;
	private View mProgressBar;
	private TextView mHintView;

	public WaterfallListViewFooter(Context context)
	{
		super(context);
		initView(context);
	}

	public WaterfallListViewFooter(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public void setState(int state)
	{
		mHintView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
		mHintView.setVisibility(View.INVISIBLE);
		if (state == STATE_READY)
		{
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.waterfall_footer_hint_ready);
		} else if (state == STATE_LOADING)
		{
			mProgressBar.setVisibility(View.VISIBLE);
		} else
		{
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.waterfall_footer_hint_normal);
		}
	}

	public void setBottomMargin(int height)
	{
		if (height < 0)
			return;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}

	public int getBottomMargin()
	{
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		return lp.bottomMargin;
	}

	/**
	 * normal status
	 */
	public void normal()
	{
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}

	/**
	 * loading status
	 */
	public void loading()
	{
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * hide footer when disable pull load more
	 */
	public void hide()
	{
		if (mContentView.getVisibility() == View.VISIBLE)
		{
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
					.getLayoutParams();
			lp.height = 0;
			mContentView.setLayoutParams(lp);
		}
	}

	/**
	 * hide footer with height
	 */
	public void hideInvisible(int height)
	{
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.height = height;
		mContentView.setLayoutParams(lp);
		mContentView.setVisibility(View.INVISIBLE);
	}

	/**
	 * show footer
	 */
	public void show()
	{
		if (mContentView.getVisibility() != View.VISIBLE)
		{
			mContentView.setVisibility(View.VISIBLE);
		}
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}

	private void initView(Context context)
	{
		mContext = context;
		LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext)
				.inflate(R.layout.waterfall_footer_layout, null);
		addView(moreView);
		moreView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		mContentView = moreView.findViewById(R.id.waterfall_footer_content);
		mProgressBar = moreView.findViewById(R.id.waterfall_footer_progressbar);
		mHintView = (TextView) moreView
				.findViewById(R.id.waterfall_footer_hint_textview);
	}

}
