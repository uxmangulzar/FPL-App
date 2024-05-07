package com.locksmith.fpl.ui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.locksmith.fpl.DTO.PostedJobDTO;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.adapter.JobsAdapter;
import com.locksmith.fpl.utils.ProjectUtils;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyRequestFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private String TAG = MyRequestFragment.class.getSimpleName();
    private RecyclerView my_req_Recycler;
    private JobsAdapter jobsAdapter;
    private ArrayList<PostedJobDTO> postedJobDTOSList;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private View view;
    private ImageView ivPost;
    private EditText svSearch;
    private RelativeLayout rlSearch;
    private SwipeRefreshLayout swipeRefreshLayout;
    // TODO: Rename and change types of parameters
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LinearLayout no_request_layout;

    public MyRequestFragment() {
        // Required empty public constructor
    }

    public static MyRequestFragment newInstance(String param1, String param2) {
        MyRequestFragment fragment = new MyRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_request, container, false);
        no_request_layout = view.findViewById(R.id.no_request_layout);
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        setUiAction(view);
        return view;
    }

    public void setUiAction(View v) {
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        svSearch = v.findViewById(R.id.searchView);
        svSearch.setHint("Search here...");
        svSearch.setTextSize(14.0f);
        my_req_Recycler = v.findViewById(R.id.my_req_recycler);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        my_req_Recycler.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e("Runnable", "FIRST");
                                        if (NetworkManager.isConnectToInternet(getActivity())) {
                                            swipeRefreshLayout.setRefreshing(true);
                                            getjobs();
                                        } else {
                                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                                        }
                                    }
                                }
        );
    }

    public void getjobs() {
        //ProjectUtils.showCustomProgressDialog(getActivity(), getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_ALL_JOB_USER_API, getparm(), getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                //ProjectUtils.hideDialog();
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    no_request_layout.setVisibility(View.GONE);
                    my_req_Recycler.setVisibility(View.VISIBLE);
                    try {
                        postedJobDTOSList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<PostedJobDTO>>() {
                        }.getType();
                        postedJobDTOSList = (ArrayList<PostedJobDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                        showData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        rlSearch.setVisibility(View.GONE);
                    }
                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                    no_request_layout.setVisibility(View.VISIBLE);
                    my_req_Recycler.setVisibility(View.GONE);
                }
            }
        });
    }

    public HashMap<String, String> getparm() {
        Log.d("iddd", "getparm: " + userDTO.getUser_id());
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        return parms;
    }

    public void showData() {
        jobsAdapter = new JobsAdapter(getContext(), MyRequestFragment.this,postedJobDTOSList, userDTO);
        my_req_Recycler.setAdapter(jobsAdapter);
    }

    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getjobs();
    }

}