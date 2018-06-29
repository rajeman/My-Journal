package com.rajeman.myjournal;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;
import java.util.List;


public class AppViewModel extends AndroidViewModel {

    MutableLiveData<List<UserEntry>> userEntries;
    private MutableLiveData<Integer> uploadResult;

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
    public void fetchUserEntries(String userUid) {
       NetworkUtils.fetchUserEntries(this, userUid);
    }

    public void saveEntry(final String userUid, final UserEntry entry, Uri imageUri){

      NetworkUtils.saveEntry(this, userUid, entry, imageUri );
    }
}
