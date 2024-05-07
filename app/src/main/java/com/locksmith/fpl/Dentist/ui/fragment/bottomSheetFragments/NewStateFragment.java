package com.locksmith.fpl.Dentist.ui.fragment.bottomSheetFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.locksmith.fpl.R;

public class NewStateFragment extends BottomSheetDialogFragment {
    RelativeLayout subscribe_new_state;
    public static NewStateFragment newInstance() {
        NewStateFragment fragment = new NewStateFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.new_state_dialog, null);
        dialog.setContentView(contentView);
        setCancelable(false);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        subscribe_new_state = contentView.findViewById(R.id.subscribe_new_state);
        subscribe_new_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
