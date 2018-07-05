package com.example.q.jde3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;


import com.example.q.jde3.R;
import com.example.q.jde3.OneFragment;
import com.example.q.jde3.ThreeFragment;
import com.example.q.jde3.TwoFragment;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    static ThreeFragment thirdfrag;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 20;

    public static void showController()
    {
        thirdfrag.controller.show(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("Actionbar","clicked");

                stopService(thirdfrag.playIntent);

                if(thirdfrag.controller != null)
                    {
                        //if it is showing right now,
                        if (thirdfrag.controller.nowshowing)
                        {//we will hide it
                            thirdfrag.controller.reallyhide = true;
                            thirdfrag.controller.hide();
                        }
                        //if it is hiding right now
                        else
                        {//we will show it
                            thirdfrag.controller.show();
                        }
                        thirdfrag.controller.nowshowing = !(thirdfrag.controller.nowshowing);
                    }

            }

        });
        */

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setupViewPager(viewPager);

            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "Contacts");
        adapter.addFragment(new TwoFragment(), "Images");
        thirdfrag = new ThreeFragment();
        //thirdfrag.setRetainInstance(true);
        adapter.addFragment(thirdfrag, "Music");
        viewPager.setAdapter(adapter);

    }
    public void PressThis(View view){
        Log.i("Toolbar", "pressed");
    }

    public void songPicked(View view){
        if (thirdfrag.musicSrv == null){
            Log.i("songpicked","noService");
            return;
        }
        thirdfrag.musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        thirdfrag.musicSrv.playSong();
        if(thirdfrag.playbackPaused){
            Log.i("songPicked", "and setController is called");
            thirdfrag.setController();
            thirdfrag.playbackPaused=false;
        }
        else
            thirdfrag.controller.show(0);
    }

    @Override
    protected void onDestroy() {

        Log.i("MainActivity", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause(){


        Log.i("MainActivity", "onPause is called");
        if (thirdfrag.controller != null) {
            if (thirdfrag.controller.nowshowing) {//we will hide it
                thirdfrag.controller.reallyhide = true;
                thirdfrag.controller.hide();
                thirdfrag.controller.nowshowing = !(thirdfrag.controller.nowshowing);
            }
        }

        super.onPause();
    }
    public static void setStart(){
        thirdfrag.MusicStarted = true;
    }

    protected void togglePichu(View view){
        Log.i("togglePichu", "outside" + thirdfrag.MusicStarted );
        if(thirdfrag.controller != null && thirdfrag.MusicStarted)
        {
            Log.i("togglePichu", "inside");
            //if it is showing right now,
            if (thirdfrag.controller.nowshowing)
            {//we will hide it
                thirdfrag.controller.reallyhide = true;
                thirdfrag.controller.hide();
            }
            //if it is hiding right now
            else
            {//we will show it
                thirdfrag.controller.show();
            }
            thirdfrag.controller.nowshowing = !(thirdfrag.controller.nowshowing);

        }
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
        }
}


