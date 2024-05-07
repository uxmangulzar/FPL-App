package com.locksmith.fpl.ui.activity;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
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

public class SignInActivityForUser extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private EditText CETemailadd, CETenterpassword;
    private RelativeLayout CBsignIn;
    private TextView CTVBforgot;
    private TextView CTVsignup;
    private String TAG = SignInActivityForUser.class.getSimpleName();
    private RelativeLayout RRsncbar;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private SharedPreferences firebase;
    CheckBox cb_remember_me;
    String email,pwd;


    private final String REMEMBER_ME="remember_me";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mContext = SignInActivityForUser.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts.DEVICE_TOKEN, ""));
        setUiAction();
        SharedPreferences prefs = getSharedPreferences(REMEMBER_ME, MODE_PRIVATE);
        email = prefs.getString("email", "");//"No name defined" is the default value.
        pwd = prefs.getString("pwd", "");

        if(!email.equalsIgnoreCase("")){
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
        cb_remember_me=findViewById(R.id.cb_remember_me);
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
                startActivity(new Intent(mContext, SignUpActivityForUser.class));
                break;
        }
    }

    public void login() {
        ProjectUtils.showCustomProgressDialog(SignInActivityForUser.this,  getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.LOGIN_API, getparm(), mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.hideDialog();
                if (flag) {
                    try {

                        userDTO = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);
                        prefrence.setParentUser(userDTO, Consts.USER_DTO);
                        prefrence.setBooleanValue(Consts.IS_REGISTERED, true);
                        ProjectUtils.showToast(mContext, msg);
                        Intent in = new Intent(mContext, HomeActivity.class);
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
        if (CETemailadd.getText().toString().trim().length()<=0) {
            showErrorDialog();
        }
        else if (!ProjectUtils.isEmailValid(CETemailadd.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_email));
        }
        else if (CETenterpassword.getText().toString().trim().length()<=0){
            showErrorDialog();
        }
        else if (!ProjectUtils.isPasswordValid(CETenterpassword.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_pass));
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                if(cb_remember_me.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences(REMEMBER_ME, MODE_PRIVATE).edit();
                    editor.putString("email", CETemailadd.getText().toString().trim());
                    editor.putString("pwd", CETenterpassword.getText().toString().trim());
                    editor.apply();
                }
                else{
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
        parms.put(Consts.EMAIL_ID, ProjectUtils.getEditTextValue(CETemailadd));
        parms.put(Consts.PASSWORD, ProjectUtils.getEditTextValue(CETenterpassword));
        parms.put(Consts.DEVICE_TYPE, "ANDROID");
        parms.put(Consts.DEVICE_TOKEN, firebase.getString(Consts.DEVICE_TOKEN, ""));
        parms.put(Consts.DEVICE_ID, "12345");
        parms.put(Consts.ROLE, "2");
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
