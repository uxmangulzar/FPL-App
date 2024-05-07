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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.locksmith.fpl.DTO.UserBooking;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.R;
import com.locksmith.fpl.ui.adapter.MyBookingAdapter;
import com.locksmith.fpl.utils.ProjectUtils;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyBookingsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String TAG = MyBookingsFragment.class.getSimpleName();
    private RecyclerView rvBooking;
    private MyBookingAdapter myBookingAdapter;
    private ArrayList<UserBooking> userBookingList;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    RelativeLayout near_by_tech_btn;
    LinearLayout no_booking_lay;
    private View fragmentView;
    public MyBookingsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyBookingsFragment newInstance(String param1, String param2) {
        MyBookingsFragment fragment = new MyBookingsFragment();
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
        view =  inflater.inflate(R.layout.fragment_my_bookings, container, false);
        if (fragmentView != null) {
            return fragmentView;
        }
        ButterKnife.bind(this,view);
        fragmentView = view;
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        setUiAction(view);

        return view;
    }

    public void setUiAction(View v) {
        rvBooking = v.findViewById(R.id.rvBooking);
        near_by_tech_btn = view.findViewById(R.id.near_by_tech_btn);
        no_booking_lay = view.findViewById(R.id.no_booking_lay);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvBooking.setLayoutManager(mLayoutManager);
        near_by_tech_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e("Runnable", "FIRST");
                                        if (NetworkManager.isConnectToInternet(getActivity())) {
                                            swipeRefreshLayout.setRefreshing(true);
                                            getBooking();
                                        } else {
                                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                                        }
                                    }
                                }
        );

    }
    @Override
    public void onResume() {
        super.onResume();

    }
    public void getBooking() {
        new HttpsRequest(Consts.CURRENT_BOOKING_API, getparm(), getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    no_booking_lay.setVisibility(View.GONE);
                    rvBooking.setVisibility(View.VISIBLE);
                    try {
                        userBookingList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<UserBooking>>() {
                        }.getType();
                        userBookingList = (ArrayList<UserBooking>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                        showData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                    no_booking_lay.setVisibility(View.VISIBLE);
                    rvBooking.setVisibility(View.GONE);
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
        myBookingAdapter = new MyBookingAdapter(MyBookingsFragment.this, userBookingList,userDTO);
        rvBooking.setAdapter(myBookingAdapter);
        myBookingAdapter.notifyDataSetChanged();
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        getBooking();
    }
}