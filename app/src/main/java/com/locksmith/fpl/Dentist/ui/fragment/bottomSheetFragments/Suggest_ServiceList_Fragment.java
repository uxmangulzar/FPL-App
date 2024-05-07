package com.locksmith.fpl.Dentist.ui.fragment.bottomSheetFragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.locksmith.fpl.Dentist.ui.activity.ServiceListActivity;
import com.locksmith.fpl.Dentist.ui.activity.ViewStoreActivity;
import com.locksmith.fpl.R;

public class Suggest_ServiceList_Fragment extends BottomSheetDialogFragment {
    RelativeLayout service_list_btn,get_all_store_btn;
    public static Suggest_ServiceList_Fragment newInstance() {
        Suggest_ServiceList_Fragment fragment = new Suggest_ServiceList_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.suggest_service_layout_design, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        service_list_btn = contentView.findViewById(R.id.service_list_btn);
        get_all_store_btn = contentView.findViewById(R.id.get_all_store_btn);
        service_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
               startActivity(new Intent(getContext(), ServiceListActivity.class));
            }
        });
        get_all_store_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(getContext(), ViewStoreActivity.class));
            }
        });
    }

}
