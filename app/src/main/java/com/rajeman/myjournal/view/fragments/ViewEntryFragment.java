package com.rajeman.myjournal.view.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gturedi.views.StatefulLayout;
import com.rajeman.myjournal.view.dialogs.DeleteDialog;
import com.rajeman.myjournal.view.dialogs.UploadDialog;
import com.rajeman.myjournal.viewmodel.AppViewModel;
import com.rajeman.myjournal.utils.DateUtils;
import com.rajeman.myjournal.GlideApp;
import com.rajeman.myjournal.model.NetworkUtils;
import com.rajeman.myjournal.R;
import com.rajeman.myjournal.UserEntry;
import com.rajeman.myjournal.databinding.EntryViewBinding;

import java.util.List;

import static android.app.Activity.RESULT_OK;
/* this fragment displays an entry that was selected from the journal-entries-list fragment*/
public class ViewEntryFragment extends Fragment {

    private AppViewModel appViewModel;
    private AppViewModel entryViewModel;
    private StatefulLayout mStatefulLayout;
    private Fragment fragment;
    private TextView dayTextView, wkDayTextView, monthYearTextView, timeTextView;
    private ImageView entryImageView, locationEditIcon, titleEditIcon, storyEditIcon;
    private EditText titleEditText, storyEditText, locationEditText;
    private ImageView imageDeleteButton;
    private int RC_PHOTO_PICKER = 90;
    private String userUid;
    private Uri selectedImageUri;
    private String imageLink;
    private boolean isLocationEditable = false;
    private boolean isStoryEditable = false;
    private boolean isTitleEditable = false;
    private int adapterPosition;
    private EntryViewBinding mEntryViewBinding;
    private UserEntry entry;
    private String entryId;

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
        imageDeleteButton = mEntryViewBinding.deleteButton;
        mStatefulLayout = mEntryViewBinding.viewEntryStateful;

        configureEditTexts();
         //set click listener for image delete button
        imageDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (entryImageView.getDrawable() != null) {
                    imageDeleteButton.setVisibility(View.GONE);
                    GlideApp.with(fragment).clear(entryImageView);
                    imageLink = null;
                    selectedImageUri = null;
                }
            }
        });


        return mEntryViewBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(getString(R.string.view_entry));
        //get the adapter position of the entry saved into this fragment's argument
        adapterPosition = getArguments().getInt(getString(R.string.adapter_position_key));
        userUid = getArguments().getString(getString(R.string.user_uid_key));


        //get the view model instance of the parent activity;
        appViewModel = ViewModelProviders.of(this.getActivity()).get(AppViewModel.class);
        //set an observer for the data fetched by the journal-entries-list fragment
        final Observer<List<UserEntry>> userEntryDataObserver = new Observer<List<UserEntry>>() {
            @Override
            public void onChanged(@Nullable List<UserEntry> userEntries) {

                if (userEntries == null) {

                    mStatefulLayout.showError(getString(R.string.network_error), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            getActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(false).detach(fragment)
                                    .attach(fragment).commitAllowingStateLoss();
                        }
                    });
                } else if (userEntries.isEmpty()) {
                    mStatefulLayout.showEmpty();
                } else {
                    //since there is data, get the data corresponding to this entry using the adapter position
                    // that was passed to this fragment

                    UserEntry userEntry = userEntries.get(adapterPosition);
                    entry = userEntry;   //set the entry object to the global entry object so that it can be used by the deleteEntry method

                    //populate the views using this data
                    titleEditText.setText(userEntry.getTitle());
                    storyEditText.setText(userEntry.getStory());
                    locationEditText.setText(userEntry.getLocation());
                    DateUtils dateUtils = new DateUtils(userEntry.getTimestampLong());
                    wkDayTextView.setText(dateUtils.getWeekDay());
                    dayTextView.setText(dateUtils.getDay());
                    timeTextView.setText(dateUtils.getTime());
                    monthYearTextView.setText(dateUtils.getMonthYear());
                    entryId = userEntry.getEntryId();
                    imageLink = userEntry.getImageLink();
                    GlideApp.with(fragment).load(userEntry.getImageLink()).into(entryImageView);
                    if (imageLink != null) {
                        imageDeleteButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        imageDeleteButton.setVisibility(View.GONE);
                    }

                    mStatefulLayout.showContent();
                }

            }
        };

        appViewModel.getUserEntry().observe(this, userEntryDataObserver);

        //set an observer for the update result of the updateEntry method
        entryViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        final Observer<Integer> updateObserver = new Observer<Integer>() {


            @Override
            public void onChanged(@Nullable Integer updateResult) {
                UploadDialog uploadDialog = (UploadDialog) getActivity().getSupportFragmentManager().findFragmentByTag(getString(R.string.update_dialog_tag));

                if (updateResult != null && updateResult.equals(NetworkUtils.UPLOAD_SUCCESS)) {
                    //  Toast.makeText(fragment.getContext(), "upload successful", Toast.LENGTH_SHORT).show();
                    if (uploadDialog != null && uploadDialog.getDialog() != null && uploadDialog.getDialog().isShowing()) {
                        uploadDialog.dismiss();
                    }
                    //Toast.makeText(getContext(),  uploadPrefix + " "+ entry.getTitle() + " " + uploadSuffix, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    if (uploadDialog != null && uploadDialog.getDialog() != null && uploadDialog.getDialog().isShowing()) {
                        uploadDialog.dismiss();
                    }

                }

            }
        };

        entryViewModel.getUpdateResult().observe(this, updateObserver);

        //set an observer for delete result of the deleteEntryMethod

        final Observer<Integer> deleteObserver = new Observer<Integer>() {


            @Override
            public void onChanged(@Nullable Integer deleteResult) {
                DeleteDialog deleteDialog = (DeleteDialog) getActivity().getSupportFragmentManager().findFragmentByTag(getString(R.string.delete_dialog_tag));

                if (deleteResult != null && deleteResult.equals(NetworkUtils.UPLOAD_SUCCESS)) {
                    //  Toast.makeText(fragment.getContext(), "upload successful", Toast.LENGTH_SHORT).show();
                    if (deleteDialog != null && deleteDialog.getDialog() != null && deleteDialog.getDialog().isShowing()) {
                        deleteDialog.dismiss();
                    }
                    //Toast.makeText(getContext(),  uploadPrefix + " "+ entry.getTitle() + " " + uploadSuffix, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    if (deleteDialog != null && deleteDialog.getDialog() != null && deleteDialog.getDialog().isShowing()) {
                        deleteDialog.dismiss();
                    }

                }

            }
        };
        entryViewModel.getDeleteResult().observe(this, deleteObserver);


    }

    void hideKeyboard(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }


    void showKeyboard(EditText editText) {

        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    void setCursorToEnd(EditText et) {

        et.setSelection(et.getText().length());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_PHOTO_PICKER) {
            if (resultCode == RESULT_OK) {

                if (data.getData() != null) {
                    selectedImageUri = data.getData();
                }
                //glide does not load the image properly on first attempt. This is a quick fix;
                GlideApp.with(fragment).load(selectedImageUri).into(entryImageView);
                GlideApp.with(fragment).clear(entryImageView);
                GlideApp.with(fragment).load(selectedImageUri).into(entryImageView);

                imageDeleteButton.setVisibility(View.VISIBLE);
                //GlideApp.with(fragment).load(selectedImageUri).centerCrop().into(entryImageView);
            } else {
                Toast.makeText(getContext(), getString(R.string.cannot_load_image), Toast.LENGTH_SHORT).show();
            }


        } else {

            Toast.makeText(getContext(),getString(R.string.no_image_selected) , Toast.LENGTH_SHORT).show();

        }
    }


    private void startPhotoSelectionIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_intent)), RC_PHOTO_PICKER);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.view_entry_fragment_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_entry_image:

                startPhotoSelectionIntent();
                return true;
            case R.id.save_entry:
                updateEntry();
                return true;
            case R.id.delete_entry:
                deleteEntry();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void updateEntry() {
        String titleText = titleEditText.getText().toString();
        if (titleText.trim().isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.enter_title), Toast.LENGTH_SHORT).show();
            return;
        }
        String story = storyEditText.getText().toString();
        String location = locationEditText.getText().toString();
        UserEntry userEntry = new UserEntry(titleText, story, location, imageLink);
        if (entryId != null) {
            userEntry.setEntryId(entryId);
            showUploadDialog();

            entryViewModel.updateEntry(userUid, userEntry, selectedImageUri);
        }
    }

    private void showUploadDialog() {
        UploadDialog uploadDialog = new UploadDialog();
        uploadDialog.setCancelable(false);
        uploadDialog.show(getActivity().getSupportFragmentManager(), getString(R.string.update_dialog_tag));


    }

    private void showDeleteDialog() {
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.setCancelable(false);
        deleteDialog.show(getActivity().getSupportFragmentManager(), getString(R.string.delete_dialog_tag));

    }

    private void deleteEntry() {
        if (entry != null) {
            showDeleteDialog();
            entryViewModel.deleteEntry(userUid, entry);

        }
    }

    private void configureEditTexts() {
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
                if (isLocationEditable) {
                    locationEditText.setFocusable(false);
                    locationEditText.setBackgroundResource(android.R.color.transparent);
                    locationEditIcon.setImageResource(R.drawable.ic_edit);
                    isLocationEditable = false;
                    hideKeyboard(locationEditText);


                } else {
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
                if (isStoryEditable) {
                    storyEditText.setFocusable(false);
                    storyEditText.setBackgroundResource(android.R.color.transparent);
                    storyEditIcon.setImageResource(R.drawable.ic_edit);
                    isStoryEditable = false;
                    hideKeyboard(storyEditText);


                } else {
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
                if (isTitleEditable) {
                    titleEditText.setFocusable(false);
                    titleEditText.setCursorVisible(false);
                    titleEditText.setFocusableInTouchMode(false);
                    titleEditText.setBackgroundResource(android.R.color.transparent);
                    titleEditIcon.setImageResource(R.drawable.ic_edit);
                    isTitleEditable = false;
                    hideKeyboard(titleEditText);


                } else {
                    titleEditText.setFocusableInTouchMode(true);
                    titleEditText.setBackgroundResource(R.color.entry_edit_text_background);
                    titleEditIcon.setImageResource(R.drawable.ic_done);
                    isTitleEditable = true;
                    showKeyboard(titleEditText);
                    setCursorToEnd(titleEditText);
                }
            }
        });
    }

}
