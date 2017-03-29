package com.gpstrace.dlrc.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.fragment.FragmentTabHostInner1;
import com.gpstrace.dlrc.fragment.FragmentTabHostInner2;

/**
 * 控制类
 * @author 黑卡米
 */
public class FragmentTabHostActivity1 extends FragmentActivity {
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_tabhost1);
        mTabHost = (FragmentTabHost)findViewById(R.id.fragment_tabhost1_tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.fragment_tabhost1_realtabcontent);
        
        /*1.addTab的第二个参数只能放继承Fragment的类
         *2.setIndicator的参数可以放入一个LayoutInflater出的Viwe
         * */
        mTabHost.addTab(mTabHost.newTabSpec("simple1").setIndicator("Simple2"),
                FragmentTabHostInner1.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("contacts1").setIndicator("Contacts2"),
                FragmentTabHostInner2.class, null);
    }
}