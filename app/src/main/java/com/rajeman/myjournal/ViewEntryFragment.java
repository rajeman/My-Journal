package com.rajeman.myjournal;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gturedi.views.StatefulLayout;
import com.rajeman.myjournal.databinding.EntryViewBinding;

import java.util.List;

public class ViewEntryFragment  extends Fragment {

    AppViewModel appViewModel;
    StatefulLayout mStatefulLayout;
    Fragment fragment;
    TextView dayTextView, wkDayTextView, monthYearTextView, timeTextView;
    ImageView entryImageView, locationEditIcon, titleEditIcon, storyEditIcon;
    EditText titleEditText, storyEditText, locationEditText;
    int RC_PHOTO_PICKER = 90;
    String userUid;
    Uri selectedImageUri;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference photoReference;
    UploadDialog mUploadDialog;
    private boolean isLocationEditable = false;
    private boolean isStoryEditable = false;
    private boolean isTitleEditable = false;
    private int adapterPosition;
    JournalEntriesFragment mJentryFragment;
    EntryViewBinding mEntryViewBinding;
    UserEntry entry;

    public ViewEntryFragment() {
        fragment = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mEntryViewBinding = DataBindingUtil.inflate(inflater, R.layout.entry_view, container, false);
        dayTextView = mEntryViewBinding.entryDayTextView;
        wkDayTextView = mEntryViewBinding.entryWkDayTextView;
        monthYearTextView = mEntryViewBinding.entryMonthYearTextView;
        timeTextView = mEntryViewBinding.entryTimeTextView;
        entryImageView = mEntryViewBinding.entryImage;
        titleEditText = mEntryViewBinding.entryTitleEditText;
        storyEditText = mEntryViewBinding.entryTextEditText;
        locationEditText = mEntryViewBinding.entryLocationEditText;
        locationEditIcon = mEntryViewBinding.icLocationEdit;
        titleEditIcon = mEntryViewBinding.icTitleEdit;
        storyEditIcon = mEntryViewBinding.icStoryEdit;
        mStatefulLayout = mEntryViewBinding.viewEntryStateful;
       // mStatefulLayout.showLoading(getString(R.string.loading));
        locationEditText.setFocusable(false);
        locationEditText.setBackgroundResource(android.R.color.transparent);
        locationEditIcon.setImageResource(R.drawable.ic_edit);
        isLocationEditable = false;

        storyEditText.setFocusable(false);
        storyEditText.setBackgroundResource(android.R.color.transparent);
        storyEditIcon.setImageResource(R.drawable.ic_edit);
        isStoryEditable = false;

        storyEditText.setFocusable(false);
        storyEditText.setBackgroundResource(android.R.color.transparent);
        storyEditIcon.setImageResource(R.drawable.ic_edit);
        isStoryEditable = false;

        locationEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLocationEditable){
                    locationEditText.setFocusable(false);
                    locationEditText.setBackgroundResource(android.R.color.transparent);
                    locationEditIcon.setImageResource(R.drawable.ic_edit);
                    isLocationEditable = false;
                    hideKeyboard(locationEditText);


                }
                else{
                    locationEditText.setFocusableInTouchMode(true);
                    locationEditText.setBackgroundResource(R.color.entry_edit_text_background);
                    locationEditIcon.setImageResource(R.drawable.ic_done);
                    isLocationEditable = true;
                    showKeyboard(locationEditText);
                    setCursorToEnd(locationEditText);
                }
            }
        });

        storyEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoryEditable){
                    storyEditText.setFocusable(false);
                    storyEditText.setBackgroundResource(android.R.color.transparent);
                    storyEditIcon.setImageResource(R.drawable.ic_edit);
                    isStoryEditable = false;
                    hideKeyboard(storyEditText);


                }
                else{
                    storyEditText.setFocusableInTouchMode(true);
                    storyEditText.setBackgroundResource(R.color.entry_edit_text_background);
                    storyEditIcon.setImageResource(R.drawable.ic_done);
                    isStoryEditable = true;
                    storyEditText.performClick();
                    showKeyboard(storyEditText);
                    setCursorToEnd(storyEditText);
                }
            }
        });

        titleEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTitleEditable){
                    titleEditText.setFocusable(false);
                    titleEditText.setCursorVisible(false);
                    titleEditText.setFocusableInTouchMode(false);
                    titleEditText.setBackgroundResource(android.R.color.transparent);
                    titleEditIcon.setImageResource(R.drawable.ic_edit);
                    isTitleEditable = false;
                    hideKeyboard(titleEditText);



                }
                else{
                    titleEditText.setFocusableInTouchMode(true);
                    titleEditText.setBackgroundResource(R.color.entry_edit_text_background);
                    titleEditIcon.setImageResource(R.drawable.ic_done);
                    isTitleEditable = true;
                    showKeyboard(titleEditText);
                    setCursorToEnd(titleEditText);
                }
            }
        });


        DateUtils dateUtils = new DateUtils(System.currentTimeMillis());
        wkDayTextView.setText(dateUtils.getWeekDay());
        dayTextView.setText(dateUtils.getDay());
        timeTextView.setText(dateUtils.getTime());
        monthYearTextView.setText(dateUtils.getMonthYear());

        if(savedInstanceState != null){
            //    dayTextView.setText(savedInstanceState.getString(getString(R.string.day)));
            //  wkDayTextView.setText(savedInstanceState.getString(getString(R.string.wk_day)));
            //  monthYearTextView.setText(savedInstanceState.getString(getString(R.string.month_year)));
            //  timeTextView.setText(savedInstanceState.getString(getString(R.string.time_txt)));
            titleEditText.setText(savedInstanceState.getString(getString(R.string.title_txt)));
            storyEditText.setText(savedInstanceState.getString(getString(R.string.text_txt)));
            locationEditText.setText(savedInstanceState.getString(getString(R.string.location_txt)));




        }
        return mEntryViewBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapterPosition= getArguments().getInt(getString(R.string.adapter_position_key));
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

       // JournalEntriesFragment entriesFragment  = (JournalEntriesFragment) getActivity().getSupportFragmentManager().findFragmentByTag(getString(R.string.entries_fragment_tag));
        appViewModel = ViewModelProviders.of(this.getActivity()).get(AppViewModel.class);
        final Observer<List<UserEntry>> userEntryDataObserver = new Observer<List<UserEntry>>() {
            @Override
            public void onChanged(@Nullable List<UserEntry> userEntries) {
                Toast.makeText(fragment.getContext(), "got data", Toast.LENGTH_SHORT).show();
                if(userEntries == null){

                    mStatefulLayout.showError(getString(R.string.network_error),new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //statefulLayout.showLoading(getString(R.string.connecting));
                            getActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(false).detach(fragment)
                                    .attach(fragment).commitAllowingStateLoss();
                        }
                    });
                }
                else if (userEntries.isEmpty()){
                    mStatefulLayout.showEmpty();
                }
                else{
                    UserEntry userEntry = userEntries.get(adapterPosition);
                    titleEditText.setText(userEntry.getTitle());
                    storyEditText.setText(userEntry.getStory());
                    locationEditText.setText(userEntry.getLocation());

                    mStatefulLayout.showContent();
                }

            }
        };

        appViewModel.getUserEntry().observe(this, userEntryDataObserver);
        //appViewModel.fetchUserEntries(userUid);
        // UserEntry userEntry = new UserEntry("title", "story", "location", "http://imagelink.com");
        // appViewModel.getUserEntry().observe(this, userEntryDataObserver);
        //  appViewModel.saveEntry(userUid, userEntry);

    }

    void hideKeyboard(EditText editText){
       // editText.setInputType(InputType.TYPE_NULL);
             editText.requestFocus();
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
    }


    void showKeyboard(EditText editText){

        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

   void setCursorToEnd(EditText et){

       et.setSelection(et.getText().length());
   }


}
