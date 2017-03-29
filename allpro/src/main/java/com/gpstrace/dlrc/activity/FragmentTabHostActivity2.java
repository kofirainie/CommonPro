package com.gpstrace.dlrc.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.gpstrace.dlrc.R;
import com.gpstrace.dlrc.adapter.PeekAdapter;
import com.gpstrace.dlrc.model.DemoObject;
import com.gpstrace.dlrc.provider.Constants;
import com.peekandpop.shalskar.peekandpop.PeekAndPop;

import java.util.ArrayList;
import java.util.List;

//https://github.com/shalskar/PeekAndPopDemo.git
public class FragmentTabHostActivity2 extends AppCompatActivity {

    private PeekAndPop peekAndPop;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_tabhost2);

        setupRecyclerView();
        setupPeekAndPopStandard();
    }

    private void setupRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
    }

    private void setupPeekAndPopStandard(){
        peekAndPop = new PeekAndPop.Builder(this)
                .blurBackground(false)
                .peekLayout(R.layout.peek_view)
                .parentViewGroupToDisallowTouchEvents(recyclerView)
                .build();

        recyclerView.setAdapter(new PeekAdapter(this, Constants.TYPE_STANDARD, buildDemoObjects(), peekAndPop));
    }

    private void setupPeekAndPopLonghold(){
        peekAndPop = new PeekAndPop.Builder(this)
                .peekLayout(R.layout.peek_view)
                .parentViewGroupToDisallowTouchEvents(recyclerView)
                .build();
        recyclerView.setAdapter(new PeekAdapter(this, Constants.TYPE_LONGHOLD, buildDemoObjects(), peekAndPop));
    }

    private void setupPeekAndPopFling(){
        peekAndPop = new PeekAndPop.Builder(this)
                .peekLayout(R.layout.peek_view)
                .parentViewGroupToDisallowTouchEvents(recyclerView)
                .build();
        recyclerView.setAdapter(new PeekAdapter(this, Constants.TYPE_FLING, buildDemoObjects(), peekAndPop));
    }

    private void setupPeekAndPopHoldAndRelease(){
        peekAndPop = new PeekAndPop.Builder(this)
                .peekLayout(R.layout.peek_view)
                .parentViewGroupToDisallowTouchEvents(recyclerView)
                .build();
        recyclerView.setAdapter(new PeekAdapter(this, Constants.TYPE_HOLD_AND_RELEASE, buildDemoObjects(), peekAndPop));
    }

    private void setupPeekAndPopExample(){
        peekAndPop = new PeekAndPop.Builder(this)
                .peekLayout(R.layout.peek_view)
                .parentViewGroupToDisallowTouchEvents(recyclerView)
                .animateFling(false)
                .build();
        recyclerView.setAdapter(new PeekAdapter(this, Constants.TYPE_EXAMPLE, buildDemoObjects(), peekAndPop));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        peekAndPop.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_standard):
                setupPeekAndPopStandard();
                break;
            case (R.id.action_longhold):
                setupPeekAndPopLonghold();
                break;
            case (R.id.action_fling):
                setupPeekAndPopFling();
                break;
            case (R.id.action_hold_and_release):
                setupPeekAndPopHoldAndRelease();
                break;
            case (R.id.action_example):
                setupPeekAndPopExample();
                break;
        }
        if (id == R.id.action_standard) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private List<DemoObject> buildDemoObjects1(){



        List<DemoObject> demoObjects = new ArrayList<>();
        ArrayList<Integer> imageUrls = new ArrayList<>();
        imageUrls.add(R.drawable.sport1);
        imageUrls.add(R.drawable.sport2);
        imageUrls.add(R.drawable.sport3);
        imageUrls.add(R.drawable.sport4);
        imageUrls.add(R.drawable.sport5);
        demoObjects.add(new DemoObject(imageUrls, "Sports", true));
        imageUrls = new ArrayList<>();
        imageUrls.add(R.drawable.nature1);
        imageUrls.add(R.drawable.nature2);
        imageUrls.add(R.drawable.nature3);
        imageUrls.add(R.drawable.nature4);
        imageUrls.add(R.drawable.nature5);
        demoObjects.add(new DemoObject(imageUrls, "Nature", true));
        imageUrls = new ArrayList<>();
        imageUrls.add(R.drawable.abstract1);
        imageUrls.add(R.drawable.abstract2);
        imageUrls.add(R.drawable.abstract3);
        imageUrls.add(R.drawable.abstract4);
        imageUrls.add(R.drawable.abstract5);
        demoObjects.add(new DemoObject(imageUrls, "Abstract", true));
        imageUrls = new ArrayList<>();
        imageUrls.add(R.drawable.business1);
        imageUrls.add(R.drawable.business2);
        imageUrls.add(R.drawable.business3);
        imageUrls.add(R.drawable.business4);
        imageUrls.add(R.drawable.business5);
        demoObjects.add(new DemoObject(imageUrls, "Business", true));
        imageUrls = new ArrayList<>();
        imageUrls.add(R.drawable.cats1);
        imageUrls.add(R.drawable.cats2);
        imageUrls.add(R.drawable.cats3);
        imageUrls.add(R.drawable.cats4);
        imageUrls.add(R.drawable.cats5);
        imageUrls.add(R.drawable.cats6);
        imageUrls.add(R.drawable.cats7);
        imageUrls.add(R.drawable.cats8);
        imageUrls.add(R.drawable.cats9);
        imageUrls.add(R.drawable.cats10);
        demoObjects.add(new DemoObject(imageUrls, "Cats", true));
        imageUrls = new ArrayList<>();
        imageUrls.add(R.drawable.food1);
        imageUrls.add(R.drawable.food2);
        imageUrls.add(R.drawable.food3);
        imageUrls.add(R.drawable.food4);
        imageUrls.add(R.drawable.food5);
        demoObjects.add(new DemoObject(imageUrls, "Food", true));
        imageUrls = new ArrayList<>();
        imageUrls.add(R.drawable.nightlife1);
        imageUrls.add(R.drawable.nightlife2);
        imageUrls.add(R.drawable.nightlife3);
        imageUrls.add(R.drawable.nightlife4);
        imageUrls.add(R.drawable.nightlife5);
        demoObjects.add(new DemoObject(imageUrls, "Nightlife", true));
        imageUrls = new ArrayList<>();
        imageUrls.add(R.drawable.technics1);
        imageUrls.add(R.drawable.technics2);
        imageUrls.add(R.drawable.technics3);
        imageUrls.add(R.drawable.technics4);
        imageUrls.add(R.drawable.technics5);
        demoObjects.add(new DemoObject(imageUrls, "Technics",true));

        return demoObjects;
    }

    private List<DemoObject> buildDemoObjects(){
        List<DemoObject> demoObjects = new ArrayList<>();
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add("http://lorempixel.com/450/450/sports/1");
        imageUrls.add("http://lorempixel.com/450/450/sports/2");
        imageUrls.add("http://lorempixel.com/450/450/sports/3");
        imageUrls.add("http://lorempixel.com/450/450/sports/4");
        imageUrls.add("http://lorempixel.com/450/450/sports/5");
        demoObjects.add(new DemoObject(imageUrls, "Sports"));
        imageUrls = new ArrayList<>();
        imageUrls.add("http://lorempixel.com/450/450/nature/1");
        imageUrls.add("http://lorempixel.com/450/450/nature/2");
        imageUrls.add("http://lorempixel.com/450/450/nature/3");
        imageUrls.add("http://lorempixel.com/450/450/nature/4");
        imageUrls.add("http://lorempixel.com/450/450/nature/5");
        demoObjects.add(new DemoObject(imageUrls, "Nature"));
        imageUrls = new ArrayList<>();
        imageUrls.add("http://lorempixel.com/450/450/abstract/1");
        imageUrls.add("http://lorempixel.com/450/450/abstract/2");
        imageUrls.add("http://lorempixel.com/450/450/abstract/3");
        imageUrls.add("http://lorempixel.com/450/450/abstract/4");
        imageUrls.add("http://lorempixel.com/450/450/abstract/5");
        demoObjects.add(new DemoObject(imageUrls, "Abstract"));
        imageUrls = new ArrayList<>();
        imageUrls.add("http://lorempixel.com/450/450/business/1");
        imageUrls.add("http://lorempixel.com/450/450/business/2");
        imageUrls.add("http://lorempixel.com/450/450/business/3");
        imageUrls.add("http://lorempixel.com/450/450/business/4");
        imageUrls.add("http://lorempixel.com/450/450/business/5");
        demoObjects.add(new DemoObject(imageUrls, "Business"));
        imageUrls = new ArrayList<>();
        imageUrls.add("http://lorempixel.com/450/450/cats/1");
        imageUrls.add("http://lorempixel.com/450/450/cats/2");
        imageUrls.add("http://lorempixel.com/450/450/cats/3");
        imageUrls.add("http://lorempixel.com/450/450/cats/4");
        imageUrls.add("http://lorempixel.com/450/450/cats/5");
        imageUrls.add("http://lorempixel.com/450/450/cats/6");
        imageUrls.add("http://lorempixel.com/450/450/cats/7");
        imageUrls.add("http://lorempixel.com/450/450/cats/8");
        imageUrls.add("http://lorempixel.com/450/450/cats/9");
        imageUrls.add("http://lorempixel.com/450/450/cats/10");
        demoObjects.add(new DemoObject(imageUrls, "Cats"));
        imageUrls = new ArrayList<>();
        imageUrls.add("http://lorempixel.com/450/450/food/1");
        imageUrls.add("http://lorempixel.com/450/450/food/2");
        imageUrls.add("http://lorempixel.com/450/450/food/3");
        imageUrls.add("http://lorempixel.com/450/450/food/4");
        imageUrls.add("http://lorempixel.com/450/450/food/5");
        demoObjects.add(new DemoObject(imageUrls, "Food"));
        imageUrls = new ArrayList<>();
        imageUrls.add("http://lorempixel.com/450/450/nightlife/1");
        imageUrls.add("http://lorempixel.com/450/450/nightlife/2");
        imageUrls.add("http://lorempixel.com/450/450/nightlife/3");
        imageUrls.add("http://lorempixel.com/450/450/nightlife/4");
        imageUrls.add("http://lorempixel.com/450/450/nightlife/5");
        demoObjects.add(new DemoObject(imageUrls, "Nightlife"));
        imageUrls = new ArrayList<>();
        imageUrls.add("http://lorempixel.com/450/450/technics/1");
        imageUrls.add("http://lorempixel.com/450/450/technics/2");
        imageUrls.add("http://lorempixel.com/450/450/technics/3");
        imageUrls.add("http://lorempixel.com/450/450/technics/4");
        imageUrls.add("http://lorempixel.com/450/450/technics/5");
        demoObjects.add(new DemoObject(imageUrls, "Technics"));

        return demoObjects;
    }
}
