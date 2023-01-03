package com.elishai.officemisdeeds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class MisdeedPagerActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String EXTRA_MISDEED_ID = "com.elishai.officemisdeeds.misdeed_id";

    private Button mFirstElementButton;
    private Button mLastElementButton;

    private ViewPager       mViewPager;
    private List<Misdeed>   mMisdeeds;

    public static Intent newIntent(Context context, UUID misdeedId) {
        Intent intent = new Intent(context, MisdeedPagerActivity.class);
        intent.putExtra(EXTRA_MISDEED_ID, misdeedId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misdeed_pager);

        UUID misdeedId = (UUID) getIntent().getSerializableExtra(EXTRA_MISDEED_ID);

        mViewPager          = (ViewPager) findViewById(R.id.misdeed_view_pager);
        mFirstElementButton = (Button) findViewById(R.id.button_first_element);
        mLastElementButton  = (Button) findViewById(R.id.button_last_element);
        
        mMisdeeds = MisdeedManager.get(this).getMisdeeds();
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {

            @Override
            public Fragment getItem(int position) {
                Misdeed misdeed = mMisdeeds.get(position);

                if(mViewPager.getCurrentItem() == 0)
                    mFirstElementButton.setEnabled(false);
                else
                    mFirstElementButton.setEnabled(true);

                if(mViewPager.getCurrentItem() == mMisdeeds.size() - 1)
                    mLastElementButton.setEnabled(false);
                else
                    mLastElementButton.setEnabled(true);

                return MisdeedFragment.newInstance(misdeed.getId());
            }

            @Override
            public int getCount() {
                return mMisdeeds.size();
            }
        });

        for(int i = 0; i < mMisdeeds.size(); i++) {
            if(mMisdeeds.get(i).getId().equals(misdeedId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        mFirstElementButton.setOnClickListener(this);
        mLastElementButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_first_element:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.button_last_element:
                mViewPager.setCurrentItem(mMisdeeds.size() - 1);
                break;
            default:
                break;
        }
    }
}