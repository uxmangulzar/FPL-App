package com.locksmith.fpl.Dentist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.locksmith.fpl.Dentist.DTO.UserDTO1;
import com.locksmith.fpl.Dentist.https.HttpsRequest1;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.interfacess.Helper1;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.activity.SupportActivity;
import com.locksmith.fpl.ui.adapter.ChooseTopicAdapter;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TechAddTicket extends AppCompatActivity {
    private String TAG = TechAddTicket.class.getSimpleName();
    CardView select_topic;
    BottomSheetDialog bottomSheetDialog;
    ChooseTopicAdapter chooseTopicAdapter;
    TextView at_topic_txt, cancel_ticket_btn;
    ArrayList<String> topic_list = new ArrayList<>();
    EditText et_Reason, et_Issue;
    ImageView ivBack;
    RelativeLayout add_ticket_btn;
    private UserDTO1 userDTO1;
    private SharedPrefrence1 prefrence;
    String topic;
    private HashMap<String, String> parmsadd = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ticket);
        setUiAction();
        prefrence = SharedPrefrence1.getInstance(TechAddTicket.this);
        userDTO1 = prefrence.getParentUser(Consts.USER_DTO);
        select_topic = findViewById(R.id.select_topic);
        at_topic_txt = findViewById(R.id.at_topic_txt);
        topic_list.add("General");
        topic_list.add("Technical");
        topic_list.add("Account");
        topic_list.add("Problem with specific job");
        select_topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetDialog();
            }
        });
    }
    private void setUiAction() {
        select_topic = findViewById(R.id.select_topic);
        at_topic_txt = findViewById(R.id.at_topic_txt);
        et_Reason = findViewById(R.id.etReason);
        et_Issue = findViewById(R.id.etIssue);
        ivBack = findViewById(R.id.ivBack);
        cancel_ticket_btn = findViewById(R.id.cancel_ticket_btn);
        add_ticket_btn = findViewById(R.id.add_ticket_btn);
        add_ticket_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
        cancel_ticket_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void openBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(TechAddTicket.this);
        View view = LayoutInflater.from(TechAddTicket.this).inflate(R.layout.bottom_selection_dailog, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        TextView textView = view.findViewById(R.id.choose_topic_txt);
        textView.setText("Choose Topic");
        RelativeLayout cancel_dialog = view.findViewById(R.id.cancel_dialog);
        RecyclerView choose_topic_recycler = view.findViewById(R.id.choose_topic_recycler);
        LinearLayoutManager rvCouponManager = new LinearLayoutManager(TechAddTicket.this, LinearLayoutManager.VERTICAL, false);
        choose_topic_recycler.setLayoutManager(rvCouponManager);
        chooseTopicAdapter = new ChooseTopicAdapter(TechAddTicket.this,choose_topic_recycler,topic_list,at_topic_txt,"1");
        choose_topic_recycler.setAdapter(chooseTopicAdapter);
        //chooseTopicAdapter.notifyDataSetChanged();
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
    }
    public void submitForm() {
        if (!validateReason()) {
            return;
        } else {
            TechAddTicket();

        }
    }

    public boolean validateReason() {
        if (at_topic_txt.getText().toString().equalsIgnoreCase("Choose topic")) {
            Toast.makeText(TechAddTicket.this, "Please select a topic!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (et_Reason.getText().toString().trim().equalsIgnoreCase("")) {
            et_Reason.setError(getResources().getString(R.string.val_title));
            et_Reason.requestFocus();
            return false;
        } else if (et_Issue.getText().toString().trim().equalsIgnoreCase("")) {
            et_Issue.setError("field required!");
            et_Issue.requestFocus();
            return false;
        } else {
            et_Reason.setError(null);
            et_Reason.clearFocus();et_Issue.setError(null);
            et_Issue.clearFocus();
            return true;
        }
    }
    public void TechAddTicket() {
        parmsadd.put(Consts.REASON, ProjectUtils.getEditTextValue(et_Reason));
        parmsadd.put(Consts.DESCRIPTION, ProjectUtils.getEditTextValue(et_Issue));
        parmsadd.put(Consts.USER_ID, userDTO1.getUser_id());
        parmsadd.put(Consts.TOPIC, at_topic_txt.getText().toString());
        parmsadd.put(Consts.JOBID, "");
        ProjectUtils.showCustomProgressDialog(TechAddTicket.this, getResources().getString(R.string.please_wait));
        new HttpsRequest1(Consts1.GENERATE_TICKET_API, parmsadd, TechAddTicket.this).stringPost(TAG, new Helper1() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.hideDialog();
                if (flag) {
                    ProjectUtils.showToast(TechAddTicket.this, msg);
                    startActivity(new Intent(TechAddTicket.this, TechSupportActivity.class));
                    finish();
                } else {
                    ProjectUtils.showToast(TechAddTicket.this, msg);
                }
            }
        });
    }
}
