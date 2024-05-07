package com.locksmith.fpl.ui.fragment;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.locksmith.fpl.DTO.NearbyTechDTO;
import com.locksmith.fpl.DTO.UserBooking;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.MySingleton;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.activity.SupportActivity;
import com.locksmith.fpl.ui.adapter.MyBookingAdapter;
import com.locksmith.fpl.ui.adapter.NearByTechAdapter;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NearByTechFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private static final String ARG_PARAM1 = "param1";
    @BindView(R.id.no_tech_lay)
    LinearLayout no_tech_lay;
    @BindView(R.id.near_by_tech_recycler)
    RecyclerView near_by_tech_recycler;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.support_screen)
    TextView support_screen;
    private String TAG = NearByTechFragment.class.getSimpleName();
    private NearByTechAdapter nearByTechAdapter;
    private ArrayList<NearbyTechDTO> nearbyTechList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private LinearLayoutManager mLayoutManager;
    private View fragmentView;

    public NearByTechFragment() {
        // Required empty public constructor
    }

    public static NearByTechFragment newInstance(String param1) {
        NearByTechFragment fragment = null;
        if (fragment == null) {
            fragment = new NearByTechFragment();
        }
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        view = inflater.inflate(R.layout.fragment_near_by_tech, container, false);
        if (fragmentView != null) {
            return fragmentView;
        }
        ButterKnife.bind(this, view);
        fragmentView = view;
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        setUiAction(view);
        support_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SupportActivity.class));
            }
        });
        return view;
    }

    private void setUiAction(View view) {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        near_by_tech_recycler.setLayoutManager(mLayoutManager);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e("Runnable", "FIRST");
                                        if (NetworkManager.isConnectToInternet(getActivity())) {
                                            swipeRefreshLayout.setRefreshing(true);
                                            getNearByArtist();
                                        } else {
                                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                                        }
                                    }
                                }
        );
    }


    public void getNearByArtist() {
        new HttpsRequest(Consts.GET_NEARBY_ARTIST, getparm(), getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    try {
                        nearbyTechList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<NearbyTechDTO>>() {
                        }.getType();
                        nearbyTechList = (ArrayList<NearbyTechDTO>) new Gson().fromJson(response.getJSONArray("myList").toString(), getpetDTO);
                        if (nearbyTechList.size() > 0) {
                            no_tech_lay.setVisibility(View.GONE);
                            near_by_tech_recycler.setVisibility(View.VISIBLE);
                            showData();
                        } else {
                            no_tech_lay.setVisibility(View.VISIBLE);
                            near_by_tech_recycler.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                    no_tech_lay.setVisibility(View.VISIBLE);
                    near_by_tech_recycler.setVisibility(View.GONE);
                }
            }
        });
    }

    public void volleyRequest() {
        String FAQ_URL = Consts.BASE_URL + "getNearByArtists";
        Log.e("@NEARBYART@", FAQ_URL);
        StringRequest faq_list_req = new StringRequest(Request.Method.POST, FAQ_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("@@NEARBYART@@", response.toString());
                swipeRefreshLayout.setRefreshing(false);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray data = object.getJSONArray("myList");
                    if (data.length() > 0) {
                        no_tech_lay.setVisibility(View.GONE);
                    } else {
                        no_tech_lay.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Consts.USER_ID, userDTO.getUser_id());
                return params;
            }
        };
        faq_list_req.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequestQueue(faq_list_req);
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        //parms.put(Consts.LATITUDE, userDTO.getLive_lat());
        //parms.put(Consts.LONGITUDE, userDTO.getLive_long());
        parms.put(Consts.LATITUDE, "31.42131749");
        parms.put(Consts.LONGITUDE, "74.35908979");
        parms.put(Consts.PAGE, "1");
        return parms;
    }

    public void showData() {

        nearByTechAdapter = new NearByTechAdapter(getActivity(), nearbyTechList);
        near_by_tech_recycler.setAdapter(nearByTechAdapter);
        nearByTechAdapter.notifyDataSetChanged();
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getNearByArtist();
    }
}