package com.locksmith.fpl.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.locksmith.fpl.DTO.AllAtristListDTO;
import com.locksmith.fpl.DTO.CategoryDTO;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.MySingleton;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.activity.MapsActivity;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> implements Filterable {

    Context mContext;
    Activity activity;
    private List<CategoryDTO> categoryDTOArrayList;
    private List<CategoryDTO> objects;
    private BottomSheetDialog bottomSheetDialog;
    private SharedPrefrence prefrence;
    Map<String, String> get_artist_params = new HashMap<>();
    private UserDTO userDTO;
    private String cat_id;
    View itemView;
    public String s;
    TextView no_data_found_txt_view_id;
    private ArrayList<AllAtristListDTO> nearByallAtristList;
    HashMap<String, String> parms = new HashMap<>();
    SimpleDateFormat timeZone;
    private Date date;

    public CategoriesAdapter(Context mContext, List<CategoryDTO> categoryDTOArrayList, TextView no_data_found_txt_view_id) {
        this.mContext = mContext;
        this.categoryDTOArrayList = categoryDTOArrayList;
        this.activity = (Activity) mContext;
        this.objects = categoryDTOArrayList;
        this.no_data_found_txt_view_id = no_data_found_txt_view_id;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        date = new Date();
        timeZone = new SimpleDateFormat(Consts.DATE_FORMATE_SERVER, Locale.ENGLISH);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.cat_list_design, parent, false);
        //userDTO = prefrence.getParentUser(Consts.USER_DTO);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        try {
            holder.cat_title.setText(categoryDTOArrayList.get(position).getCat_name().toString());
            holder.cat_price.setText("$" + categoryDTOArrayList.get(position).getPrice());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("cat_bind_holder", e.getMessage());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cat_id = categoryDTOArrayList.get(position).getId();
                Log.d("cat_id", cat_id);
                prefrence.setfragmentval("catfrag", "10");
                ServiceQtyDialog qtyDialog = new ServiceQtyDialog();
                qtyDialog.bottomSheetDialog(cat_id);
                //qtyDialog.showDialog((Activity) mContext,categoryDTOArrayList.get(position).getCat_name(),categoryDTOArrayList.get(position).getPrice());

            }
        });
         /*
         ////***** This logic code for last item of recyclerview that behind the
          BottomApp Bar to show Upper side of the bottom Bar *****///
        if (position == getItemCount() - 1) {
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) holder.lastItemCard.getLayoutParams();
            params1.setMargins(0, 0, 0, 100);
            holder.lastItemCard.setLayoutParams(params1);
            //Toast.makeText(context, "Last Item", Toast.LENGTH_SHORT).show();
        } else {
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) holder.lastItemCard.getLayoutParams();
            params1.setMargins(0, 0, 0, 0);
            holder.lastItemCard.setLayoutParams(params1);
        }

    }
    //////////////////////////////////////////// Show Qty Dialog /////////////////////////////////////////////////////////

    /**********************************************************************************/
    /*....For Pop Messages Dialog Code Here....*/

    /**********************************************************************************/

    ////////////***********For Catalogue Dialog Code Start***********/////////////
    public class ServiceQtyDialog {
        int qty = 1;

        public void bottomSheetDialog(String cat_id) {
            bottomSheetDialog = new BottomSheetDialog(mContext);
            View view = LayoutInflater.from(mContext).inflate(R.layout.quantity_selection_layout, null);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
            RelativeLayout get_service_btn = view.findViewById(R.id.get_service_btn);
            RelativeLayout cancel_dialog = view.findViewById(R.id.cancel_dialog);
            TextView s_qty = view.findViewById(R.id.s_qty);
            RelativeLayout s_qty_add = view.findViewById(R.id.s_qty_add);
            RelativeLayout s_qty_minus = view.findViewById(R.id.s_qty_minus);
            s_qty_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qty = qty + 1;
                    s_qty.setText("" + qty);
                }
            });
            s_qty_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (qty == 1) {
                        s_qty.setText("1");
                    } else {
                        qty = qty - 1;
                        s_qty.setText("" + qty);
                    }
                }
            });
            cancel_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });
            get_service_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mContext.startActivity(new Intent(mContext,MapsActivity.class));
                    get_artist_params.put(Consts.USER_ID, userDTO.getUser_id());
                    //** This is for testing to set category id
                    get_artist_params.put(Consts.CATEGORY_ID, "66");
                    //** Replace this when dynamic set category id
                    //get_artist_params.put(Consts.CATEGORY_ID, cat_id);
                    get_artist_params.put(Consts.LATITUDE, prefrence.getValue(Consts.LATITUDE));
                    get_artist_params.put(Consts.LONGITUDE, prefrence.getValue(Consts.LONGITUDE));
                    get_artist_params.put(Consts.PAGE, "1");
                    getNearByArtist();
                }
            });
        }
    }

    private void getNearByArtist() {
        ProjectUtils.showProgressDialog(mContext, true, mContext.getResources().getString(R.string.please_wait));
        String NEARBY_ARTIST_URL = Consts.BASE_URL + Consts.GET_NEARBY_ARTIST;
        Log.e("@NEARBY_ARTIST_URL@", NEARBY_ARTIST_URL);
        StringRequest nearby_artist_req = new StringRequest(Request.Method.POST, NEARBY_ARTIST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("@NEARBY_ARTIST DATA@", response.toString());
                if (response.length() > 0) {
                    ProjectUtils.pauseProgressDialog();
                    try {
                        JSONObject result = new JSONObject(response);
                        String STATUS = result.getString("status");
                        String MESSAGE = result.getString("message");
                        if (STATUS.equalsIgnoreCase("1")) {
                            nearByallAtristList = new ArrayList<>();
                            // As pointed out by ZouZou, you can save an iteration by assuming the first index is the smallest
                            Type getpetDTO = new TypeToken<List<AllAtristListDTO>>() {
                            }.getType();
                            nearByallAtristList = (ArrayList<AllAtristListDTO>) new Gson().fromJson(result.getJSONArray("myList").toString(), getpetDTO);
                            if (nearByallAtristList.size() > 0) {
                                if (nearByallAtristList.size() == 1) {
                                    Log.e("@TAG", "Only 1 technician available so this is auto select");
                                    AllAtristListDTO closest_tech = nearByallAtristList.get(0);
                                    Log.e("@TAG", "Closest technician " + closest_tech.toString());
                                    bookArtist(closest_tech);
                                } else if (nearByallAtristList.size() > 1) {
                                    Log.e("@TAG", "Multiple technician available so fine the closest start here");
                                    // Keeps a running count of the smallest value so far
                                    double minVal = Double.parseDouble(nearByallAtristList.get(0).getDistance());
                                    int minIdx = 0; // Will store the index of minVal
                                    for (int idx = 1; idx < nearByallAtristList.size(); idx++) {
                                        if (Double.parseDouble(nearByallAtristList.get(idx).getDistance()) < minVal) {
                                            minVal = Double.parseDouble(nearByallAtristList.get(idx).getDistance());
                                            minIdx = idx;
                                        }
                                    }
                                    Log.e("@TAG", "Minimum index is " + minIdx);
                                    Log.e("@TAG", "Minimum distance value " + minVal);
                                    // Technician that are too closed to user
                                    AllAtristListDTO closest_tech = nearByallAtristList.get(minIdx);
                                    Log.e("@TAG", "Closest technician " + closest_tech.toString());
                                    bookArtist(closest_tech);
                                }
                            } else {
                                Toast.makeText(mContext, "No Technician available", Toast.LENGTH_SHORT).show();
                                Log.e("@TAG", "No Tech Available for this Service");
                            }
                        } else if (STATUS.equalsIgnoreCase("0")) {
                            ProjectUtils.pauseProgressDialog();
                            Toast.makeText(mContext, "No Technician available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext == null) {
                    return;
                }
                if (NetworkManager.isConnectToInternet(mContext)) {
                    error.printStackTrace();
                    ProjectUtils.pauseProgressDialog();
                    //Toast.makeText(mContext, getResources().getString(R.string.str_server_res), Toast.LENGTH_SHORT).show();
                } else {
                    ProjectUtils.pauseProgressDialog();
                    // Toast.makeText(mContext, getResources().getString(R.string.str_connection), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> get_artist_params = new HashMap<>();
                get_artist_params.put(Consts.USER_ID, userDTO.getUser_id());
                //** This is for testing to set category id
                //get_artist_params.put(Consts.CATEGORY_ID, "66");
                //** Replace this when dynamic set category id
                get_artist_params.put(Consts.CATEGORY_ID, cat_id);
                get_artist_params.put(Consts.LATITUDE, "31.42131749");
                get_artist_params.put(Consts.LONGITUDE, "74.35908979");
                get_artist_params.put(Consts.PAGE, "1");
                Log.e("@TAG", "getParams: " + get_artist_params);
                return get_artist_params;
            }
        };
        nearby_artist_req.setRetryPolicy(new DefaultRetryPolicy(
                0,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(mContext).addToRequestQueue(nearby_artist_req);
    }

    private void bookArtist(AllAtristListDTO closest_tech) {
        Log.e("TAG", "bookArtist: method called....");
        parms.put("service_id", closest_tech.getCategory_id());
        parms.put(Consts.CATEGORY_ID, closest_tech.getCategory_id());
        Log.d("TAG", "category_id = " + closest_tech.getCategory_id());
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        Log.d("TAG", "user_id = " + userDTO.getUser_id());
        parms.put(Consts.ARTIST_ID, closest_tech.getUser_id());
        Log.d("TAG", "artist_id = " + closest_tech.getUser_id());
        parms.put(Consts.LATITUDE, prefrence.getValue(Consts.LATITUDE));
        Log.d("TAG", "lat = " + prefrence.getValue(Consts.LATITUDE));
        parms.put(Consts.LONGITUDE, prefrence.getValue(Consts.LONGITUDE));
        Log.d("TAG", "long = " + prefrence.getValue(Consts.LONGITUDE));
        parms.put(Consts.ADDRESS, closest_tech.getLocation());
        Log.d("TAG", "address = " + closest_tech.getLocation());
        parms.put(Consts.TIMEZONE, timeZone.format(date));
        Log.d("TAG", "TIME_ZONE = " + timeZone.format(date));
        Log.e("TAG", "sending params in detail = " + parms.toString());
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(mContext.getResources().getString(R.string.book_artist))
                    .setMessage(mContext.getResources().getString(R.string.book_msg) + " " + closest_tech.getName() + "?")
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog_book = dialog;
                            //bookArtist();
                            ProjectUtils.showProgressDialog(mContext, true, mContext.getResources().getString(R.string.please_wait));
                            new HttpsRequest(Consts.BOOK_ARTIST_API, parms, mContext).stringPost("Service API Call", new Helper() {
                                @Override
                                public void backResponse(boolean flag, String msg, JSONObject response) {
                                    ProjectUtils.pauseProgressDialog();
                                    if (bottomSheetDialog != null) {
                                        bottomSheetDialog.dismiss();
                                        bottomSheetDialog.cancel();
                                    }
                                    if (flag) {
                                        ProjectUtils.showToast(mContext, msg);
                                        Intent mapIntent = new Intent(mContext, MapsActivity.class);
                                        mapIntent.putExtra(Consts.ARTIST_ID, closest_tech.getUser_id());
                                        mapIntent.putExtra(Consts.LATITUDE, closest_tech.getLive_lat());
                                        mapIntent.putExtra(Consts.LONGITUDE, closest_tech.getLive_long());
                                        mapIntent.putExtra(Consts.ADDRESS, closest_tech.getLocation());
                                        mapIntent.putExtra(Consts.NAME, closest_tech.getName());
                                        mapIntent.putExtra(Consts.JOB_ID, closest_tech.getId());
                                        mapIntent.putExtra(Consts.PRICE, closest_tech.getPrice());
                                        mapIntent.putExtra(Consts.RATING, closest_tech.getAva_rating());
                                        mapIntent.putExtra(Consts.IMAGE, closest_tech.getImage());
                                        mContext.startActivity(mapIntent);
                                    } else {
                                        ProjectUtils.showToast(mContext, msg);
                                    }

                                }
                            });
                        }
                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return categoryDTOArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView cat_title;
        public TextView cat_price;
        public CardView lastItemCard;

        public MyViewHolder(View view) {
            super(view);
            cat_title = view.findViewById(R.id.cat_title);
            cat_price = view.findViewById(R.id.cat_price_txt);
            lastItemCard = view.findViewById(R.id.lastItemCard);

        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        categoryDTOArrayList.clear();
        if (charText.length() == 0) {
            categoryDTOArrayList.addAll(objects);
        } else {
            for (CategoryDTO appliedJobDTO : objects) {
                if (appliedJobDTO.getCat_name().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    categoryDTOArrayList.add(appliedJobDTO);
                }
            }
        }
        notifyDataSetChanged();
    }

    private Filter fRecords;

    @Override
    public Filter getFilter() {
        if (fRecords == null) {
            fRecords = new RecordFilter();
        }
        return fRecords;
    }

    private class RecordFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence search) {
            s = search.toString();
            FilterResults results = new FilterResults();
            if (s.length() == 0) {
                categoryDTOArrayList = objects;
            } else {
                final List<CategoryDTO> fRecords = new ArrayList<>();
                for (CategoryDTO coupon : objects) {
                    ////****First Method For Matching the Exact strings ****////
                    ////*** I prefer to use Build in Method ***////
                    if ((coupon.getCat_name().toLowerCase().startsWith(s.toLowerCase()))) {
                        fRecords.add(coupon);
                    }
                }
                categoryDTOArrayList = fRecords;
            }

            results.values = categoryDTOArrayList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            categoryDTOArrayList = (List<CategoryDTO>) results.values;
            Log.e("TAG", "publishResults: " + categoryDTOArrayList.toString());
            if (categoryDTOArrayList.size() <= 0) {
                no_data_found_txt_view_id.setVisibility(View.VISIBLE);
            } else {
                no_data_found_txt_view_id.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        }
    }

}
