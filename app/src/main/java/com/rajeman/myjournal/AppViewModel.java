package com.rajeman.myjournal;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;
import java.util.List;


public class AppViewModel extends AndroidViewModel {

    private MutableLiveData<List<UserEntry>> userEntries;
    private MutableLiveData<Integer> uploadResult;
    private MutableLiveData<Integer> updateResult;
    private MutableLiveData<Integer> deleteResult;

    public AppViewModel(@NonNull Application application) {
        super(application);

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

    public MutableLiveData<Integer> getUpdateResult(){
        if(updateResult == null){
            updateResult = new SingleLiveEvent<>();
        }
        return updateResult;
    }

    public MutableLiveData<Integer> getDeleteResult(){
        if(deleteResult == null){
            deleteResult = new SingleLiveEvent<>();
        }
        return deleteResult;
    }


    public void fetchUserEntries(String userUid) {
        NetworkUtils networkUtils = new NetworkUtils();
        networkUtils.fetchUserEntries(this, userUid);
    }

    public void saveEntry(final String userUid, final UserEntry entry, Uri imageUri){

        NetworkUtils networkUtils = new NetworkUtils();
      networkUtils.saveEntry(this, userUid, entry, imageUri );
    }
    public void updateEntry(String userId, UserEntry userEntry, Uri imageUri){

        NetworkUtils networkUtils = new NetworkUtils();

        networkUtils.updateEntry(this,  userId, userEntry, imageUri);
    }

    public void deleteEntry( final String userUid, final UserEntry entry){

        NetworkUtils networkUtils = new NetworkUtils();

        networkUtils.deleteEntry(this,  userUid, entry);
    }
}
