package com.rajeman.myjournal;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rajeman.myjournal.databinding.JournalEntriesRecyclerViewBinding;

import java.util.ArrayList;
import java.util.List;

public class AppViewModel extends AndroidViewModel {
    Fragment fragment;
    AppViewModel appViewModel;

    MutableLiveData<List<UserEntry>> userEntries;
    private MutableLiveData<Integer> uploadResult;
    public AppViewModel(@NonNull Application application) {
        super(application);
        appViewModel = this;
    }


    public MutableLiveData<List<UserEntry>> getUserEntry() {

        if (userEntries == null) {
            userEntries = new MutableLiveData<>();
        }

        return userEntries;
    }

    public MutableLiveData<Integer> getUploadResult(){
        if(uploadResult == null){
            uploadResult = new SingleLiveEvent<>();
        }
        return uploadResult;
    }
    public void fetchUserEntries(String userUid) {
        FirebaseDatabase mFirebaseDatabase;
        DatabaseReference mDatabaseReference;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        DatabaseReference userReference = mDatabaseReference.child("users").child(userUid).child("entries");
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserEntry> entries = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserEntry userEntry = snapshot.getValue(UserEntry.class);
                        entries.add(userEntry);
                    }
                }
                appViewModel.getUserEntry().setValue(entries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                appViewModel.getUserEntry().setValue(null);
            }
        });
    }

    public void saveEntry(String userUid, UserEntry entry){
        FirebaseDatabase mFirebaseDatabase;
        DatabaseReference mDatabaseReference;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        DatabaseReference userReference = mDatabaseReference.child("users").child(userUid).child("entries");
        String entryId = userReference.push().getKey();
        entry.setEntryId(entryId);
        userReference.child(entryId).setValue(entry).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                appViewModel.getUploadResult().setValue(1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                appViewModel.getUploadResult().setValue(0);
            }
        });
    }
}
