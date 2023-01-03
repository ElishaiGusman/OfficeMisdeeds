package com.elishai.officemisdeeds;

import android.support.v4.app.Fragment;

public class MisdeedListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MisdeedListFragment();
    }
}
