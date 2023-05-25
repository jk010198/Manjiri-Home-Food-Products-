package com.ithub.groceryshop;

import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.ithub.groceryshop.Adaptors.IntroViewPagerAdaptor;
import com.ithub.groceryshop.Models.screenItem;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdaptor introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;
    TextView tvSkip;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // make the activity on full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // when this activity is about to be launch we need to check if its openened before or not
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), SplashScreenActivity.class);
            startActivity(mainActivity);
            finish();
        }

        // hide the action bar
        getSupportActionBar().hide();

        // ini views
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        btnGetStarted.setVisibility(View.INVISIBLE);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);

        // fill list screen
        final List<screenItem> mList = new ArrayList<>();
        mList.add(new screenItem("Vision", "To be a Model an independent, innovative, honest and sustainable co-operative in which customers not only able to choose but can demand wide range of goods at reasonable prices.", R.drawable.vision));
        mList.add(new screenItem("Mission", "To take care of our Employees, Partners, Customers & Society, with a unique & excieting shopping experience, offering quality, variety, price and service.", R.drawable.mission));
        mList.add(new screenItem("Features", "-Fresh Quality Products\n" +
                "-Competitive Rates\n" +
                "-Quick Service\n" +
                "-Discount on Bulk Buying\n" +
                "-Excellent Product Reviews\n", R.drawable.values));

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdaptor(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tablayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // next button click Listner
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }

                if (position == mList.size() - 1) { // when we rech to the last screen
                    loaddLastScreen(position);
                }
            }
        });

        // tablayout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size() - 1) {
                    pos = tab.getPosition();
                    loaddLastScreen(pos);
                } else {
                    pos = tab.getPosition();
                    loaddLastScreen(pos);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Get Started button click listener
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open main activity
                Intent mainActivity = new Intent(getApplicationContext(), SplashScreenActivity.class);
                startActivity(mainActivity);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                savePrefsData();
                finish();
            }
        });

        // skip button click listener
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend", false);
        return isIntroActivityOpnendBefore;
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend", true);
        editor.commit();
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen(int position) {
        int p = position;
        //Toast.makeText(this, "postion " +p, Toast.LENGTH_SHORT).show();
        if (p == 0 || p == 1) {
            btnNext.setVisibility(View.VISIBLE);
            btnGetStarted.setVisibility(View.INVISIBLE);
            tvSkip.setVisibility(View.VISIBLE);
            tabIndicator.setVisibility(View.VISIBLE);
        } else if (p == 2) {
            btnNext.setVisibility(View.INVISIBLE);
            btnGetStarted.setVisibility(View.VISIBLE);
            tvSkip.setVisibility(View.INVISIBLE);
            tabIndicator.setVisibility(View.INVISIBLE);

            // setup animation
            btnGetStarted.setAnimation(btnAnim);
        }
    }
}