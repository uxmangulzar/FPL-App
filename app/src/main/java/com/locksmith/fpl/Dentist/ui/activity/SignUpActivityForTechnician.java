package com.locksmith.fpl.Dentist.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.locksmith.fpl.Dentist.DTO.UserDTO1;
import com.locksmith.fpl.Dentist.https.HttpsRequest1;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.interfacess.Helper1;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.Dentist.ui.fragment.bottomSheetFragments.EmptyFieldFragment;
import com.locksmith.fpl.R;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class SignUpActivityForTechnician extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private EditText CETfirstname, CETlastname, CETemailadd, CETenterpassword, CETenterpassagain, CETnumber;
    private RelativeLayout CBsignup;
    ProgressDialog mdailogue;
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    SignInButton nGoogleBtn;
    private TextView CTVsignin;
    private String TAG = SignUpActivityForTechnician.class.getSimpleName();
    private RelativeLayout RRsncbar;
    private SharedPrefrence1 prefrence;
    private UserDTO1 userDTO1;
    private SharedPreferences firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_tech);
        mContext = SignUpActivityForTechnician.this;
        prefrence = SharedPrefrence1.getInstance(mContext);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts1.DEVICE_TOKEN, ""));
        setUiAction();
        mAuth = FirebaseAuth.getInstance();
        prefrence = SharedPrefrence1.getInstance(mContext);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts1.DEVICE_TOKEN, ""));

    }

    public void setUiAction() {
        RRsncbar = findViewById(R.id.RRsncbar);
        CETfirstname = findViewById(R.id.CETfirstname);
        CETlastname = findViewById(R.id.CETlastname);
        CETemailadd = findViewById(R.id.CETemailadd);
        CETenterpassword = findViewById(R.id.CETenterpassword);
        CETenterpassagain = findViewById(R.id.CETenterpassagain);
        CETnumber = findViewById(R.id.CETnumber);
        CBsignup = findViewById(R.id.CBsignup);
        CTVsignin = findViewById(R.id.CTVsignin);
        CBsignup.setOnClickListener(this);
        CTVsignin.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CBsignup:
                clickForSubmit();
                break;
            case R.id.CTVsignin:
                startActivity(new Intent(mContext, SignInActivityForTechnician.class));
                finish();
                break;
        }
    }

    public void register() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest1(Consts1.REGISTER_API, getparm(), mContext).stringPost(TAG, new Helper1() {
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
        if (!validation(CETfirstname, getResources().getString(R.string.val_fname))) {
            return;
        } else if (!validation(CETlastname, getResources().getString(R.string.val_lname))) {
            return;

        } else if (CETemailadd.getText().toString().trim().length() <= 0) {
            showErrorDialog();
        } else if (!com.locksmith.fpl.utils.ProjectUtils.isEmailValid(CETemailadd.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_email));
        } else if (CETenterpassword.getText().toString().trim().length() <= 0) {
            showErrorDialog();
        } else if (!com.locksmith.fpl.utils.ProjectUtils.isPasswordValid(CETenterpassword.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_pass));
        } else if (CETnumber.getText().toString().trim().length() <= 0) {
            showErrorDialog();
        } else if (!com.locksmith.fpl.utils.ProjectUtils.isPhoneNumberValid(CETnumber.getText().toString().trim())) {
            showSickbar("Please enter valid number!");
        } else if (!checkpass()) {
            return;
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                //verifyNumber();
                register();
            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
            }
        }


    }

    private boolean checkpass() {
        if (CETenterpassword.getText().toString().trim().equals("")) {
            showSickbar(getResources().getString(R.string.val_pass));
            return false;
        } else if (CETenterpassagain.getText().toString().trim().equals("")) {
            showSickbar(getResources().getString(R.string.val_pass1));
            return false;
        } else if (!CETenterpassword.getText().toString().trim().equals(CETenterpassagain.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_pass3));
            return false;
        }
        return true;
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts1.FIRST_NAME, ProjectUtils.getEditTextValue(CETfirstname));
        parms.put(Consts1.LAST_NAME, ProjectUtils.getEditTextValue(CETlastname));
        parms.put(Consts1.NAME, ProjectUtils.getEditTextValue(CETfirstname) + ProjectUtils.getEditTextValue(CETlastname));
        parms.put(Consts1.EMAIL_ID, ProjectUtils.getEditTextValue(CETemailadd));
        parms.put(Consts1.PASSWORD, ProjectUtils.getEditTextValue(CETenterpassword));
        parms.put(Consts1.ROLE, "1");
        parms.put(Consts1.DEVICE_TYPE, "ANDROID");
        parms.put(Consts1.DEVICE_TOKEN, firebase.getString(Consts1.DEVICE_TOKEN, ""));
        parms.put(Consts1.DEVICE_ID, "12345");
        parms.put(Consts1.REFERRAL_CODE, "");
        Log.e(TAG, parms.toString());
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

    public boolean validation(EditText editText, String msg) {
        if (!ProjectUtils.isEditTextFilled(editText)) {
            showErrorDialog();
            return false;
        } else {
            return true;
        }
    }


    private void verifyNumber() {
        Intent intent = new Intent(SignUpActivityForTechnician.this, PhoneVerificationForLockSmith.class);
        intent.putExtra("firstName", CETfirstname.getText().toString());
        intent.putExtra("email", CETemailadd.getText().toString());
        intent.putExtra("password", CETenterpassword.getText().toString());
        intent.putExtra("number", CETnumber.getText().toString());
        startActivity(intent);
        finish();
    }

}
