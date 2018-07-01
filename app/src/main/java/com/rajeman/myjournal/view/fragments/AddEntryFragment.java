package com.rajeman.myjournal.view.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.rajeman.myjournal.view.dialogs.UploadDialog;
import com.rajeman.myjournal.viewmodel.AppViewModel;
import com.rajeman.myjournal.utils.DateUtils;
import com.rajeman.myjournal.GlideApp;
import com.rajeman.myjournal.model.NetworkUtils;
import com.rajeman.myjournal.R;
import com.rajeman.myjournal.UserEntry;
import com.rajeman.myjournal.databinding.JournalEntryLayoutBinding;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class AddEntryFragment extends Fragment {
    private AppViewModel appViewModel;
    private Fragment fragment;
    private TextView dayTextView, wkDayTextView, monthYearTextView, timeTextView;
    private ImageView entryImageView;
    private EditText titleEditText, textEditText, locationEditText;
    private int RC_PHOTO_PICKER = 90;
    private String userUid;
    private Uri selectedImageUri;
    private UploadDialog mUploadDialog;
    private JournalEntryLayoutBinding jEntryLayoutBinding;

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

        DateUtils dateUtils = new DateUtils(System.currentTimeMillis());
        wkDayTextView.setText(dateUtils.getWeekDay());
        dayTextView.setText(dateUtils.getDay());
        timeTextView.setText(dateUtils.getTime());
        monthYearTextView.setText(dateUtils.getMonthYear());

        if (savedInstanceState != null) {

            titleEditText.setText(savedInstanceState.getString(getString(R.string.title_txt)));
            textEditText.setText(savedInstanceState.getString(getString(R.string.text_txt)));
            locationEditText.setText(savedInstanceState.getString(getString(R.string.location_txt)));


        }
        return jEntryLayoutBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //set action bar title
        getActivity().setTitle(getString(R.string.add_an_entry));
        userUid = getArguments().getString(getString(R.string.user_uid_key));
        if (savedInstanceState != null) {
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
                UploadDialog uploadDialog = (UploadDialog) getActivity().getSupportFragmentManager().findFragmentByTag(getString(R.string.upload_dialog_tag));

                if (uploadResult != null && uploadResult.equals(NetworkUtils.UPLOAD_SUCCESS)) {
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


        appViewModel.getUploadResult().observe(this, uploadObserver);


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
        switch (item.getItemId()) {
            case R.id.add_entry_image:

                startPhotoSelectionIntent();
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

        outState.putString(getString(R.string.title_txt), titleEditText.getText().toString());
        outState.putString(getString(R.string.text_txt), textEditText.getText().toString());
        outState.putString(getString(R.string.location_txt), locationEditText.getText().toString());
        if (selectedImageUri != null) {

            outState.putString(getString(R.string.entry_image), selectedImageUri.toString());
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_PHOTO_PICKER) {
            //IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // open user diary entry
                selectedImageUri = data.getData();
                //quick fix to make glide load image properly
                GlideApp.with(fragment).load(selectedImageUri).into(entryImageView);
                GlideApp.with(fragment).clear(entryImageView);
                GlideApp.with(fragment).load(selectedImageUri).into(entryImageView);

            } else {
                Toast.makeText(getContext(), getString(R.string.cannot_load_image), Toast.LENGTH_SHORT).show();
            }


        } else {

            Toast.makeText(getContext(), getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show();

        }
    }


    private void startPhotoSelectionIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_intent)), RC_PHOTO_PICKER);


    }

    private void saveEntry() {
        String titleText = titleEditText.getText().toString();
        if (titleText.trim().isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.enter_title), Toast.LENGTH_SHORT).show();
            return;
        }
        String story = textEditText.getText().toString();
        String location = locationEditText.getText().toString();
        UserEntry userEntry = new UserEntry(titleText, story, location, null);
        showUploadDialog();
        appViewModel.saveEntry(userUid, userEntry, selectedImageUri);
    }

    private void showUploadDialog() {
        mUploadDialog = new UploadDialog();
        mUploadDialog.setCancelable(false);
        mUploadDialog.setArguments(new Bundle());
        mUploadDialog.show(getActivity().getSupportFragmentManager(), getString(R.string.upload_dialog_tag));


    }

}
