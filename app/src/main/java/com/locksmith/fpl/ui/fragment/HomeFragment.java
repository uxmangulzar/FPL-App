package com.locksmith.fpl.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.locksmith.fpl.DTO.CategoryDTO;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.MySingleton;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.R;
import com.locksmith.fpl.ui.activity.HomeActivity;
import com.locksmith.fpl.ui.adapter.CategoriesAdapter;
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
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context context;
    BottomSheetDialog bottomSheetDialog;
    @BindView(R.id.main_cat_title)
    TextView main_cat_title;
    @BindView(R.id.no_data_txt)
    TextView no_data_txt;

    @BindView(R.id.residentail)
    CardView residential;
    @BindView(R.id.commercail)
    CardView commercial;
    @BindView(R.id.auto_motive)
    CardView automotive;
    @BindView(R.id.cat_Recycler)
    RecyclerView cat_Recycler;
    @BindView(R.id.residential_img)
    ImageView resi_img;
    @BindView(R.id.commercail_img)
    ImageView auto_img;
    @BindView(R.id.auto_motive_img)
    ImageView com_img;
    @BindView(R.id.searchView)
    EditText searchView;
    @BindView(R.id.cat_progress_bar)
    ProgressBar cat_progress_bar;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    HashMap<String, String> params = new HashMap<>();
    private LayoutInflater myInflater;
    private List<CategoryDTO> categoryDTOS;
    private HashMap<String, String> paramsCategory;
    CategoriesAdapter categoriesAdapter;
    //** Flag for Category Click **//
    boolean resi_flag = true, com_flag = false, auto_flag = false;

    public HomeFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        categoryDTOS = new ArrayList<>();
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        params.put(Consts.USER_ID, userDTO.getUser_id());
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        cat_Recycler.setLayoutManager(mLayoutManager);
        Glide.with(getActivity()).load(R.drawable.resi).placeholder(R.drawable.imagethumbnail).into(resi_img);
        Glide.with(getActivity()).load(R.drawable.com).placeholder(R.drawable.imagethumbnail).into(com_img);
        Glide.with(getActivity()).load(R.drawable.auto).placeholder(R.drawable.imagethumbnail).into(auto_img);
        /// This is Residential call because first appear this in HomeFragment
        callCategory("2");
        ///***** EditText method called every time change text ****////
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    if (categoriesAdapter != null) {
                        categoriesAdapter.getFilter().filter(editable);
                    }
                    if (HomeActivity.getInstance() != null) {
                        HomeActivity.getInstance().changeIndex(0);
                    }
                } else {
                    if (categoriesAdapter != null) {
                        categoriesAdapter.getFilter().filter(editable);
                    }
                    if (HomeActivity.getInstance() != null) {
                        HomeActivity.getInstance().changeIndex(1);
                    }
                }
            }
        });
        return view;
    }

    @OnClick({R.id.residentail, R.id.commercail, R.id.auto_motive})
    public void submit(View view) {
        switch (view.getId()) {
            case R.id.residentail:
                if (resi_flag) {
                } else {
                    resi_flag = true;
                    com_flag = false;
                    auto_flag = false;
                    main_cat_title.setText("Residential");
                    cat_Recycler.setVisibility(View.GONE);
                    no_data_txt.setVisibility(View.GONE);
                    cat_progress_bar.setVisibility(View.VISIBLE);
                    callCategory("2");
                }
                break;
            case R.id.commercail:
                if (com_flag) {
                } else {
                    resi_flag = false;
                    com_flag = true;
                    auto_flag = false;
                    main_cat_title.setText("Commercial");
                    cat_Recycler.setVisibility(View.GONE);
                    no_data_txt.setVisibility(View.GONE);
                    cat_progress_bar.setVisibility(View.VISIBLE);
                    callCategory("1");
                }
                break;
            case R.id.auto_motive:
                if (auto_flag) {
                } else {
                    resi_flag = false;
                    com_flag = false;
                    auto_flag = true;
                    main_cat_title.setText("Automotive");
                    cat_Recycler.setVisibility(View.GONE);
                    no_data_txt.setVisibility(View.GONE);
                    cat_progress_bar.setVisibility(View.VISIBLE);
                    callCategory("3");
                }
                break;
        }
    }

    private void callCategory(String type) {
        paramsCategory = new HashMap<>();
        paramsCategory.put(Consts.USER_ID, userDTO.getUser_id());
        paramsCategory.put(Consts.TYPE, type);
        Log.e("paramsCategory", "" + paramsCategory.toString());
        getCategory(paramsCategory);
        //category_api_call(type);

    }

    ///*** Using AndroidNetworking Library to Get Category
    public void getCategory(HashMap<String, String> paramsCategory) {

        new HttpsRequest(Consts.GET_ALL_CATEGORY_API, paramsCategory, getActivity()).stringPost("MMMM", new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                categoryDTOS.clear();
                if (flag) {
                    cat_progress_bar.setVisibility(View.GONE);
                    cat_Recycler.setVisibility(View.VISIBLE);
                    no_data_txt.setVisibility(View.GONE);
                    try {
                        Log.e("@Cat_Res@", "Response = " + response.toString());
                        Type getpetDTO = new TypeToken<List<CategoryDTO>>() {
                        }.getType();
                        categoryDTOS = (List<CategoryDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                        categoriesAdapter = new CategoriesAdapter(getActivity(), categoryDTOS, no_data_txt);
                        cat_Recycler.setAdapter(categoriesAdapter);
                        categoriesAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("errorwegetting", e.getMessage());
                    }
                } else {
                    cat_progress_bar.setVisibility(View.GONE);
                    cat_Recycler.setVisibility(View.GONE);
                    no_data_txt.setVisibility(View.VISIBLE);
                    ProjectUtils.showToast(getActivity(), msg);
                }
            }
        });
    }

    ///*** Using Volley Get Category
    public void category_api_call(String type) {
        String CATEGORY_URL = Consts.BASE_URL + Consts.GET_ALL_CATEGORY_API;
        Log.e("@CATEGORY_URL@", CATEGORY_URL);
        StringRequest newsSubCategory = new StringRequest(Request.Method.POST, CATEGORY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("@@CATEGORY DATA@@", response.toString());
                categoryDTOS.clear();
                if (response.length() > 0) {
                    cat_progress_bar.setVisibility(View.GONE);
                    try {
                        JSONObject result = new JSONObject(response);
                        String STATUS = result.getString("status");
                        String message = result.getString("message");
                        if (STATUS.equalsIgnoreCase("1")) {
                            cat_Recycler.setVisibility(View.VISIBLE);
                            no_data_txt.setVisibility(View.GONE);
                            try {
                                JSONArray jsonArray = result.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject subCategory = jsonArray.getJSONObject(i);
                                        String id = subCategory.getString("id");
                                        String cat_name = subCategory.getString("cat_name");
                                        String price = subCategory.getString("price");
                                        String type = subCategory.getString("type");
                                        String created_at = subCategory.getString("created_at");
                                        String updated_at = subCategory.getString("updated_at");
                                        String status = subCategory.getString("status");
                                        categoryDTOS.add(new CategoryDTO(id, cat_name, price, created_at, updated_at, status, type));
                                    }
                                    categoriesAdapter = new CategoriesAdapter(getActivity(), categoryDTOS, no_data_txt);
                                    cat_Recycler.setAdapter(categoriesAdapter);
                                    categoriesAdapter.notifyDataSetChanged();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (STATUS.equalsIgnoreCase("0")) {
                            cat_Recycler.setVisibility(View.GONE);
                            no_data_txt.setVisibility(View.VISIBLE);
                            Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (getActivity() == null) {
                    return;
                }
                if (NetworkManager.isConnectToInternet(getContext())) {
                    error.printStackTrace();
                    cat_progress_bar.setVisibility(View.GONE);
                    //Toast.makeText(getContext(), getResources().getString(R.string.str_server_res), Toast.LENGTH_SHORT).show();
                } else {
                    cat_progress_bar.setVisibility(View.GONE);
                    // Toast.makeText(getContext(), getResources().getString(R.string.str_connection), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Consts.USER_ID, userDTO.getUser_id());
                params.put(Consts.TYPE, type);
                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(newsSubCategory);

    }


}