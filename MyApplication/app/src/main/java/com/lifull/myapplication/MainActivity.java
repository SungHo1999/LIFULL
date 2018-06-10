package com.lifull.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoScrollViewPager viewPager = (AutoScrollViewPager) findViewById(R.id.view_pager);
        ImageAdapter imgadapter = new ImageAdapter(this);

//    PagerAdapter adapter = new InfinitePagerAdapter(imgadapter);

        viewPager.setAdapter(imgadapter);
        viewPager.startAutoScroll();

    }
}






