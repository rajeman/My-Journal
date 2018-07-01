package com.rajeman.myjournal.view.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gturedi.views.StatefulLayout;
import com.rajeman.myjournal.viewmodel.AppViewModel;
import com.rajeman.myjournal.view.EntriesAdapter;
import com.rajeman.myjournal.R;
import com.rajeman.myjournal.UserEntry;
import com.rajeman.myjournal.databinding.JournalEntriesRecyclerViewBinding;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class JournalEntriesFragment extends Fragment {

    private AppViewModel appViewModel;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private StatefulLayout statefulLayout;
    private final Fragment fragment;
    private JournalEntriesRecyclerViewBinding jEntryBinding;
    private String userUid;
    private EntriesAdapter mAdapter;
    private FloatingActionButton fab;

    public interface FabClickListener{

        void onFabClicked();
    }

    public JournalEntriesFragment() {
        fragment = this;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        jEntryBinding = DataBindingUtil.inflate(inflater, R.layout.journal_entries_recycler_view, container, false);
        statefulLayout = jEntryBinding.journalEntriesStateful;
        mRecyclerView = jEntryBinding.entriesRecyclerView;
        fab = jEntryBinding.fab;

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        statefulLayout.showLoading(getString(R.string.fetching_data));
        mAdapter =  new EntriesAdapter(this, new ArrayList<UserEntry>());
        mRecyclerView.setAdapter(mAdapter);

        return jEntryBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FabClickListener fabClickListener = (FabClickListener) getActivity();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //notify activity of click
                assert fabClickListener != null;
                fabClickListener.onFabClicked();
            }
        });
         //set actionbar title
        getActivity().setTitle(getString(R.string.entries_fragment_title));
        userUid = getArguments().getString(getString(R.string.user_uid_key));

        appViewModel = ViewModelProviders.of(this.getActivity()).get(AppViewModel.class);
        final Observer<List<UserEntry>> userEntryDataObserver = new Observer<List<UserEntry>>() {
            @Override
            public void onChanged(@Nullable List<UserEntry> userEntries) {
                if(userEntries == null){

                    statefulLayout.showError(getString(R.string.network_error),new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //statefulLayout.showLoading(getString(R.string.connecting));
                            getActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(false).detach(fragment)
                                    .attach(fragment).commitAllowingStateLoss();
                        }
                    });
                }
                else if (userEntries.isEmpty()){
                    statefulLayout.showEmpty(getString(R.string.no_entries));
                }
                else{
                    mAdapter.setNewItems(userEntries);
                    mAdapter.notifyDataSetChanged();
                    statefulLayout.showContent();
                  //  Toast.makeText(fragment.getContext(), "got something", Toast.LENGTH_SHORT).show();
                }

            }
        };

        appViewModel.getUserEntry().observe(this, userEntryDataObserver);
        appViewModel.fetchUserEntries(userUid);

    }
}
