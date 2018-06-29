package com.rajeman.myjournal;



import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.rajeman.myjournal.databinding.UploadDialogBinding;


public class UploadDialog extends DialogFragment {
    UploadDialogBinding uploadDialogBinding;
    String message;
    String title;
    AlertDialog dialog;
    private static Fragment mfragment;

    public UploadDialog() {
        mfragment = this;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        uploadDialogBinding = DataBindingUtil.inflate(inflater, R.layout.upload_dialog, null, false);

        View view = uploadDialogBinding.getRoot();


            uploadDialogBinding.uploadDialogTxtView.setText(getString(R.string.uploading));
            uploadDialogBinding.uploadDialogTitle.setText(getString(R.string.please_wait));
            builder.setView(view);

        dialog = builder.setNeutralButton(R.string.minimize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                   mfragment.getActivity().getSupportFragmentManager().popBackStack();
            }
        }).create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        //0 choose appropriate theme


        return dialog;
    }

    public static void setFragment(Fragment fragment){
        mfragment = fragment;
    }







}
