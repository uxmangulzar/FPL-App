package com.locksmith.fpl.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.locksmith.fpl.DTO.TicketDTO;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.adapter.TicketAdapter;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SupportActivity extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener {
    RelativeLayout add_ticket_btn;
    private String TAG = SupportActivity.class.getSimpleName();
    private RecyclerView RVhistorylist;
    private TicketAdapter ticketAdapter;
    private ArrayList<TicketDTO> ticketDTOSList;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private LinearLayout tvNo;
    private View view;
    private ImageButton ivBack;
    EditText searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // Do something for lollipop and above versions
                getWindow().setStatusBarColor(getResources().getColor(R.color.bg_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        prefrence = SharedPrefrence.getInstance(SupportActivity.this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        setUiAction();


    }

    private void setUiAction() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        tvNo = findViewById(R.id.tvNo);
        RVhistorylist = findViewById(R.id.RVhistorylist);
        searchView = findViewById(R.id.searchView);
        searchView.setHint("Search here...");
        searchView.setTextSize(14.0f);
        mLayoutManager = new LinearLayoutManager(SupportActivity.this);
        RVhistorylist.setLayoutManager(mLayoutManager);
        add_ticket_btn = findViewById(R.id.ivPost);
        ivBack = findViewById(R.id.ivBack);
        add_ticket_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportActivity.this, AddTicket.class));
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e("Runnable", "FIRST");
                                        if (NetworkManager.isConnectToInternet(SupportActivity.this)) {
                                            swipeRefreshLayout.setRefreshing(true);
                                            getTicket();
                                        } else {
                                            ProjectUtils.showToast(SupportActivity.this, getResources().getString(R.string.internet_concation));
                                        }
                                    }
                                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getTicket();
    }

    private void getTicket() {
        //ProjectUtils.showCustomProgressDialog(SupportActivity.this, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_MY_TICKET_API, getparm(), SupportActivity.this).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                //ProjectUtils.hideDialog();
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    tvNo.setVisibility(View.GONE);
                    RVhistorylist.setVisibility(View.VISIBLE);
                    try {
                        ticketDTOSList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<TicketDTO>>() {
                        }.getType();
                        ticketDTOSList = (ArrayList<TicketDTO>) new Gson().fromJson(response.getJSONArray("my_ticket").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    ProjectUtils.showToast(SupportActivity.this, msg);
                    tvNo.setVisibility(View.VISIBLE);
                    RVhistorylist.setVisibility(View.GONE);
                }
            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        return parms;
    }

    public void showData() {
        ticketAdapter = new TicketAdapter(SupportActivity.this, ticketDTOSList, userDTO);
        RVhistorylist.setAdapter(ticketAdapter);
    }

}