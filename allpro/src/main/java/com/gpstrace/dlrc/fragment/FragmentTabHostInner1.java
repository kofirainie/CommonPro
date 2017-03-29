package com.gpstrace.dlrc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 碎片类必须要有一个空的构造函数，因为框架在需要的时候常常会重新实例化它，如果没有将会抛出异常
 * @author 黑卡米
 *
 */
public class FragmentTabHostInner1 extends Fragment {

	private TextView textView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		textView=new TextView(getActivity());
		textView.setText("FragmentTabHostInner1");
		return textView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		textView = null;
	}
}
