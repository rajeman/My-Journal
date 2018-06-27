package com.rajeman.myjournal;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.gturedi.views.StatefulLayout;
import com.rajeman.myjournal.databinding.JournalEntriesRecyclerViewBinding;

import java.util.List;

public class AddEntryFragment extends Fragment {
    AppViewModel appViewModel;

    StatefulLayout statefulLayout;
    Fragment fragment;
    JournalEntriesRecyclerViewBinding jEntryBinding;
    String userUid;
    EntriesAdapter mAdapter;
    FloatingActionButton fab;
    public AddEntryFragment() {
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        userUid = getArguments().getString(getString(R.string.user_uid_key));

        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
       final Observer<Integer> uploadObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer uploadResult) {
                if(uploadResult == 1){

                    Toast.makeText(fragment.getContext(), "upload successful", Toast.LENGTH_SHORT).show();
                }

                else{
                    Toast.makeText(fragment.getContext(), "upload failed", Toast.LENGTH_SHORT).show();
                }

            }
        };

    UserEntry userEntry = new UserEntry("title", "summary", "location", "http://imagelink.com");
       // appViewModel.getUserEntry().observe(this, userEntryDataObserver);
        appViewModel.saveEntry(userUid, userEntry);

    }
}
