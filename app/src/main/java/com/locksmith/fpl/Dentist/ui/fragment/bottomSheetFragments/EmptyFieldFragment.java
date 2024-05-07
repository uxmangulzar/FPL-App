package com.locksmith.fpl.Dentist.ui.fragment.bottomSheetFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.locksmith.fpl.R;
import com.locksmith.fpl.ui.activity.MapsActivity;

public class EmptyFieldFragment extends BottomSheetDialogFragment {
    RelativeLayout continue_btn;
    public static EmptyFieldFragment newInstance() {
        EmptyFieldFragment fragment = new EmptyFieldFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.empty_field_error_dialog, null);
        dialog.setContentView(contentView);
        setCancelable(false);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        continue_btn = contentView.findViewById(R.id.continue_btn);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
