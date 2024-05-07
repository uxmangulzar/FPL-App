package com.locksmith.fpl.Dentist.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.locksmith.fpl.Dentist.DTO.UserDTO1;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.Dentist.ui.fragment.StoreFragment;
import com.locksmith.fpl.Dentist.ui.fragment.TechBookingsFragment;
import com.locksmith.fpl.Dentist.ui.fragment.TechHomeFragment;
import com.locksmith.fpl.Dentist.ui.fragment.TechSettingFragment;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.utils.ProgressDialogFragment;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TechHomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String PROGRESS_DIALOG = "start_map_wait_frag";
    AdvanceDrawerLayout drawer;
    Window window;
    ImageView cross_btn;
    LinearLayout feedback_btn, support_btn, req_btn, store_btn, join_fpl_btn, tc_home, tc_chat, tc_earn, tc_booking, tc_Sett;
    ImageView ic_home, ic_chat, ic_setting, ic_booking, tc_earning_icon;
    TextView txt_home, txt_chat, txt_setting, txt_booking, tc_earning_txt, tech_toolbar_txt;
    RelativeLayout tech_menuBtn;
    NavigationView navigationView;
    AppBarLayout tech_appBarLayout;
    View tech_Search_bar;
    // location last updated time
    private String mLastUpdateTime;
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private AlertDialog alertDialog;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private static final String TAG = TechHomeActivity.class.getSimpleName();
    private SharedPrefrence1 prefrence1;
    private SharedPrefrence prefrence;
    private UserDTO1 userDTO1;
    HashMap<String, String> parms = new HashMap<>();
    private Handler handler = new Handler();

    public TechHomeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_home);
        initiateViews();
        navigationView.setNavigationItemSelectedListener(this);
        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setViewElevation(Gravity.START, 20f);
        drawer.setRadius(GravityCompat.START, 80);
        drawer.useCustomBehavior(Gravity.END);

        cross_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        tech_menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tech_drawer", "tech menu click to open Drawer");
                drawer.openDrawer(GravityCompat.START);
            }
        });
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                Log.e("tech_drawer", "onDrawerSlide: was called");
                changeStatusBarColor(R.color.t_s_nav_bg);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.e("tech_drawer", "onDrawerClosed: was called");
                changeStatusBarColor(R.color.bg_color);
            }
        });
    }

    private void initiateViews() {
        drawer = findViewById(R.id.tech_drawer_layout);
        cross_btn = findViewById(R.id.tech_cross_btn);
        support_btn = findViewById(R.id.tc_support_btn);
        feedback_btn = findViewById(R.id.tc_feedback_btn);
        req_btn = findViewById(R.id.tc_req_btn);
        join_fpl_btn = findViewById(R.id.join_fpl_btn);
        store_btn = findViewById(R.id.tc_store_btn);
        tech_menuBtn = findViewById(R.id.tech_menuBtn);
        tech_appBarLayout = findViewById(R.id.tech_appBarLayout);
        tech_Search_bar = findViewById(R.id.tech_search_bar);
        tc_home = findViewById(R.id.tc_home);
        tc_chat = findViewById(R.id.tc_chat);
        tc_earn = findViewById(R.id.tc_earnings);
        tc_booking = findViewById(R.id.tc_booking);
        tc_Sett = findViewById(R.id.tc_settings);
        //logout = findViewById(R.id.logout);
        ic_home = findViewById(R.id.tc_home_icon);
        ic_chat = findViewById(R.id.tc_chat_icon);
        ic_booking = findViewById(R.id.tc_booking_icon);
        ic_setting = findViewById(R.id.tc_setting_icon);
        tc_earning_icon = findViewById(R.id.tc_earning_icon);
        txt_home = findViewById(R.id.tc_home_txt);
        txt_chat = findViewById(R.id.tc_chat_txt);
        txt_booking = findViewById(R.id.tc_booking_txt);
        txt_setting = findViewById(R.id.tc_setting_txt);
        tc_earning_txt = findViewById(R.id.tc_earning_txt);
        tech_toolbar_txt = findViewById(R.id.tech_toolbar_txt);
        tc_home.setOnClickListener(this::onClick);
        tc_chat.setOnClickListener(this::onClick);
        tc_booking.setOnClickListener(this::onClick);
        tc_Sett.setOnClickListener(this::onClick);
        tc_earn.setOnClickListener(this::onClick);
        join_fpl_btn.setOnClickListener(this::onClick);
        store_btn.setOnClickListener(this::onClick);
        support_btn.setOnClickListener(this::onClick);
        feedback_btn.setOnClickListener(this::onClick);
        navigationView = findViewById(R.id.tech_nav_view);
        ///***Tech & User Preferences initialized here***///
        prefrence = SharedPrefrence.getInstance(this);
        prefrence1 = SharedPrefrence1.getInstance(this);
        userDTO1 = prefrence1.getParentUser(Consts.USER_DTO);
        prefrence1.setBooleanValue(Consts.LOGIN_AS_LOCK_SMITH, true);
        prefrence.setBooleanValue(Consts.LOGIN_AS_USER, false);
        ///***FusedLocationProvider initialized here***///
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };
        mRequestingLocationUpdates = false;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(1.0f);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        checkPermissions();
        showProgress();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //CallURLAPI();
                dismissProgress();
                loadFragment(new TechHomeFragment(TechHomeActivity.this, drawer), "HF");
            }
        }, 2000);

    }

    public void showProgress() {
        ProgressDialogFragment f = ProgressDialogFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(f, PROGRESS_DIALOG)
                .commitAllowingStateLoss();
    }

    public void dismissProgress() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager == null) return;
        ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tc_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                ic_home.setImageResource(R.drawable.ic_homecolor);
                txt_home.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt_chat.setTextColor(getResources().getColor(R.color.black));
                txt_setting.setTextColor(getResources().getColor(R.color.black));
                txt_booking.setTextColor(getResources().getColor(R.color.black));
                tc_earning_txt.setTextColor(getResources().getColor(R.color.black));
                ic_setting.setImageResource(R.drawable.ic_sett);
                ic_chat.setImageResource(R.drawable.ic_chat);
                tc_earning_icon.setImageResource(R.drawable.ic_doller);
                ic_booking.setImageResource(R.drawable.ic_booking);
                tech_appBarLayout.setVisibility(View.GONE);
                tech_Search_bar.setVisibility(View.GONE);
                changeStatusBarColor(R.color.white);
                loadFragment(new TechHomeFragment(TechHomeActivity.this, drawer), "HF");
                break;
            case R.id.tc_chat:
                Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show();
                ic_home.setImageResource(R.drawable.ic_home);
                ic_setting.setImageResource(R.drawable.ic_sett);
                ic_chat.setImageResource(R.drawable.ic_chatcolor);
                ic_booking.setImageResource(R.drawable.ic_booking);
                tc_earning_icon.setImageResource(R.drawable.ic_doller);
                txt_home.setTextColor(getResources().getColor(R.color.black));
                txt_chat.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt_setting.setTextColor(getResources().getColor(R.color.black));
                txt_booking.setTextColor(getResources().getColor(R.color.black));
                tc_earning_txt.setTextColor(getResources().getColor(R.color.black));
                startActivity(new Intent(TechHomeActivity.this, ChatListActivity.class));
                break;
            case R.id.tc_earnings:
                Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
                ic_home.setImageResource(R.drawable.ic_home);
                ic_setting.setImageResource(R.drawable.ic_sett);
                ic_chat.setImageResource(R.drawable.ic_chat);
                ic_booking.setImageResource(R.drawable.ic_booking);
                tc_earning_icon.setImageResource(R.drawable.ic_doller_color);
                txt_home.setTextColor(getResources().getColor(R.color.black));
                txt_chat.setTextColor(getResources().getColor(R.color.black));
                txt_setting.setTextColor(getResources().getColor(R.color.black));
                txt_booking.setTextColor(getResources().getColor(R.color.black));
                tc_earning_txt.setTextColor(getResources().getColor(R.color.colorPrimary));

                break;
            case R.id.tc_booking:
                Toast.makeText(this, "Booking", Toast.LENGTH_SHORT).show();
                ic_home.setImageResource(R.drawable.ic_home);
                ic_setting.setImageResource(R.drawable.ic_sett);
                ic_chat.setImageResource(R.drawable.ic_chat);
                ic_booking.setImageResource(R.drawable.ic_bookingcolor);
                tc_earning_icon.setImageResource(R.drawable.ic_doller);
                txt_home.setTextColor(getResources().getColor(R.color.black));
                txt_chat.setTextColor(getResources().getColor(R.color.black));
                txt_setting.setTextColor(getResources().getColor(R.color.black));
                txt_booking.setTextColor(getResources().getColor(R.color.colorPrimary));
                tc_earning_txt.setTextColor(getResources().getColor(R.color.black));
                tech_toolbar_txt.setText("My Booking");
                tech_appBarLayout.setVisibility(View.VISIBLE);
                tech_Search_bar.setVisibility(View.VISIBLE);
                changeStatusBarColor(R.color.bg_color);
                loadFragment(new TechBookingsFragment(), "TBF");
                break;
            case R.id.tc_settings:
                Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
                ic_home.setImageResource(R.drawable.ic_home);
                ic_setting.setImageResource(R.drawable.ic_settingscolor);
                ic_chat.setImageResource(R.drawable.ic_chat);
                ic_booking.setImageResource(R.drawable.ic_booking);
                tc_earning_icon.setImageResource(R.drawable.ic_doller);
                txt_home.setTextColor(getResources().getColor(R.color.black));
                txt_chat.setTextColor(getResources().getColor(R.color.black));
                txt_setting.setTextColor(getResources().getColor(R.color.colorPrimary));
                txt_booking.setTextColor(getResources().getColor(R.color.black));
                tc_earning_txt.setTextColor(getResources().getColor(R.color.black));
                tech_toolbar_txt.setText("Settings");
                tech_appBarLayout.setVisibility(View.VISIBLE);
                tech_Search_bar.setVisibility(View.VISIBLE);
                changeStatusBarColor(R.color.bg_color);
                loadFragment(new TechSettingFragment(), "TSF");
                break;
            case R.id.join_fpl_btn:
                startActivity(new Intent(TechHomeActivity.this, JoinFplActivity.class));
                break;
            case R.id.tc_support_btn:
                startActivity(new Intent(TechHomeActivity.this, TechSupportActivity.class));
                break;
            case R.id.tc_feedback_btn:
                startActivity(new Intent(TechHomeActivity.this, TechnicianFeedBack.class));
                break;
            case R.id.tc_store_btn:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                ic_home.setImageResource(R.drawable.ic_home);
                ic_setting.setImageResource(R.drawable.ic_sett);
                ic_chat.setImageResource(R.drawable.ic_chat);
                ic_booking.setImageResource(R.drawable.ic_booking);
                tc_earning_icon.setImageResource(R.drawable.ic_doller);
                txt_home.setTextColor(getResources().getColor(R.color.black));
                txt_chat.setTextColor(getResources().getColor(R.color.black));
                txt_setting.setTextColor(getResources().getColor(R.color.black));
                txt_booking.setTextColor(getResources().getColor(R.color.black));
                tc_earning_txt.setTextColor(getResources().getColor(R.color.black));
                tech_toolbar_txt.setText("Store");
                tech_appBarLayout.setVisibility(View.VISIBLE);
                tech_Search_bar.setVisibility(View.VISIBLE);
                changeStatusBarColor(R.color.bg_color);
                loadFragment(new StoreFragment(), "Store_frag");
                break;

        }

    }

    private void loadFragment(Fragment fragment, String f_Tag) {
        if (fragment != null) {
            Bundle b = new Bundle();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.tech_frame_container, fragment, f_Tag);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeStatusBarColor(int sld_color) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // Do something for lollipop and above versions
                getWindow().setStatusBarColor(getResources().getColor(sld_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * **********************************************************************************************
     * Here is code that will GET Runtime Permission.
     ************************************************************************************************
     */
    private void checkPermissions() {
        Dexter.withContext(TechHomeActivity.this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(UserCurrentLocation.this, "", Toast.LENGTH_SHORT).show();
                            getLocation();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            new androidx.appcompat.app.AlertDialog.Builder(TechHomeActivity.this)
                                    .setTitle("Permission Required")
                                    .setMessage("Location permission is mandatory to perform other action in this app")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(intent, 51);

                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();

    }

    ///*** User Current Location Code Start Region ***///
    private void getLocation() {
        mRequestingLocationUpdates = true;
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(TechHomeActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        //Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        updateLocationUI();
                    }
                })
                .addOnFailureListener(TechHomeActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(TechHomeActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                //Toast.makeText(TechHomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                        updateLocationUI();
                    }
                });
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }
    }

    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            Log.e("@LocationParams@", "updateLocationUI: " +
                    "Lat: " + mCurrentLocation.getLatitude() + ", " +
                    "Lng: " + mCurrentLocation.getLongitude()
            );
            Log.e("Last updated on: ", "time = " + mLastUpdateTime);
            prefrence1.setValue(Consts1.LATITUDE, mCurrentLocation.getLatitude() + "");
            prefrence1.setValue(Consts1.LONGITUDE, mCurrentLocation.getLongitude() + "");
            parms.put(Consts.USER_ID, userDTO1.getUser_id());
            parms.put(Consts.ROLE, "1");
            parms.put(Consts.LATITUDE, mCurrentLocation.getLatitude() + "");
            parms.put(Consts.LONGITUDE, mCurrentLocation.getLongitude() + "");
            //updateLocation();
        }
    }

    public void updateLocation() {
        // ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.UPDATE_LOCATION_API, parms, TechHomeActivity.this).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    Log.e(TAG, "Location update response = " + response.toString());
                } else {
                    ProjectUtils.showToast(TechHomeActivity.this, msg);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

}