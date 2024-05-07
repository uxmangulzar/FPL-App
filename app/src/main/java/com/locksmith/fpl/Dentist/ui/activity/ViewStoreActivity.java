package com.locksmith.fpl.Dentist.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.locksmith.fpl.R;

public class ViewStoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_store);
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // Do something for lollipop and above versions
                getWindow().setStatusBarColor(getResources().getColor(R.color.bg_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}