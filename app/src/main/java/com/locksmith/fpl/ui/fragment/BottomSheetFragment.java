package com.locksmith.fpl.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.locksmith.fpl.ui.activity.MapsActivity;
import com.locksmith.fpl.R;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    RelativeLayout continue_service;
    public static BottomSheetFragment newInstance() {
        BottomSheetFragment fragment = new BottomSheetFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.cancel_service_dialog, null);
        dialog.setContentView(contentView);
        setCancelable(false);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        continue_service = contentView.findViewById(R.id.continue_service);
        continue_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ((MapsActivity)getActivity()).showTechLayout();
            }
        });
    }

}
