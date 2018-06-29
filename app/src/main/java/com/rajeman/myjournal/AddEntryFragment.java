package com.rajeman.myjournal;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gturedi.views.StatefulLayout;
import com.rajeman.myjournal.databinding.JournalEntriesRecyclerViewBinding;
import com.rajeman.myjournal.databinding.JournalEntryLayoutBinding;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.rajeman.myjournal.MainActivity.RC_SIGN_IN;

public class AddEntryFragment extends Fragment {
    AppViewModel appViewModel;
    //StatefulLayout statefulLayout;
    Fragment fragment;
    TextView dayTextView, wkDayTextView, monthYearTextView, timeTextView;
    ImageView entryImageView;
    EditText titleEditText, textEditText, locationEditText;
    int RC_PHOTO_PICKER = 90;
    String userUid;
    Uri selectedImageUri;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference photoReference;
   JournalEntryLayoutBinding jEntryLayoutBinding;
    public AddEntryFragment() {
        fragment = this;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        jEntryLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.journal_entry_layout, container, false);
        dayTextView = jEntryLayoutBinding.entryDayTextView;
        wkDayTextView = jEntryLayoutBinding.entryWkDayTextView;
        monthYearTextView = jEntryLayoutBinding.entryMonthYearTextView;
        timeTextView = jEntryLayoutBinding.entryTimeTextView;
        entryImageView = jEntryLayoutBinding.entryImage;
        titleEditText = jEntryLayoutBinding.entryTitleEditText;
        textEditText = jEntryLayoutBinding.entryTextEditText;
        locationEditText = jEntryLayoutBinding.entryLocationEditText;

        if(savedInstanceState != null){
              dayTextView.setText(savedInstanceState.getString(getString(R.string.day)));
            wkDayTextView.setText(savedInstanceState.getString(getString(R.string.wk_day)));
            monthYearTextView.setText(savedInstanceState.getString(getString(R.string.month_year)));
            timeTextView.setText(savedInstanceState.getString(getString(R.string.time_txt)));
            titleEditText.setText(savedInstanceState.getString(getString(R.string.title_txt)));
            textEditText.setText(savedInstanceState.getString(getString(R.string.text_txt)));
            locationEditText.setText(savedInstanceState.getString(getString(R.string.location_txt)));




        }
        return jEntryLayoutBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userUid = getArguments().getString(getString(R.string.user_uid_key));
        if(savedInstanceState != null) {
            String retrievedImgUri = savedInstanceState.getString(getString(R.string.entry_image));
            if (retrievedImgUri != null) {
                selectedImageUri = Uri.parse(retrievedImgUri);
                GlideApp.with(fragment).asBitmap().load(selectedImageUri).centerCrop().into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, 300) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        entryImageView.setImageBitmap(resource);
                    }
                });
            }
        }


        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
       final Observer<Integer> uploadObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer uploadResult) {
                if(uploadResult.equals  ((Integer)1)){

                    Toast.makeText(fragment.getContext(), "upload successful", Toast.LENGTH_SHORT).show();
                }

                else{
                    Toast.makeText(fragment.getContext(), "upload failed", Toast.LENGTH_SHORT).show();
                }

            }
        };

        appViewModel.getUploadResult().observe(this, uploadObserver);
   // UserEntry userEntry = new UserEntry("title", "story", "location", "http://imagelink.com");
       // appViewModel.getUserEntry().observe(this, userEntryDataObserver);
      //  appViewModel.saveEntry(userUid, userEntry);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.add_entry_fragment_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_entry_image:

                startPhotoSelectionIntent();
                Toast.makeText(getContext(), "menu clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.save_entry:
                saveEntry();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.day), dayTextView.getText().toString());
        outState.putString(getString(R.string.wk_day), wkDayTextView.getText().toString());
        outState.putString(getString(R.string.month_year), monthYearTextView.getText().toString());
        outState.putString(getString(R.string.time_txt), timeTextView.getText().toString());
        outState.putString(getString(R.string.title_txt), titleEditText.getText().toString());
        outState.putString(getString(R.string.text_txt), textEditText.getText().toString());
        outState.putString(getString(R.string.location_txt), locationEditText.getText().toString());
        if(selectedImageUri!= null){

            outState.putString(getString(R.string.entry_image), selectedImageUri.toString());
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RC_PHOTO_PICKER){
            //IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                // open user diary entry
                    selectedImageUri = data.getData();

                    GlideApp.with(fragment).load(selectedImageUri).centerCrop().into(entryImageView);
                    //GlideApp.with(fragment).load(selectedImageUri).centerCrop().into(entryImageView);
                    }
                   else{
                        Toast.makeText(getContext(), "cannot load image", Toast.LENGTH_SHORT).show();
                    }


            }

            else{

                Toast.makeText(getContext(), "no image selected", Toast.LENGTH_SHORT).show();

            }
        }


    private void startPhotoSelectionIntent(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_intent)), RC_PHOTO_PICKER);



    }

    private void saveEntry(){
        String titleText = titleEditText.getText().toString();
        if(titleText.trim().isEmpty()){
            Toast.makeText(getContext(), getString(R.string.enter_title), Toast.LENGTH_SHORT).show();
            return;
        }
        String story = textEditText.getText().toString();
        String location = locationEditText.getText().toString();
        UserEntry userEntry = new UserEntry(titleText, story, location, null );

            appViewModel.saveEntry(userUid, userEntry, selectedImageUri);
    }


}
