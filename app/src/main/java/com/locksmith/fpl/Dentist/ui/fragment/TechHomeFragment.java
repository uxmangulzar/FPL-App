package com.locksmith.fpl.Dentist.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
import com.locksmith.fpl.Dentist.DTO.ArtistBooking1;
import com.locksmith.fpl.Dentist.DTO.ArtistDetailsDTO1;
import com.locksmith.fpl.Dentist.DTO.UserDTO1;
import com.locksmith.fpl.Dentist.https.HttpsRequest1;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.interfacess.Helper1;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.R;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class TechHomeFragment extends Fragment {
    AdvanceDrawerLayout drawer;
    Context context;
    RelativeLayout tech_menu, btn_go_online_offline;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = TechHomeFragment.class.getSimpleName();
    private View view;
    private CircleImageView ivArtist;
    private RelativeLayout llTime;
    private Chronometer chronometer;
    private LinearLayout llACDE, llAccept, llDecline, llSt, llStart, llCancel, llFinishJob, llWork;
    private SharedPrefrence1 prefrence;
    private UserDTO1 userDTO1;
    private HashMap<String, String> paramsGetBooking = new HashMap<>();
    private HashMap<String, String> parms = new HashMap<>();
    private HashMap<String, String> paramsForNearByJobs = new HashMap<>();
    private HashMap<String, String> paramsBookingOp;
    private HashMap<String, String> paramsDecline;
    private ArtistBooking1 artistBooking1;
    private CardView cardData, cardNoRequest;
    private MapView mMapView;
    private GoogleMap googleMap;
    Marker My_Location = null;
    Marker User_Location = null;
    Context mycontext;
    Boolean cancel = false;
    Button btn_go;
    TextView tv_online_offline;
    TextView txt_go_btn;
    private HashMap<String, String> paramsUpdate;
    private ArtistDetailsDTO1 artistDetailsDTO1 = new ArtistDetailsDTO1();
    private HashMap<String, String> params = new HashMap<>();
    private AlertDialog deleteDialog;
    public static final String TAG_JOBS = "jobs";

    public TechHomeFragment() {
    }

    public TechHomeFragment(Context context, AdvanceDrawerLayout drawer) {
        // Required empty public constructor
        this.context = context;
        this.drawer = drawer;
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
        View view = inflater.inflate(R.layout.fragment_tech_home, container, false);
        tech_menu = view.findViewById(R.id.tech_menu);
        btn_go_online_offline = view.findViewById(R.id.btn_go_online_offline);
        tv_online_offline = view.findViewById(R.id.txt_online_offline);
        txt_go_btn = view.findViewById(R.id.txt_go_btn);
        prefrence = SharedPrefrence1.getInstance(getActivity());
        userDTO1 = prefrence.getParentUser(Consts1.USER_DTO);
        paramsGetBooking.put(Consts1.ARTIST_ID, userDTO1.getUser_id());
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        //parms.put(Consts1.ARTIST_ID, userDTO1.getUser_id());
        parms.put(Consts1.USER_ID, userDTO1.getUser_id());
        mMapView.onResume(); // needed to get the map to display immediately
        if (userDTO1 != null) {
            if (userDTO1.getStatus().equals("1")) {
                tv_online_offline.setText("You are Online");
            } else {
                tv_online_offline.setText("You are Offline");
            }
        }
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 180, 300);*/
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(Double.parseDouble(prefrence.getValue(Consts1.LATITUDE)), Double.parseDouble(prefrence.getValue(Consts1.LONGITUDE)));
                if (My_Location != null) {
                    My_Location.remove();
                    My_Location = googleMap.addMarker(new MarkerOptions().position(sydney).title(userDTO1.getName()).snippet(userDTO1.getAddress()).title("My Location"));
                } else {
                    My_Location = googleMap.addMarker(new MarkerOptions().position(sydney).title(userDTO1.getName()).snippet(userDTO1.getAddress()).title("My Location"));
                }
                My_Location.setTag(0);
                //    My_Location = googleMap.addMarker(new MarkerOptions().position(sydney).title(userDTO1.getName()).snippet(userDTO1.getAddress()).title("My Location"));
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        int position = (int) (marker.getTag());
                        Log.d("position", "onMarkerClick: " + position);
                        if (position != 0) {
                            Log.d("position", "onMarkerClick: " + position);
                            //customDialogueWithMarkerPosition(position - 1);
                        }
                        return false;
                    }
                });
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                    }
                });
            }
        });
        btn_go_online_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramsUpdate = new HashMap<>();
                paramsUpdate.put(Consts1.USER_ID, userDTO1.getUser_id());
                Log.d("onClick", "onClick: " + artistDetailsDTO1.getIs_online());
                if (artistDetailsDTO1.getIs_online().equalsIgnoreCase("1")) {
                    paramsUpdate.put(Consts1.IS_ONLINE, "0");
                    isOnline();
                } else {
                    paramsUpdate.put(Consts1.IS_ONLINE, "1");
                    isOnline();
                }
            }
        });
        ///*** Drawer open & close ****/////
        tech_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                try {
                    //int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        // Do something for lollipop and above versions
                        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        // finally change the color to any color with transparency
                        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.t_s_nav_bg));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        // Do something for lollipop and above versions
                        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.white));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    public void isOnline() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest1(Consts1.ONLINE_OFFLINE_API, paramsUpdate, getActivity()).stringPost("ONL", new Helper1() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(getActivity(), msg);
                    getArtist();

                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }


            }
        });
    }

    public void getArtist() {
        new HttpsRequest1(Consts1.GET_ARTIST_BY_ID_API, parms, getActivity()).stringPost("onlineee", new Helper1() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        artistDetailsDTO1 = new Gson().fromJson(response.getJSONObject("data").toString(), ArtistDetailsDTO1.class);
                        if (artistDetailsDTO1.getIs_online().equalsIgnoreCase("1")) {
                            tv_online_offline.setText("You are Online");
                            ProjectUtils.showToast(getActivity(), "You are Online successfully");
                            txt_go_btn.setText("Off");
                            paramsForNearByJobs.put(Consts1.ARTIST_ID, userDTO1.getUser_id());
                            paramsForNearByJobs.put(Consts1.LATI, prefrence.getValue(Consts1.LATITUDE));
                            paramsForNearByJobs.put(Consts1.LONGI, prefrence.getValue(Consts1.LONGITUDE));
                            //getAllNearestJobs();
                        } else {
                            ProjectUtils.showToast(getActivity(), "You are Offline  successfully");
                            userDTO1.setStatus("0");
                            tv_online_offline.setText("You are Offline");
                            txt_go_btn.setText("GO");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }
            }
        });
    }
}