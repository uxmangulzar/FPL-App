package com.locksmith.fpl.Dentist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.locksmith.fpl.DTO.TicketDTO;
import com.locksmith.fpl.Dentist.DTO.TicketDTO1;
import com.locksmith.fpl.Dentist.DTO.UserDTO1;
import com.locksmith.fpl.Dentist.https.HttpsRequest1;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.interfacess.Helper1;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.Dentist.ui.adapter.TicketAdapter1;
import com.locksmith.fpl.R;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.ui.activity.AddTicket;
import com.locksmith.fpl.utils.ProjectUtils;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TechSupportActivity extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener {
    RelativeLayout add_ticket_btn;
    private String TAG = TechSupportActivity.class.getSimpleName();
    private RecyclerView RVhistorylist;
    private TicketAdapter1 ticketAdapter1;
    private ArrayList<TicketDTO1> ticketDTOSList;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence1 prefrence;
    private UserDTO1 userDTO1;
    private LinearLayout tvNo;
    private View view;
    private ImageView ivBack;
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
        prefrence = SharedPrefrence1.getInstance(TechSupportActivity.this);
        userDTO1 = prefrence.getParentUser(Consts1.USER_DTO);
        setUiAction();


    }

    private void setUiAction() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        tvNo = findViewById(R.id.tvNo);
        RVhistorylist = findViewById(R.id.RVhistorylist);
        searchView = findViewById(R.id.searchView);
        searchView.setHint("Search here...");
        searchView.setTextSize(14.0f);
        mLayoutManager = new LinearLayoutManager(TechSupportActivity.this);
        RVhistorylist.setLayoutManager(mLayoutManager);
        add_ticket_btn = findViewById(R.id.ivPost);
        ivBack = findViewById(R.id.ivBack);
        add_ticket_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TechSupportActivity.this, TechAddTicket.class));
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
                                        if (NetworkManager.isConnectToInternet(TechSupportActivity.this)) {
                                            swipeRefreshLayout.setRefreshing(true);
                                            getTicket();
                                        } else {
                                            ProjectUtils.showToast(TechSupportActivity.this, getResources().getString(R.string.internet_concation));
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
        //ProjectUtils.showCustomProgressDialog(TechSupportActivity.this, getResources().getString(R.string.please_wait));
        new HttpsRequest1(Consts1.GET_MY_TICKET_API, getparm(), TechSupportActivity.this).stringPost(TAG, new Helper1() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                //ProjectUtils.hideDialog();
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    tvNo.setVisibility(View.GONE);
                    RVhistorylist.setVisibility(View.VISIBLE);
                    try {
                        ticketDTOSList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<TicketDTO1>>() {
                        }.getType();
                        ticketDTOSList = (ArrayList<TicketDTO1>) new Gson().fromJson(response.getJSONArray("my_ticket").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    ProjectUtils.showToast(TechSupportActivity.this, msg);
                    tvNo.setVisibility(View.VISIBLE);
                    RVhistorylist.setVisibility(View.GONE);
                }
            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts1.USER_ID, userDTO1.getUser_id());
        return parms;
    }

    public void showData() {
        ticketAdapter1 = new TicketAdapter1(TechSupportActivity.this, ticketDTOSList, userDTO1);
        RVhistorylist.setAdapter(ticketAdapter1);
    }

}