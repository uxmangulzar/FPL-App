package com.locksmith.fpl.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.Services.GpsUtils;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.fragment.ChatFragment;
import com.locksmith.fpl.ui.fragment.HomeFragment;
import com.locksmith.fpl.ui.fragment.MyBookingsFragment;
import com.locksmith.fpl.ui.fragment.MyRequestFragment;
import com.locksmith.fpl.ui.fragment.SettingFragment;
import com.locksmith.fpl.R;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
    HashMap<String, String> parms = new HashMap<>();
    private AdvanceDrawerLayout drawer;
    private TextView user_name, toolbar_txt;
    private RelativeLayout menuBtn, main_cat;
    private Window window;
    private ImageView cross_btn, user_image;
    private BottomSheetDialog bottomSheetDialog;
    private FloatingActionButton fab;
    private LinearLayout rate_tech_btn, support_btn, my_req_btn, logout, near_by_tech_btn;
    private ImageView ic_home, ic_chat, ic_setting, ic_booking;
    private TextView txt_home, txt_chat, txt_setting, txt_booking;
    private NavigationView navigationView;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    public static int navItemIndex = 0;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private SharedPrefrence1 prefrence1;
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
    private MyBookingsFragment myBookingsFragment;
    private static HomeActivity instance = null;

    public static HomeActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        instance = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.bg_color));
        }
        // initialize the necessary View
        initiateViews();
        navigationView.setNavigationItemSelectedListener(this);
        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setViewElevation(Gravity.START, 20f);
        drawer.setRadius(GravityCompat.START, 80);
        drawer.useCustomBehavior(Gravity.END);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                try {
                    //int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        // Do something for lollipop and above versions
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        // finally change the color to any color with transparency
                        window.setStatusBarColor(getResources().getColor(R.color.secondary));
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
                        window.setStatusBarColor(getResources().getColor(R.color.bg_color));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        cross_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, PostRequestActivity.class));
            }
        });
        rate_tech_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this, RateTechnicianActivity.class));
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        support_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SupportActivity.class));
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);
        //Load HomeFragment
        loadFragment(new HomeFragment(HomeActivity.this), "HF");
    }

    private void initiateViews() {
        myBookingsFragment = new MyBookingsFragment();
        drawer = findViewById(R.id.drawer_layout);
        fab = findViewById(R.id.fab);
        cross_btn = findViewById(R.id.cross_btn);
        user_image = findViewById(R.id.user_image);
        toolbar_txt = findViewById(R.id.toolbar_txt);
        rate_tech_btn = findViewById(R.id.rate_tech_btn);
        support_btn = findViewById(R.id.support_btn);
        my_req_btn = findViewById(R.id.my_req_btn);
        near_by_tech_btn = findViewById(R.id.near_by_tech_btn);
        logout = findViewById(R.id.logout);
        user_name = findViewById(R.id.user_name);
        ic_home = findViewById(R.id.home_icon);
        ic_chat = findViewById(R.id.chat_icon);
        ic_booking = findViewById(R.id.booking_icon);
        ic_setting = findViewById(R.id.setting_icon);
        txt_home = findViewById(R.id.home_txt);
        txt_chat = findViewById(R.id.chat_txt);
        txt_booking = findViewById(R.id.booking_txt);
        txt_setting = findViewById(R.id.setting_txt);
        ic_home.setOnClickListener(this::onClick);
        ic_chat.setOnClickListener(this::onClick);
        ic_booking.setOnClickListener(this::onClick);
        ic_setting.setOnClickListener(this::onClick);
        my_req_btn.setOnClickListener(this::onClick);
        near_by_tech_btn.setOnClickListener(this::onClick);
        logout.setOnClickListener(this::onClick);
        //user_name.setText("C7-Usman");
        menuBtn = findViewById(R.id.menuBtn);
        //main_cat = findViewById(R.id.main_cat);
        //user_name.setText("Welcome to our ne lock smith app");
        navigationView = findViewById(R.id.nav_view);
        //*** SharedPrefrence for the App Login Save || Location Saved ***///
        prefrence = SharedPrefrence.getInstance(this);
        prefrence1 = SharedPrefrence1.getInstance(this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        prefrence1.setBooleanValue(Consts.LOGIN_AS_LOCK_SMITH, false);
        prefrence.setBooleanValue(Consts.LOGIN_AS_USER, true);
        ///*** Get User Store Data After Login and Display ***///
        Glide.with(HomeActivity.this).
                load(userDTO.getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(user_image);
        user_name.setText(userDTO.getName());
        ///***FusedLocationProvider initializaed here***///
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
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        checkPermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_icon:
                navItemIndex = 0;
                homeTab();
                loadFragment(new HomeFragment(HomeActivity.this), "HF");
                toolbar_txt.setText("Home");
                break;
            case R.id.chat_icon:
                navItemIndex = 1;
                chatTab();
                open_chat_fragment("1");
                toolbar_txt.setText("Chat");
                break;
            case R.id.setting_icon:
                navItemIndex = 2;
                settingTab();
                loadFragment(new SettingFragment(), "SF");
                toolbar_txt.setText("Settings");
                break;
            case R.id.booking_icon:
                navItemIndex = 3;
                bookingTab();
                loadFragment(myBookingsFragment, "MBF");
                toolbar_txt.setText("My Bookings");
                break;
            case R.id.my_req_btn:
                /* final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    my_req_btn.setBackgroundDrawable(ContextCompat.getDrawable(HomeActivity.this, R.drawable.white_rounded_btn));
                } else {
                    my_req_btn.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.white_rounded_btn));
                }*/
                loadFragment(new MyRequestFragment(), "MRF");
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                toolbar_txt.setText("My Requests");
                break;
            case R.id.logout:
                new ProjectUtils().showPopDialog(HomeActivity.this, getResources().getString(R.string.logout_msg), new ProjectUtils.onDialogClickListener() {
                    @Override
                    public void dialogClick(boolean click) {
                        //Toast.makeText(HomeActivity.this, "yes click by Home Activity", Toast.LENGTH_SHORT).show();
                        ProjectUtils.hideDialog();
                        prefrence.clearAllPreferences();
                        Intent intent = new Intent(HomeActivity.this, SignInActivityForUser.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                break;
            case R.id.near_by_tech_btn:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                navItemIndex = 1;
                chatTab();
                toolbar_txt.setText("Chat");
                open_chat_fragment("0");
        }

    }

    private void open_chat_fragment(String s) {
        try {
            ChatFragment fragment = ChatFragment.newInstance(s);
            if (fragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, fragment, "CF");
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void homeTab() {
        ic_home.setImageResource(R.drawable.ic_homecolor);
        txt_home.setTextColor(getResources().getColor(R.color.colorPrimary));
        txt_chat.setTextColor(getResources().getColor(R.color.black));
        txt_setting.setTextColor(getResources().getColor(R.color.black));
        txt_booking.setTextColor(getResources().getColor(R.color.black));
        ic_setting.setImageResource(R.drawable.ic_sett);
        ic_chat.setImageResource(R.drawable.ic_chat);
        ic_booking.setImageResource(R.drawable.ic_booking);
    }

    private void chatTab() {
        ic_home.setImageResource(R.drawable.ic_home);
        ic_setting.setImageResource(R.drawable.ic_sett);
        ic_chat.setImageResource(R.drawable.ic_chatcolor);
        ic_booking.setImageResource(R.drawable.ic_booking);
        txt_home.setTextColor(getResources().getColor(R.color.black));
        txt_chat.setTextColor(getResources().getColor(R.color.colorPrimary));
        txt_setting.setTextColor(getResources().getColor(R.color.black));
        txt_booking.setTextColor(getResources().getColor(R.color.black));
    }

    private void settingTab() {
        ic_home.setImageResource(R.drawable.ic_home);
        ic_setting.setImageResource(R.drawable.ic_settingscolor);
        ic_chat.setImageResource(R.drawable.ic_chat);
        ic_booking.setImageResource(R.drawable.ic_booking);
        txt_home.setTextColor(getResources().getColor(R.color.black));
        txt_chat.setTextColor(getResources().getColor(R.color.black));
        txt_setting.setTextColor(getResources().getColor(R.color.colorPrimary));
        txt_booking.setTextColor(getResources().getColor(R.color.black));
    }

    private void bookingTab() {
        ic_home.setImageResource(R.drawable.ic_home);
        ic_setting.setImageResource(R.drawable.ic_sett);
        ic_chat.setImageResource(R.drawable.ic_chat);
        ic_booking.setImageResource(R.drawable.ic_bookingcolor);
        txt_home.setTextColor(getResources().getColor(R.color.black));
        txt_chat.setTextColor(getResources().getColor(R.color.black));
        txt_setting.setTextColor(getResources().getColor(R.color.black));
        txt_booking.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void loadFragment(Fragment fragment, String f_Tag) {
        if (fragment != null) {
            Bundle b = new Bundle();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, fragment, f_Tag);
            transaction.commit();
        }
    }

    public void changeIndex(int i) {
        navItemIndex = i;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                toolbar_txt.setText("Home");
                homeTab();
                loadFragment(new HomeFragment(HomeActivity.this), "HF");
                return;
            }

        }
        exitApp();
    }

    public void exitApp() {
        new ProjectUtils().showPopDialog(HomeActivity.this, getResources().getString(R.string.closeMsg), new ProjectUtils.onDialogClickListener() {
            @Override
            public void dialogClick(boolean click) {
                //Toast.makeText(HomeActivity.this, "yes click by Home Activity", Toast.LENGTH_SHORT).show();
                ProjectUtils.hideDialog();
                prefrence1.setBooleanValue(Consts.LOGIN_AS_LOCK_SMITH, false);
                prefrence.setBooleanValue(Consts.LOGIN_AS_USER, true);
                Intent i = new Intent();
                i.setAction(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     * **********************************************************************************************
     * Here is code that will GET Runtime Permission.
     ************************************************************************************************
     */
    private void checkPermissions() {
        Dexter.withContext(HomeActivity.this)
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
                            new AlertDialog.Builder(HomeActivity.this)
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
                .addOnSuccessListener(HomeActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
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
                .addOnFailureListener(HomeActivity.this, new OnFailureListener() {
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
                                    rae.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                //Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
            prefrence.setValue(Consts.LATITUDE, mCurrentLocation.getLatitude() + "");
            prefrence.setValue(Consts.LONGITUDE, mCurrentLocation.getLongitude() + "");
            parms.put(Consts.USER_ID, userDTO.getUser_id());
            parms.put(Consts.ROLE, "2");
            parms.put(Consts.LATITUDE, mCurrentLocation.getLatitude() + "");
            parms.put(Consts.LONGITUDE, mCurrentLocation.getLongitude() + "");
            //updateLocation();
        }
    }

    public void updateLocation() {
        // ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.UPDATE_LOCATION_API, parms, HomeActivity.this).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    Log.e(TAG, "Location update response = " + response.toString());
                } else {
                    ProjectUtils.showToast(HomeActivity.this, msg);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                        alertDialog = new AlertDialog.Builder(this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage(getResources().getString(R.string.gps))
                                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkPermissions();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        break;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates) {
            checkPermissions();
        }
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "Location Update stop!");
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Log.e(TAG, "Location Update Destroy Called");
        }
    }
}
