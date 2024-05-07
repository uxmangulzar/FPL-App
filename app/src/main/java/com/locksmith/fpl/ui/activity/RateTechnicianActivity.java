package com.locksmith.fpl.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.locksmith.fpl.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RateTechnicianActivity extends AppCompatActivity {
    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_technician);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}