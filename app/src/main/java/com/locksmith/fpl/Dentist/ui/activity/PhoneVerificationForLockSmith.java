package com.locksmith.fpl.Dentist.ui.activity;

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
import com.locksmith.fpl.Dentist.DTO.UserDTO1;
import com.locksmith.fpl.Dentist.https.HttpsRequest1;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.interfacess.Helper1;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.R;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.utils.ProjectUtils;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneVerificationForLockSmith extends AppCompatActivity {

    FirebaseAuth mAuth;
    private UserDTO1 userDTO1;
    private SharedPrefrence1 prefrence;
    private SharedPreferences firebase;
    String CETfirstname, CETemailadd, CETenterpassword, CTEnumber;
    private TextView verificationMessage, retryTimer;
    private ProgressDialog progressDialog;
    private boolean authInProgress;
    private CountDownTimer countDownTimer;
    private String mVerificationId;
    private EditText otpCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    String number;
    private String TAG = PhoneVerificationForLockSmith.class.getSimpleName();
    private boolean success = true;
    String completeData;


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
        CETemailadd = getIntent().getStringExtra("email");
        CETfirstname = getIntent().getStringExtra("firstName");
        CETenterpassword = getIntent().getStringExtra("password");
        CTEnumber = getIntent().getStringExtra("number");
        number = "+92" + CTEnumber;

        verificationMessage = findViewById(R.id.verificationMessage);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        prefrence = SharedPrefrence1.getInstance(PhoneVerificationForLockSmith.this);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts.DEVICE_TOKEN, ""));
        retryTimer = findViewById(R.id.resend);
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
                    Toast.makeText(PhoneVerificationForLockSmith.this, "Number Not Found!", Toast.LENGTH_SHORT).show();
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
        Log.d("anyyyyyy", "register: here");
        ProjectUtils.showProgressDialog(PhoneVerificationForLockSmith.this, true, getResources().getString(R.string.please_wait));
        new HttpsRequest1(Consts1.REGISTER_API, getparm(), PhoneVerificationForLockSmith.this).stringPost("hereee", new Helper1() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                Log.d("anyyyyyy", "backResponse: " + response);
                progressDialog.dismiss();

                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        ProjectUtils.showToast(PhoneVerificationForLockSmith.this, msg);
                        userDTO1 = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO1.class);
                        prefrence.setParentUser(userDTO1, Consts1.USER_DTO);
                        prefrence.setBooleanValue(Consts1.IS_REGISTERED, true);
                        String userRegisterData = "Technician Registration Detail" + "\n" + "User Name = " + userDTO1.getName() + "\n" + "User Email = " + userDTO1.getEmail_id();
                        completeData = userRegisterData + "\n";
                        Log.e("Technician RegData", "Data = " + completeData);
                        ProjectUtils.showToast(PhoneVerificationForLockSmith.this, msg);
                        Intent in = new Intent(PhoneVerificationForLockSmith.this, TechHomeActivity.class);
                        startActivity(in);
                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ProjectUtils.showToast(PhoneVerificationForLockSmith.this, msg);
                }


            }
        });
    }


    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts1.NAME, CETfirstname);
        parms.put(Consts1.EMAIL_ID, CETemailadd);
        parms.put(Consts1.PASSWORD, CETenterpassword);
        parms.put(Consts1.ROLE, "1");
        parms.put(Consts1.DEVICE_TYPE, "ANDROID");
        parms.put(Consts1.DEVICE_TOKEN, firebase.getString(Consts1.DEVICE_TOKEN, ""));
        parms.put(Consts1.DEVICE_ID, "12345");
        parms.put(Consts1.REFERRAL_CODE, "");
        parms.put("number", number);
        Log.e("hbhb", parms.toString());
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
                        authInProgress = false;
                        progressDialog.dismiss();
                        countDownTimer.cancel();
                        verificationMessage.setText("Something went wrong please try again");
                        retryTimer.setVisibility(View.VISIBLE);
                        retryTimer.setText("Resending...");
                        retryTimer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                initiateAuth(number);
                            }
                        });
                        e.printStackTrace();
                        Toast.makeText(PhoneVerificationForLockSmith.this, "Error in detail!", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(PhoneVerificationForUser.this, String.format(getString(R.string.error_detail), e.getMessage()) != null ? "\n" + e.getMessage() : "", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        authInProgress = true;
                        progressDialog.dismiss();
                        mVerificationId = verificationId;
                        mResendToken = forceResendingToken;
                        verificationMessage.setText("OTP Sent on " + number);
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
                Toast.makeText(PhoneVerificationForLockSmith.this, "Error in Verification try again!", Toast.LENGTH_SHORT).show();
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
