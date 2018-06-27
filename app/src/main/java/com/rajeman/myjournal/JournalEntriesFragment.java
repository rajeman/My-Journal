package com.rajeman.myjournal;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gturedi.views.StatefulLayout;
import com.rajeman.myjournal.databinding.JournalEntriesRecyclerViewBinding;

import java.util.List;

public class JournalEntriesFragment extends Fragment {

    AppViewModel appViewModel;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    StatefulLayout statefulLayout;
    Fragment fragment;
    JournalEntriesRecyclerViewBinding jEntryBinding;
    String userUid;


    public JournalEntriesFragment() {
        fragment = this;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        jEntryBinding = DataBindingUtil.inflate(inflater, R.layout.journal_entries_recycler_view, container, false);
        statefulLayout = jEntryBinding.journalEntriesStateful;
        mRecyclerView = jEntryBinding.entriesRecyclerView;
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        statefulLayout.showLoading(getString(R.string.fetching_data));
     /*   mAdapter =  new ThreadPostsAdapter(this, new ArrayList<ThreadData>());
        statefulLayout.showLoading(getString(R.string.connecting));
        mRecyclerView.setAdapter(mAdapter);
        */
        return jEntryBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userUid = getArguments().getString(getString(R.string.user_uid_key));

        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
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
                    statefulLayout.showEmpty();
                }
                else{
                    Toast.makeText(fragment.getContext(), "got something", Toast.LENGTH_SHORT).show();
                }

            }
        };

        appViewModel.getUserEntry().observe(this, userEntryDataObserver);
        appViewModel.fetchUserEntries(userUid);

    }
}
