package com.rajeman.myjournal.view.dialogs;

import android.app.Dialog;
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

import com.rajeman.myjournal.R;
import com.rajeman.myjournal.databinding.UploadDialogBinding;

@SuppressWarnings("ConstantConditions")
public class DeleteDialog extends DialogFragment {


    private UploadDialogBinding deleteDialogBinding;

    private AlertDialog dialog;
    private Fragment mFragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo);
        mFragment = this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        deleteDialogBinding = DataBindingUtil.inflate(inflater, R.layout.upload_dialog, null, false);

        View view = deleteDialogBinding.getRoot();


        deleteDialogBinding.uploadDialogTxtView.setText(getString(R.string.deleting));
        deleteDialogBinding.uploadDialogTitle.setText(getString(R.string.please_wait));
        builder.setView(view);

        dialog = builder.setNeutralButton(R.string.minimize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //close the window if neutral button is pressed
                mFragment.getActivity().getSupportFragmentManager().popBackStack();
            }
        }).create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);



        return dialog;
    }


}
