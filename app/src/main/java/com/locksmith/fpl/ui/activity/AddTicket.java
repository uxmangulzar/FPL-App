package com.locksmith.fpl.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.adapter.ChooseTopicAdapter;
import com.locksmith.fpl.R;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddTicket extends AppCompatActivity {
    private String TAG = AddTicket.class.getSimpleName();
    CardView select_topic;
    BottomSheetDialog bottomSheetDialog;
    ChooseTopicAdapter chooseTopicAdapter;
    TextView at_topic_txt, cancel_ticket_btn;
    ArrayList<String> topic_list = new ArrayList<>();
    EditText et_Reason, et_Issue;
    ImageButton ivBack;
    RelativeLayout add_ticket_btn;
    private UserDTO userDTO;
    private SharedPrefrence prefrence;
    String topic;
    private HashMap<String, String> parmsadd = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ticket);
        setUiAction();
        prefrence = SharedPrefrence.getInstance(AddTicket.this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        select_topic = findViewById(R.id.select_topic);
        at_topic_txt = findViewById(R.id.at_topic_txt);
        topic_list.add("General");
        topic_list.add("Technical");
        topic_list.add("Account");
        topic_list.add("Job Inquiry");
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
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void openBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(AddTicket.this);
        View view = LayoutInflater.from(AddTicket.this).inflate(R.layout.bottom_selection_dailog, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        TextView textView = view.findViewById(R.id.choose_topic_txt);
        textView.setText("Choose Topic");
        RelativeLayout cancel_dialog = view.findViewById(R.id.cancel_dialog);
        RecyclerView choose_topic_recycler = view.findViewById(R.id.choose_topic_recycler);
        LinearLayoutManager rvCouponManager = new LinearLayoutManager(AddTicket.this, LinearLayoutManager.VERTICAL, false);
        choose_topic_recycler.setLayoutManager(rvCouponManager);
        chooseTopicAdapter = new ChooseTopicAdapter(AddTicket.this,choose_topic_recycler,topic_list,at_topic_txt,"1");
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
            addTicket();

        }
    }

    public boolean validateReason() {
        if (at_topic_txt.getText().toString().equalsIgnoreCase("Choose topic")) {
            Toast.makeText(AddTicket.this, "Please select a topic!", Toast.LENGTH_SHORT).show();
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
    public void addTicket() {
        parmsadd.put(Consts.REASON, ProjectUtils.getEditTextValue(et_Reason));
        parmsadd.put(Consts.DESCRIPTION, ProjectUtils.getEditTextValue(et_Issue));
        parmsadd.put(Consts.USER_ID, userDTO.getUser_id());
        parmsadd.put(Consts.TOPIC, at_topic_txt.getText().toString());
        parmsadd.put(Consts.JOBID, "");
        ProjectUtils.showCustomProgressDialog(AddTicket.this, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GENERATE_TICKET_API, parmsadd, AddTicket.this).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.hideDialog();
                if (flag) {
                    ProjectUtils.showToast(AddTicket.this, msg);
                    startActivity(new Intent(AddTicket.this, SupportActivity.class));
                    finish();
                } else {
                    ProjectUtils.showToast(AddTicket.this, msg);
                }
            }
        });
    }
}
