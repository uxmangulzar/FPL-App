package com.locksmith.fpl.Dentist.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.locksmith.fpl.Dentist.ui.fragment.bottomSheetFragments.Suggest_ServiceList_Fragment;
import com.locksmith.fpl.R;

public class ChatListActivity extends AppCompatActivity {
   RelativeLayout suggest_service_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        suggest_service_btn = findViewById(R.id.suggest_service_btn);
        suggest_service_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Suggest_ServiceList_Fragment suggest_serviceList_fragment = new Suggest_ServiceList_Fragment();
                suggest_serviceList_fragment.show(getSupportFragmentManager(), "SSLF");
            }
        });
    }
}
