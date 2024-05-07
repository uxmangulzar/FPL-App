package com.locksmith.fpl.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.Dentist.ui.fragment.bottomSheetFragments.EmptyFieldFragment;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.utils.ProjectUtils;
import org.json.JSONObject;
import java.util.HashMap;

public class SignUpActivityForUser extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    ProgressDialog mdailogue;
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private EditText CETfirstname, CETLastname, CETemailadd, CETenterpassword, CETenterpassagain, CETnumber;
    private RelativeLayout CBsignup;
    private TextView CTVsignin, us_country_code;
    private String TAG = SignUpActivityForUser.class.getSimpleName();
    private RelativeLayout RRsncbar;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private SharedPreferences firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_user);
        mContext = SignUpActivityForUser.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts.DEVICE_TOKEN, ""));
        setUiAction();
        mAuth = FirebaseAuth.getInstance();
       /* prefrence = SharedPrefrence.getInstance(mContext);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts.DEVICE_TOKEN, ""));*/

    }
    public void setUiAction() {
        RRsncbar = findViewById(R.id.RRsncbar);
        CETfirstname = findViewById(R.id.CETfirstname);
        CETLastname = findViewById(R.id.CETLastname);
        CETemailadd = findViewById(R.id.CETemailadd);
        CETenterpassword = findViewById(R.id.CETenterpassword);
        CETenterpassagain = findViewById(R.id.CETenterpassagain);
        CBsignup = findViewById(R.id.CBsignup);
        CETnumber = findViewById(R.id.CETnumber);
        CTVsignin = findViewById(R.id.CTVsignin);
        us_country_code = findViewById(R.id.us_country_code);
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
                startActivity(new Intent(mContext, SignInActivityForUser.class));
                finish();
                break;
        }
    }

    public void register() {
        Log.d("hereeee", "heree");
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.REGISTER_API, getparm(), mContext).stringPost("myTagg", new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        Log.d("hereeee", response.toString());
                        ProjectUtils.showToast(mContext, msg);
                        userDTO = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);
                        prefrence.setParentUser(userDTO, Consts.USER_DTO);
                        Log.e("youcandoit", msg.toString());
                        prefrence.setBooleanValue(Consts.IS_REGISTERED, true);
                        ProjectUtils.showToast(mContext, msg);
                        Intent in = new Intent(mContext, HomeActivity.class);
                        startActivity(in);
                        finish();
                        Log.e("youcandoit", "run");
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
        } else if (!validation(CETLastname, getResources().getString(R.string.val_lname))) {
            return;

        } else if (CETemailadd.getText().toString().trim().length() <= 0) {
            showErrorDialog();
        } else if (!ProjectUtils.isEmailValid(CETemailadd.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_email));
        } else if (CETenterpassword.getText().toString().trim().length() <= 0) {
            showErrorDialog();
        } else if (!ProjectUtils.isPasswordValid(CETenterpassword.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_pass));
        } else if (CETnumber.getText().toString().trim().length() <= 0) {
            showErrorDialog();
        } else if (!ProjectUtils.isPhoneNumberValid(CETnumber.getText().toString().trim())) {
            showSickbar("Please enter valid number!");
        } else if (!checkpass()) {
            return;
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                Toast.makeText(mContext, "Added", Toast.LENGTH_SHORT).show();
                //verifyNumber();
                register();
            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
            }
        }


    }

    private void verifyNumber() {
        Intent intent = new Intent(SignUpActivityForUser.this, PhoneVerificationForUser.class);
        intent.putExtra("firstName", CETfirstname.getText().toString());
        intent.putExtra("lastName", CETLastname.getText().toString());
        intent.putExtra("email", CETemailadd.getText().toString());
        intent.putExtra("password", CETenterpassword.getText().toString());
        intent.putExtra("number", CETnumber.getText().toString());
        startActivity(intent);
        //finish();
    }

    private boolean checkpass() {
        if (CETenterpassword.getText().toString().trim().equals("")) {
            showSickbar(getResources().getString(R.string.val_pass));
            return false;
        } else if (CETenterpassagain.getText().toString().trim().equals("")) {
            showSickbar(getResources().getString(R.string.val_pass1));
            return false;
        } else if (!CETenterpassword.getText().toString().trim().equals(CETenterpassagain.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.pass_not_match));
            return false;
        }
        return true;
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.NAME, ProjectUtils.getEditTextValue(CETfirstname));
        parms.put(Consts.EMAIL_ID, ProjectUtils.getEditTextValue(CETemailadd));
        parms.put(Consts.PASSWORD, ProjectUtils.getEditTextValue(CETenterpassword));
        parms.put(Consts.ROLE, "2");
        parms.put(Consts.DEVICE_TYPE, "ANDROID");
        parms.put(Consts.DEVICE_TOKEN, firebase.getString(Consts.DEVICE_TOKEN, ""));
        parms.put(Consts.DEVICE_ID, "12345");
        parms.put(Consts.REFERRAL_CODE, "");
        Log.e("youcandoit", parms.toString());
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


}
