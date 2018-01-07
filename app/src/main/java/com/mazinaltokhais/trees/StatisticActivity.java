package com.mazinaltokhais.trees;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.eggheadgames.aboutbox.activity.AboutActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.util.ArrayList;
import java.util.HashMap;

import nl.dionsegijn.steppertouch.StepperTouch;

public class StatisticActivity extends AppCompatActivity {

    AppBarLayout appBarLayout;
    StepperTouch stepperTouch;
    private SwipeSelector swipeSelector;
    DatabaseReference mDatabase;
    ListView simpleList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        IntialView();
//        InitalStepperTouch();
        setSwipeSelector();
        RankingList();
    }
    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stat_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_search) {
            appBarLayout.setExpanded(true);
        }
        else if( item.getItemId() == R.id.option_about)
        {
            AboutActivity.launch(StatisticActivity.this);
        }
        else
        {
            finish();
        }
        return true;
    }
    private void IntialView()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference("Trees");
        swipeSelector = (SwipeSelector) findViewById(R.id.swipe_selector);
        appBarLayout = (AppBarLayout)findViewById(R.id.appbar);

        stepperTouch =(StepperTouch) findViewById(R.id.stepperTouch);
        //mTree =  Tree.createTree();
        simpleList = (ListView)findViewById(R.id.StatsticListview);




    }
//    private void  InitalStepperTouch()
//    {
//        stepperTouch.stepper.setMin(2017);
//        stepperTouch.stepper.setMax(2100);
//        stepperTouch.stepper.setValue(2017);
//
//        stepperTouch.stepper.addStepCallback(new OnStepCallback() {
//            @Override
//            public void onStep(int i, boolean b) {
////                if (mTree.getmCountryName() == null)
////                {
////                    updateLocationUI();
////                    getDeviceLocation();
////                }
////                if (globalVariable.getmLastKnownLocation() != null && mTree.getmCountryName() != null) {
//////                    getTreesFromFireBase();
//////                    RankingList();
////                    getFireBaseCountries( mTree);
////                }
//                RankingList();
//            }
//
//
//        });
//
//
//    }
    private void setSwipeSelector()
    {
        swipeSelector.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, getString(R.string.categryType), ""),
                new SwipeItem(1, getString(R.string.categryCampaign), "")

        );
//        swipeSelector.setItems(swipeItems);
//        if (swipeItems.length>0) {
//            swipeSelector.selectItemWithValue(0);
//            swipeSelector.selectItemWithValue(SwipeAreaIndex);
//
//            getTreesFromFireBase();
//        }
        RankingList();
        swipeSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(SwipeItem item) {
//                if (globalVariable.getmLastKnownLocation()  == null)//mTree == null)
//                {
//                    updateLocationUI();
//                    getDeviceLocation();
//                }
//                else {
//
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(globalVariable.getmLastKnownLocation().getLatitude(),
//                            globalVariable.getmLastKnownLocation().getLongitude()), DEFAULT_ZOOM));
//
//                }
                RankingList();
//                getTreesFromFireBase();
            }
        });
    }
    private void RankingList()
    {
        String selector;
        if (swipeSelector.getSelectedItem().title.toString().equals(getString(R.string.categryType))) {
            selector = "type";
        }
        else{
            selector = "mCampaignName";
        }

        final ArrayList<HashMap<String,String>> lista =
                new ArrayList<HashMap<String,String>>();

//        String mYear = String.valueOf(stepperTouch.stepper.getValue());
       // Query myTopPostsQuery = mDatabase.equalTo("Saudi Arabia","mCountryName");//.equalTo(whereQuiry);//equalTo("type").orderByValue();//.equalTo(mTree.getmCountryName());
        Query myTopPostsQuery = mDatabase.orderByChild(selector);//.equalTo("Saudi Arabia");

        final String finalSelector = selector;
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                int rank= 0 ;//(int) snapshot.getChildrenCount();
                int toatalRank=rank;
                int toatalTrees=0;
                String prevType ="";
                int count = 1;
                for (DataSnapshot child : snapshot.getChildren()) {
                    String area = String.valueOf(child.getKey());//child("type").getValue(String.class);
                    String treeType = String.valueOf(child.child(finalSelector).getValue(String.class));

                    if (!treeType.equals("") && treeType != null && !treeType.equals("null") ) {
                        if (!prevType.equals(treeType)) {
                            HashMap map = new HashMap();
                            map.put("rank", "");
                            map.put("model", treeType);
                            map.put("company", count);
                            lista.add(0, map);
                            // map.clear();
//                    toatalTrees+= Integer.valueOf(count);
                            rank++;
                            count = 1;
                        }
                        else
                        count++;
                        prevType = treeType;
                    }
                }

                SimpleAdapter adapter = new SimpleAdapter(
                        StatisticActivity.this,
                        lista,
                        R.layout.trees_count_row,
                        new String[] {"rank","model","company"},
                        new int[] {R.id.rank_txt,R.id.area_name_txt, R.id.area_count_txt}
                );

                simpleList.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
