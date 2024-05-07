package com.locksmith.fpl.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.DirectionsJSONParser;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.fragment.BottomSheetFragment;
import com.locksmith.fpl.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    RelativeLayout map_tech_detail;
    View mapView;
    private UserDTO userDTO;
    private SharedPrefrence prefrence;
    @BindView(R.id.cancel_dialog)
    RelativeLayout cancel_dialog;
    @BindView(R.id.tech_name)
    TextView tech_name;
    @BindView(R.id.tech_time)
    TextView tech_time;
    @BindView(R.id.tech_full_name)
    TextView tech_full_name;
    @BindView(R.id.tech_price)
    TextView tech_price;
    @BindView(R.id.current_loc)
    TextView current_loc_address;
    @BindView(R.id.destination_loc)
    TextView destination_loc_address;
    @BindView(R.id.tech_img)
    CircleImageView tech_img;
    @BindView(R.id.chat_btn)
    RelativeLayout chat_btn;
    private String job_id, artist_id, live_lat, live_long, artist_name, artist_address, artist_image, artist_price, artist_rating;
    Bundle args;
    ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        try {
            ////**** Get Artist Data START ****/////
            args = getIntent().getExtras();
            if (args != null) {
                artist_id = args.getString(Consts.ARTIST_ID, "");
                live_lat = args.getString(Consts.LATITUDE, "");
                live_long = args.getString(Consts.LONGITUDE, "");
                artist_name = args.getString(Consts.NAME, "");
                artist_address = args.getString(Consts.ADDRESS, "");
                job_id = args.getString(Consts.JOB_ID, "");
                artist_price = args.getString(Consts.PRICE, "");
                artist_image = args.getString(Consts.IMAGE, "");
                artist_rating = args.getString(Consts.RATING, "");
                tech_name.setText("" + artist_name);
                tech_price.setText("" + artist_price);
                tech_full_name.setText("" + artist_name);
                current_loc_address.setText("" + artist_address);
                Glide.with(MapsActivity.this).
                        load(artist_image)
                        .placeholder(R.drawable.dummyuser_image)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(tech_img);
                Log.d("TAG", "artist id = " + artist_id);
                Log.d("TAG", "live lat = : " + live_lat);
                Log.d("TAG", "live long = : " + live_long);
                Log.d("TAG", "artist_name = : " + artist_name);
                Log.d("TAG", "artist_address = : " + artist_address);
                Log.d("TAG", "Job ID = : " + job_id);
                Log.d("TAG", "artist_price = : " + artist_price);
                Log.d("TAG", "artist_image = : " + artist_image);
                Log.d("TAG", "artist_rating = : " + artist_rating);
            }
        } catch (Exception e) {
            Log.e("TAG", "" + e.getMessage());
        }
        ////**** Get Artist Data END****/////////
        ////****SharedPreference Data GET ****/////////
        prefrence = SharedPrefrence.getInstance(MapsActivity.this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        /** Get Addresses from LAT LONG  ***/
        geoCodedAddress();
        /** Google Map Initialization ***/
        initMap();
        map_tech_detail = findViewById(R.id.map_tech_detail);
        /** Cancel the service ***/
        cancel_dialog = findViewById(R.id.cancel_dialog);
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map_tech_detail.setVisibility(View.GONE);
                BottomSheetFragment bottomSheetDialog = BottomSheetFragment.newInstance();
                bottomSheetDialog.show(getSupportFragmentManager(), "BottomSheetDialogFragment");
            }
        });
    }

    ////**** Google Map Setup ****/////////
    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        if (markerPoints.size() > 1) {
            markerPoints.clear();
            mMap.clear();
        }
        //mMap.setMyLocationEnabled(true);
       /* LatLng live_loc_user = new LatLng(Double.parseDouble(prefrence.getValue(Consts.LATITUDE)), Double.parseDouble(prefrence.getValue(Consts.LONGITUDE)));
        mMap.addMarker(new MarkerOptions().position(live_loc_user).title(userDTO.getName()));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(live_loc_user).zoom(17).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(live_loc_user));*/
        setupDirectionLine();
//        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
//        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
//        // position on right bottom
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        rlp.setMargins(0, 0, 30, 100);
    }

    private void setupDirectionLine() {
        LatLng live_loc_user = new LatLng(Double.parseDouble(prefrence.getValue(Consts.LATITUDE)), Double.parseDouble(prefrence.getValue(Consts.LONGITUDE)));
//        LatLng live_loc_tech = new LatLng(Double.parseDouble(live_lat), Double.parseDouble(live_long)); // ending point (LatLng)
        //zoom/move cam on map ready
        LatLng destination = new LatLng(31.557797, 73.112122);
        markerPoints.add(live_loc_user);
        markerPoints.add(destination);
        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
        MarkerOptions options2 = new MarkerOptions();
        // Setting the position of the marker
        options.position(live_loc_user);
        options2.position(destination);
        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        //options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //options2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //marker.icon(BitmapDescriptorFactory.fromBitmap(bmp));
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.combine));
        options2.icon(BitmapDescriptorFactory.fromResource(R.drawable.loc));
        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);
        mMap.addMarker(options2);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(live_loc_user)
                .include(destination).build();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 500, 0));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(live_loc_user, 11));
        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);
            DownloadTask downloadTask = new DownloadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }

    }

    public void showTechLayout() {
        map_tech_detail.setVisibility(View.VISIBLE);
    }

    /**
     * For Direction URL Make Method
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Setup Google Map Key
        String key = "key=" + getResources().getString(R.string.google_map_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No direction found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(16);
                lineOptions.color(getColor(R.color.colorAccent));
            }
            Log.e("TAG", "Distance: " + distance + "Duration: " + duration);
            //tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);
            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    public void geoCodedAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(prefrence.getValue(Consts.LATITUDE)), Double.parseDouble(prefrence.getValue(Consts.LONGITUDE)), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            if (address != null) {
                destination_loc_address.setText("" + address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}