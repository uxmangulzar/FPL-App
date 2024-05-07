package com.locksmith.fpl.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.locksmith.fpl.R;
import com.locksmith.fpl.utils.BasicFragment;
import com.locksmith.fpl.utils.FontUtils;

public class BasicActivity extends FragmentActivity {
    private static final String TAG = BasicActivity.class.getSimpleName();
    Uri myUri;
    int requestCode;

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, BasicActivity.class);
    }

    // Lifecycle Method ////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_basic);
        myUri = Uri.parse(getIntent().getExtras().getString("imageUri"));
        requestCode = getIntent().getExtras().getInt("requestCode");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, BasicFragment.newInstance(myUri)).commit();
        }

        // apply custom font
        FontUtils.setFont(findViewById(R.id.root_layout));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public void startResultActivity(Uri uri) {
        if (isFinishing()) return;
        // Start ResultActivity
        Intent i = getIntent();
        i.putExtra("resultUri", uri.toString());
        setResult(requestCode, i);
        finish();
    }

    public void goBack() {
        onBackPressed();
    }
}
