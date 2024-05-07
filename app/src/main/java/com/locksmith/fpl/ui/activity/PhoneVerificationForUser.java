package com.locksmith.fpl.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.utils.ProjectUtils;
import com.locksmith.fpl.utils.SessionManager;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneVerificationForUser extends AppCompatActivity {

    FirebaseAuth mAuth;
    private UserDTO userDTO;
    private SharedPrefrence prefrence;
    private SharedPreferences firebase;
    String CETfirstname,CETLastname,CETemailadd,CETenterpassword,CTEnumber;
    private TextView verificationMessage, retryTimer;
    private ProgressDialog progressDialog;
    private boolean authInProgress;
    private CountDownTimer countDownTimer;
    private String mVerificationId;
    private EditText otpCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    String number;
    SessionManager sessionManager;
    String completeData;
    private boolean success = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // Do something for lollipop and above versions
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        CETemailadd=getIntent().getStringExtra("email");
        CETfirstname=getIntent().getStringExtra("firstName");
        CETLastname=getIntent().getStringExtra("lastName");
        CETenterpassword=getIntent().getStringExtra("password");
        CTEnumber=getIntent().getStringExtra("number");
        number="+1"+CTEnumber;
        sessionManager=new SessionManager(this);
        verificationMessage = findViewById(R.id.verificationMessage);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        prefrence = SharedPrefrence.getInstance(PhoneVerificationForUser.this);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts.DEVICE_TOKEN, ""));
        retryTimer = findViewById(R.id.resend);
        verificationMessage = findViewById(R.id.verificationMessage);
        otpCode = findViewById(R.id.otp1);
        findViewById(R.id.changeNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        initiateAuth(number);
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(PhoneVerificationForUser.this, "Number Not Found!", Toast.LENGTH_SHORT).show();
                } else {
                    //force authenticate
                    String otp = otpCode.getText().toString();
                    if (!TextUtils.isEmpty(otp) && !TextUtils.isEmpty(mVerificationId))
                        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(mVerificationId, otp));
                    //verifyOtp(otpCode[0].getText().toString() + otpCode[1].getText().toString() + otpCode[2].getText().toString() + otpCode[3].getText().toString());
                }
            }
        });
    }

    public void register() {
        Log.d("hereeee", "heree");
        ProjectUtils.showProgressDialog(PhoneVerificationForUser.this, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.REGISTER_API, getparm(), PhoneVerificationForUser.this).stringPost("myTagg", new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                progressDialog.dismiss();
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        Log.d("hereeEEee", response.toString());
                        ProjectUtils.showToast(PhoneVerificationForUser.this, msg);
                        userDTO = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);
                        prefrence.setParentUser(userDTO, Consts.USER_DTO);
                        Log.e("youcandoit", msg.toString());
                        String userRegisterData = "User Registration Detail" + "\n" + "User Name = " + userDTO.getName() + "\n" + "User Email = " + userDTO.getEmail_id();
                        completeData = userRegisterData + "\n";
                        Log.e("User Register Data", "Data = " + completeData);
                        prefrence.setBooleanValue(Consts.IS_REGISTERED, true);
                        ProjectUtils.showToast(PhoneVerificationForUser.this, msg);
                        sessionManager.saveUserPhoneNo(CTEnumber);
                        Intent in = new Intent(PhoneVerificationForUser.this, HomeActivity.class);
                        startActivity(in);
                        finish();
                        Log.e("youcandoit", "run");
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ProjectUtils.showToast(PhoneVerificationForUser.this, msg);
                }


            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.FIRST_NAME,CETfirstname);
        parms.put(Consts.LAST_NAME,CETLastname);
        parms.put(Consts.NAME,CETfirstname+" "+CETLastname);
        parms.put(Consts.EMAIL_ID, CETemailadd);
        parms.put(Consts.PASSWORD, CETenterpassword);
        parms.put(Consts.ROLE, "2");
        parms.put(Consts.DEVICE_TYPE, "ANDROID");
        parms.put(Consts.DEVICE_TOKEN, firebase.getString(Consts.DEVICE_TOKEN, ""));
        parms.put(Consts.DEVICE_ID, "12345");
        parms.put(Consts.REFERRAL_CODE, "");
        parms.put("number", number);
        Log.e("youcandoit", parms.toString());
        return parms;
    }
    private void initiateAuth(String phone) {
        showProgress(1);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        progressDialog.dismiss();
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        e.printStackTrace();
                        authInProgress = false;
                        progressDialog.dismiss();
                        countDownTimer.cancel();
                        verificationMessage.setText("Error in Detail!");
                        retryTimer.setVisibility(View.VISIBLE);
                        retryTimer.setText("Resending...");
                        retryTimer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                initiateAuth(number);
                            }
                        });
                        Toast.makeText(PhoneVerificationForUser.this, "Error in detail!", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(PhoneVerificationForUser.this, String.format(getString(R.string.error_detail), e.getMessage()) != null ? "\n" + e.getMessage() : "", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        authInProgress = true;
                        progressDialog.dismiss();
                        mVerificationId = verificationId;
                        mResendToken = forceResendingToken;
                        verificationMessage.setText("OTP Sent on "+number);
                        retryTimer.setVisibility(View.GONE);
                    }
                });
        startCountdown();
    }
    private void showProgress(int i) {
        String title = (i == 1) ? getString(R.string.otp_sending) : getString(R.string.otp_verifying);
        String message = (i == 1) ? (String.format(getString(R.string.otp_sending_msg), number)) : getString(R.string.otp_verifying_msg);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        showProgress(2);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.setMessage("Logging In..");
                register();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PhoneVerificationForUser.this, "Error in Verification!", Toast.LENGTH_SHORT).show();
                //Toast.makeText(PhoneVerificationForUser.this, String.format(getString(R.string.error_detail), e.getMessage()) != null ? "\n" + e.getMessage() : "", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                authInProgress = false;
            }
        });
    }
    private void startCountdown() {
        retryTimer.setOnClickListener(null);
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                if (retryTimer != null) {
                    retryTimer.setText(String.valueOf(l / 1000));
                }
            }
            @Override
            public void onFinish() {
                if (retryTimer != null) {
                    retryTimer.setText("Resend Code?");
                    retryTimer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initiateAuth(number);
                        }
                    });
                }
            }
        }.start();
    }
}
