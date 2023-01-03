package com.elishai.officemisdeeds;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MisdeedListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mMisdeedRecyclerView;
    private MisdeedAdapter mAdapter;
    private boolean mSubtitleVisible;
    private int mCurrentPosition; // a variable for inner MisdeedHolder class and for resuming the fragment

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_misdeed_list, container,
                false);
        mMisdeedRecyclerView = (RecyclerView) view.findViewById(R.id.misdeed_recycler_view);
        mMisdeedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
       // updateUI(mCurrentPosition);
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_misdeed_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_misdeed:
                Misdeed misdeed = new Misdeed();
                MisdeedManager.get(getActivity()).addMisdeed(misdeed);
                Intent intent = MisdeedPagerActivity.newIntent(getActivity(), misdeed.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        MisdeedManager misdeedManager   = MisdeedManager.get(getActivity());
        int misdeedAmount               = misdeedManager.getMisdeeds().size();
        String subtitle                 = getString(R.string.subtitle_format, misdeedAmount);
        if(!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity      = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        MisdeedManager misdeedManager = MisdeedManager.get(getActivity());
        List<Misdeed> misdeeds = misdeedManager.getMisdeeds();
        if(mAdapter == null) {
            mAdapter = new MisdeedAdapter(misdeeds);
            mMisdeedRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setMisdeeds(misdeeds);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private void updateUI(int position) {
        mAdapter.notifyItemChanged(position);
    }

    private class MisdeedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView    mTextViewTitle;
        private TextView    mTextViewDate;
        private ImageView   mImageViewCaseSolved;

        private Misdeed mMisdeed;

        public MisdeedHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_misdeeds, parent, false));
            itemView.setOnClickListener(this);

            mTextViewTitle          = (TextView)    itemView.findViewById(R.id.misdeed_title);
            mTextViewDate           = (TextView)    itemView.findViewById(R.id.misdeed_date);
            mImageViewCaseSolved    = (ImageView)   itemView.findViewById(R.id.case_solved);
        }

        public void bind(Misdeed misdeed) {
            mMisdeed = misdeed;
            mTextViewTitle.setText(mMisdeed.getTitle());
            mTextViewDate.setText(DateFormat.format("EEE, MMM dd, yyyy.", mMisdeed.getDate()));
            mImageViewCaseSolved.setVisibility(mMisdeed.isSolved() ? ImageView.VISIBLE : ImageView.GONE);
        }

        @Override
        public void onClick(View view) {
            mCurrentPosition = getAdapterPosition();
            Intent intent = MisdeedPagerActivity.newIntent(getActivity(), mMisdeed.getId());
            startActivity(intent);
        }
    }

    private class MisdeedAdapter extends RecyclerView.Adapter<MisdeedHolder> {

        private List<Misdeed> mMisdeeds;

        public MisdeedAdapter(List<Misdeed> misdeeds) {
            mMisdeeds = misdeeds;
        }

        @Override
        public MisdeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new MisdeedHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(MisdeedHolder holder, int position) {
            Misdeed misdeed = mMisdeeds.get(position);
            holder.bind(misdeed);
        }

        @Override
        public int getItemCount() {
            return mMisdeeds.size();
        }

        public void setMisdeeds(List<Misdeed> misdeeds) {
            mMisdeeds = misdeeds;
        }
    }
}