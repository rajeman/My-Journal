package com.rajeman.myjournal;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkUtils {
    public static final Integer UPLOAD_FAILED = 0;
    public static final Integer UPLOAD_SUCCESS = 1;

    private static UploadTask mUploadTask;


    public static void fetchUserEntries(final AppViewModel appViewModel, String userUid) {

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

    public static void saveEntry(final AppViewModel appViewModel, final String userUid, final UserEntry entry, Uri imageUri) {
        final Context context = appViewModel.getApplication().getApplicationContext();
        final String uploadPrefix = context.getString(R.string.upload_prefix);
        final String uploadSuffix = context.getString(R.string.upload_suffix);
        final String uploadFailSuffix = context.getString(R.string.upload_fail_suffix);
        if (imageUri != null) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
           // Uri imgUrl;
            final StorageReference photoReference = storageReference.child("users")
                    .child(userUid).child("photo").child(UUID.randomUUID().toString());

          mUploadTask =  photoReference.putFile(imageUri);
                 mUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //start text upload
                    photoReference.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            appViewModel.getUploadResult().setValue(UPLOAD_FAILED);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //imgUrl = uri;

                            Uri imgUrl = uri;
                            String imgStringUrl = imgUrl.toString();
                            entry.setImageLink(imgStringUrl);

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

                                    Toast.makeText(context,  uploadPrefix + " \""   + entry.getTitle()  + "\" " + uploadSuffix, Toast.LENGTH_SHORT).show();
                                    appViewModel.getUploadResult().setValue(UPLOAD_SUCCESS);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    appViewModel.getUploadResult().setValue(UPLOAD_FAILED);
                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,  uploadPrefix + " \""   + entry.getTitle()  + "\" " + uploadFailSuffix, Toast.LENGTH_SHORT).show();
                    appViewModel.getUploadResult().setValue(UPLOAD_FAILED);
                }
            });
        } else {

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
                    Toast.makeText(context,  uploadPrefix + " \""   + entry.getTitle()  + "\" " + uploadSuffix, Toast.LENGTH_SHORT).show();
                    appViewModel.getUploadResult().setValue(UPLOAD_SUCCESS);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,  uploadPrefix + " \""   + entry.getTitle()  + "\" " + uploadFailSuffix, Toast.LENGTH_SHORT).show();
                    appViewModel.getUploadResult().setValue(UPLOAD_FAILED);
                }
            });
        }


    }

    public static void cancelUpload(AppViewModel appViewModel){
      if(mUploadTask != null && mUploadTask.isInProgress()){
          mUploadTask.cancel();
      }
    }
}
