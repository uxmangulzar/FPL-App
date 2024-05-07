package com.locksmith.fpl.Dentist.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.locksmith.fpl.Dentist.DTO.StoreDTO1;
import com.locksmith.fpl.Dentist.DTO.UserDTO1;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.Dentist.ui.adapter.ServiceAdapter;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceListActivity extends AppCompatActivity {
    @BindView(R.id.residential_recycler)
    RecyclerView residential_recycler;
    @BindView(R.id.commercial_recycler)
    RecyclerView commercial_recycler;
    @BindView(R.id.automotive_recycler)
    RecyclerView automotive_recycler;
    LinearLayoutManager residentialManager, commercialManager, automotiveManager;
    private ArrayList<StoreDTO1> residentialList, commercialList, autoMotiveList;
    ServiceAdapter residentialAdapter, commercialAdapter, autoMotiveAdapter;
    private ArrayList<StoreDTO1> storeDTO1ArrayList = new ArrayList<>();
    @BindView(R.id.tvNo_Auto)
    TextView tvNo_Auto;
    @BindView(R.id.tvNo_Resi)
    TextView tvNo_Resi;
    @BindView(R.id.tvNo_Comm)
    TextView tvNo_Comm;
    private SharedPrefrence1 prefrence;
    private UserDTO1 userDTO1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        ButterKnife.bind(this);
        prefrence = SharedPrefrence1.getInstance(this);
        userDTO1 = prefrence.getParentUser(Consts1.USER_DTO);
        setUpViews();
    }

    private void setUpViews() {
        residentialList = new ArrayList<>();
        commercialList = new ArrayList<>();
        autoMotiveList = new ArrayList<>();
        /////*****Residential Recycler Linear Layout Manager Set****/////////
        residentialManager = new LinearLayoutManager(ServiceListActivity.this, LinearLayoutManager.VERTICAL, false);
        residential_recycler.setHasFixedSize(true);
        residential_recycler.setLayoutManager(residentialManager);
        /////*****Commercial Recycler Linear Layout Manager Set****/////////
        commercialManager = new LinearLayoutManager(ServiceListActivity.this, LinearLayoutManager.VERTICAL, false);
        commercial_recycler.setHasFixedSize(true);
        commercial_recycler.setLayoutManager(commercialManager);
        /////*****Automotive Recycler Linear Layout Manager Set****/////////
        automotiveManager = new LinearLayoutManager(ServiceListActivity.this, LinearLayoutManager.VERTICAL, false);
        automotive_recycler.setHasFixedSize(true);
        automotive_recycler.setLayoutManager(automotiveManager);
        if (NetworkManager.isConnectToInternet(this)) {
            getStores();

        } else {
            ProjectUtils.showToast(this, getResources().getString(R.string.internet_concation));
        }
    }

    private void getStores() {
        ProjectUtils.showProgressDialog(this, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_SERVICES, getparm(), this).stringPost("storess", new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                Log.d("flaggg", "backResponse: " + flag);

                if (flag) {
                    try {
                        storeDTO1ArrayList.clear();
                        Type getpetDTO = new TypeToken<List<StoreDTO1>>() {
                        }.getType();
                        storeDTO1ArrayList = (ArrayList<StoreDTO1>) new Gson().fromJson(response.getJSONArray("my_store").toString(), getpetDTO);
                        showData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("flaggg", "backResponse: " + e.getMessage());
                    }
                } else {
                    ProjectUtils.showToast(ServiceListActivity.this, msg);
                }
            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.ARTIST_ID, userDTO1.getUser_id());
        return parms;
    }

    public void showData() {
        residentialList.clear();
        commercialList.clear();
        autoMotiveList.clear();
        if (storeDTO1ArrayList.size() > 0) {
            for (int i = 0; i < storeDTO1ArrayList.size(); i++) {
                StoreDTO1 storeDTO1 = storeDTO1ArrayList.get(i);
                if (storeDTO1ArrayList.get(i).getCategory_id().equalsIgnoreCase("1")) {
                    commercialList.add(storeDTO1);
                } else if (storeDTO1ArrayList.get(i).getCategory_id().equalsIgnoreCase("2")) {
                    autoMotiveList.add(storeDTO1);
                } else if (storeDTO1ArrayList.get(i).getCategory_id().equalsIgnoreCase("3")) {
                    residentialList.add(storeDTO1);
                }
            }
            Log.e("TAG", "showData: residentialList length" + residentialList.size());
            Log.e("TAG", "showData: commercialList length" + commercialList.size());
            Log.e("TAG", "showData: autoMotiveList length" + autoMotiveList.size());

            if (residentialList.size() > 0) {
                tvNo_Resi.setVisibility(View.GONE);
                residentialAdapter = new ServiceAdapter(ServiceListActivity.this, residentialList, userDTO1);
                residential_recycler.setAdapter(residentialAdapter);
            } else {
                tvNo_Resi.setVisibility(View.VISIBLE);
            }
            if (commercialList.size() > 0) {
                tvNo_Comm.setVisibility(View.GONE);
                commercialAdapter = new ServiceAdapter(ServiceListActivity.this, commercialList, userDTO1);
                commercial_recycler.setAdapter(commercialAdapter);
            } else {
                tvNo_Comm.setVisibility(View.VISIBLE);
            }
            if (autoMotiveList.size() > 0) {
                tvNo_Auto.setVisibility(View.GONE);
                autoMotiveAdapter = new ServiceAdapter(ServiceListActivity.this, autoMotiveList, userDTO1);
                automotive_recycler.setAdapter(autoMotiveAdapter);
            } else {
                tvNo_Auto.setVisibility(View.VISIBLE);
            }
        } else {
            tvNo_Resi.setVisibility(View.VISIBLE);
            tvNo_Comm.setVisibility(View.VISIBLE);
            tvNo_Auto.setVisibility(View.VISIBLE);
        }
    }
}