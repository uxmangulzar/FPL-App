package com.locksmith.fpl.Dentist.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.locksmith.fpl.Dentist.DTO.UserDTO1;
import com.locksmith.fpl.Dentist.https.HttpsRequest1;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.interfacess.Helper1;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.Dentist.ui.fragment.bottomSheetFragments.EmptyFieldFragment;
import com.locksmith.fpl.R;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.ui.activity.ForgetPasswordUser;
import com.locksmith.fpl.utils.ProjectUtils;
import org.json.JSONObject;
import java.util.HashMap;

public class SignInActivityForTechnician extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private EditText CETemailadd, CETenterpassword;
    private RelativeLayout CBsignIn;
    private TextView CTVBforgot;
    private TextView CTVsignup;
    private String TAG = SignInActivityForTechnician.class.getSimpleName();
    private RelativeLayout RRsncbar;
    private SharedPrefrence1 prefrence;
    private UserDTO1 userDTO1;
    private SharedPreferences firebase;
    CheckBox cb_remember_me;
    String email, pwd;
    private final String REMEMBER_ME = "remember_me_tech";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_tech);
        mContext = SignInActivityForTechnician.this;
        prefrence = SharedPrefrence1.getInstance(mContext);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts1.DEVICE_TOKEN, ""));
        SharedPreferences prefs = getSharedPreferences(REMEMBER_ME, MODE_PRIVATE);
        email = prefs.getString("email", "");//"No name defined" is the default value.
        pwd = prefs.getString("pwd", "");
        setUiAction();
        if (!email.equalsIgnoreCase("")) {
            CETemailadd.setText(email);
            CETenterpassword.setText(pwd);
            cb_remember_me.setChecked(true);
        }

    }

    public void setUiAction() {
        RRsncbar = findViewById(R.id.RRsncbar);
        CETemailadd = findViewById(R.id.CETemailadd);
        CETenterpassword = findViewById(R.id.CETenterpassword);
        CBsignIn = findViewById(R.id.CBsignIn);
        CTVsignup = findViewById(R.id.CTVsignup);
        CTVBforgot = findViewById(R.id.CTVBforgot);
        cb_remember_me = findViewById(R.id.cb_remember_me_tech);
        CBsignIn.setOnClickListener(this);
        CTVBforgot.setOnClickListener(this);
        CTVsignup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CTVBforgot:
                startActivity(new Intent(mContext, ForgetPasswordUser.class));
                break;
            case R.id.CBsignIn:
                clickForSubmit();
                break;
            case R.id.CTVsignup:
                startActivity(new Intent(mContext, SignUpActivityForTechnician.class));
                break;
        }
    }

    public void login() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest1(Consts1.LOGIN_API, getparm(), mContext).stringPost(TAG, new Helper1() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        ProjectUtils.showToast(mContext, msg);
                        userDTO1 = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO1.class);
                        prefrence.setParentUser(userDTO1, Consts1.USER_DTO);
                        prefrence.setBooleanValue(Consts1.IS_REGISTERED, true);
                        ProjectUtils.showToast(mContext, msg);
                        Intent in = new Intent(mContext, TechHomeActivity.class);
                        startActivity(in);
                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ProjectUtils.showToast(mContext, msg);
                }
            }
        });
    }

    public void clickForSubmit() {
        if (CETemailadd.getText().toString().trim().length() <= 0) {
            showErrorDialog();
        } else if (!com.locksmith.fpl.utils.ProjectUtils.isEmailValid(CETemailadd.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_email));
        } else if (CETenterpassword.getText().toString().trim().length() <= 0) {
            showErrorDialog();
        } else if (!com.locksmith.fpl.utils.ProjectUtils.isPasswordValid(CETenterpassword.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_pass));
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                if (cb_remember_me.isChecked()) {
                    SharedPreferences.Editor editor = getSharedPreferences(REMEMBER_ME, MODE_PRIVATE).edit();
                    editor.putString("email", CETemailadd.getText().toString().trim());
                    editor.putString("pwd", CETenterpassword.getText().toString().trim());
                    editor.apply();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(REMEMBER_ME, MODE_PRIVATE).edit();
                    editor.putString("email", "");
                    editor.putString("pwd", "");
                    editor.apply();
                }
                login();
            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
            }
        }
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts1.EMAIL_ID, ProjectUtils.getEditTextValue(CETemailadd));
        parms.put(Consts1.PASSWORD, ProjectUtils.getEditTextValue(CETenterpassword));
        parms.put(Consts1.DEVICE_TYPE, "ANDROID");
        parms.put(Consts1.DEVICE_TOKEN, firebase.getString(Consts1.DEVICE_TOKEN, ""));
        parms.put(Consts1.DEVICE_ID, "12345");
        parms.put(Consts1.ROLE, "1");
        Log.e(TAG + " Login", parms.toString());
        return parms;
    }

    public void showSickbar(String msg) {
        Snackbar snackbar = Snackbar.make(RRsncbar, msg, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    public void showErrorDialog() {
        EmptyFieldFragment emptyFieldFragment = new EmptyFieldFragment().newInstance();
        emptyFieldFragment.show(getSupportFragmentManager(), "EFF");
    }

}
