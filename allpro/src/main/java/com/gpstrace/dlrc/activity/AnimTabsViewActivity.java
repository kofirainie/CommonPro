package com.gpstrace.dlrc.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.view.AnimTabsView;


public class AnimTabsViewActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animtabview);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		private AnimTabsView mTabsView;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.animtabview_fragment, container, false);
			setupViews(rootView);
			return rootView;
		}
		
		private void setupViews(View rootView) {
			mTabsView = (AnimTabsView) rootView.findViewById(R.id.publiclisten_tab);
			mTabsView.addItem("推荐");
			mTabsView.addItem("排行榜");
			mTabsView.addItem("歌单");
			mTabsView.addItem("DJ节目");

            mTabsView.setOnAnimTabsItemViewChangeListener(new AnimTabsView.IAnimTabsItemViewChangeListener() {
                @Override
                public void onChange(AnimTabsView tabsView, int oldPosition, int currentPosition) {
                }
            });
		}
	}

}
