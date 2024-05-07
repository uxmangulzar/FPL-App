package com.locksmith.fpl.Dentist.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.locksmith.fpl.Dentist.DTO.UserDTO1;
import com.locksmith.fpl.Dentist.https.HttpsRequest1;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.interfacess.Helper1;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.R;

import com.locksmith.fpl.ui.adapter.ChooseTopicAdapter;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateStoreActivity extends AppCompatActivity {
    String[] residential = {"Access Control System", "CCTV", "Intercom System", "Other"};
    String[] automotive = {"Car Key Making", "Car Key Programming", "Other"};
    String[] commercial = {"Audit Trail System", "Biometric Fingerprint Systems", "CCTV", "Electric Strikes", "Intercom System,", "Proximity Readers",
            "Magnetic Locks", "Other"};
    JSONArray jsonVPArray = new JSONArray();
    int gallery_permission_code = 4, REQUEST_IMAGE_SELECT = 2;
    Uri imgUri;
    boolean isSelected = false;
    private Bitmap bitmap;
    private String currentPhotoPath = "";
    private HashMap<String, String> params;
    private HashMap<String, File> paramsFile = new HashMap<>();
    File file;
    private UserDTO1 userDTO1;
    private SharedPrefrence1 prefrence;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AutoCompleteTextView serviceName;
    RelativeLayout type_installation_1, type_installation_0, upload_img_rel, type_programming_1, type_programming_0, submitService, btn_yes, btn_no;
    EditText productName, price, description, etYears, et_qty, et_unit_p, et_total_p;
    String category, s_type = "";
    ArrayList<String> type = new ArrayList<>();
    LinearLayout ll_volume_price, vp_layout;
    ImageButton img_back;
    BottomSheet.Builder builder;
    CardView select_topic;
    BottomSheetDialog bottomSheetDialog;
    ChooseTopicAdapter chooseTopicAdapter;
    TextView at_topic_txt, tvUploadImg;
    ArrayList<String> topic_list = new ArrayList<>();
    private String TAG = CreateStoreActivity.class.getSimpleName();
    private Context mContext;
    private static CreateStoreActivity instance = null;
    boolean add_vp = false;

    public static CreateStoreActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);
        instance = this;
        setUiAction();

    }

    public void setUiAction() {
        sharedPreferences = getSharedPreferences("AppInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        prefrence = SharedPrefrence1.getInstance(this);
        userDTO1 = prefrence.getParentUser(Consts1.USER_DTO);
        //TODO:ID's
        createFolder();
        serviceName = findViewById(R.id.etfServiceName);
        serviceName.setThreshold(1);
        type_installation_0 = findViewById(R.id.type_insatallation_0);
        type_programming_0 = findViewById(R.id.type_programming_0);
        type_installation_1 = findViewById(R.id.type_insatallation_1);
        type_programming_1 = findViewById(R.id.type_programming_1);
        //get_Category_List();
        productName = findViewById(R.id.etlProductName);
        description = findViewById(R.id.etDescription);
        price = findViewById(R.id.etPrice);
        upload_img_rel = findViewById(R.id.upload_img_rel);
        et_qty = findViewById(R.id.et_qty);
        et_total_p = findViewById(R.id.et_total_p);
        et_unit_p = findViewById(R.id.et_unit_p);
        submitService = findViewById(R.id.submit_service);
        btn_no = findViewById(R.id.btn_no);
        btn_yes = findViewById(R.id.btn_yes);
        ll_volume_price = findViewById(R.id.ll_volume_price);
        vp_layout = findViewById(R.id.vp_layout);
        img_back = findViewById(R.id.close_btn);
        etYears = findViewById(R.id.etYears);
        select_topic = findViewById(R.id.select_topic);
        at_topic_txt = findViewById(R.id.at_topic_txt);
        tvUploadImg = findViewById(R.id.tvUploadImg);
        topic_list.add("Residential");
        topic_list.add("Commercial");
        topic_list.add("Automotive");
        topic_list.add("Other");
        builder = new BottomSheet.Builder(CreateStoreActivity.this).sheet(R.menu.menu_cards);
        builder.title(getResources().getString(R.string.take_image));
        builder.listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.gallery_cards:
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), REQUEST_IMAGE_SELECT);
                        break;
                    case R.id.cancel_cards:
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });
                        break;
                }
            }
        });
        select_topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetDialog();
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        type_programming_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!serviceName.getText().toString().equalsIgnoreCase("")) {
                    type_programming_1.setVisibility(View.VISIBLE);
                    type_programming_0.setVisibility(View.GONE);
                    serviceName.append("+Programming");
                    type.add("Programming");
                } else {
                    Toast.makeText(CreateStoreActivity.this, "write service name first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        type_installation_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!serviceName.getText().toString().equalsIgnoreCase("")) {
                    type_installation_1.setVisibility(View.VISIBLE);
                    type_installation_0.setVisibility(View.GONE);
                    serviceName.append("+Installation");
                    type.add("Installation");
                } else {
                    Toast.makeText(CreateStoreActivity.this, "write service name first!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        type_programming_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_programming_0.setVisibility(View.VISIBLE);
                type_programming_1.setVisibility(View.GONE);
                String name = serviceName.getText().toString();
                serviceName.setText(name.replace("+Programming", ""));
                type.remove("Programming");
            }
        });
        type_installation_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_installation_0.setVisibility(View.VISIBLE);
                type_installation_1.setVisibility(View.GONE);
                String name = serviceName.getText().toString();
                serviceName.setText(name.replace("+Installation", ""));
                type.remove("Installation");
            }
        });
        //TODO: Button Clicks
        upload_img_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
        submitService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_vp = true;
                ll_volume_price.setVisibility(View.GONE);
                vp_layout.setVisibility(View.VISIBLE);
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_vp = false;
                ll_volume_price.setVisibility(View.GONE);
                vp_layout.setVisibility(View.GONE);
            }
        });

    }

    private void openBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(CreateStoreActivity.this);
        View view = LayoutInflater.from(CreateStoreActivity.this).inflate(R.layout.bottom_selection_dailog, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        TextView textView = view.findViewById(R.id.choose_topic_txt);
        textView.setText("Select Category");
        RelativeLayout cancel_dialog = view.findViewById(R.id.cancel_dialog);
        RecyclerView choose_topic_recycler = view.findViewById(R.id.choose_topic_recycler);
        LinearLayoutManager rvCouponManager = new LinearLayoutManager(CreateStoreActivity.this, LinearLayoutManager.VERTICAL, false);
        choose_topic_recycler.setLayoutManager(rvCouponManager);
        chooseTopicAdapter = new ChooseTopicAdapter(CreateStoreActivity.this, choose_topic_recycler, topic_list, at_topic_txt, "0");
        choose_topic_recycler.setAdapter(chooseTopicAdapter);
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    public void setServicesName(String text) {
        Toast.makeText(CreateStoreActivity.this, "" + text, Toast.LENGTH_SHORT).show();
        if (text.equalsIgnoreCase("Automotive")) {
            category = "2";
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (CreateStoreActivity.this, android.R.layout.select_dialog_item, automotive);
            //spinner_sub_category.getBackground().setColorFilter(Color.parseColor("#b6b6b6"), PorterDuff.Mode.SRC_ATOP);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            serviceName.setAdapter(adapter);
            changeFieldName();

        } else if (text.equalsIgnoreCase("Residential")) {
            category = "3";
            changeFieldName();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (CreateStoreActivity.this, android.R.layout.select_dialog_item, residential);
            //spinner_sub_category.getBackground().setColorFilter(Color.parseColor("#b6b6b6"), PorterDuff.Mode.SRC_ATOP);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            serviceName.setAdapter(adapter);
            //rely_sub_cat.setVisibility(View.VISIBLE);
            //tv_select_service_type.setVisibility(View.VISIBLE);

        } else if (text.equalsIgnoreCase("Commercial")) {
            category = "1";
            changeFieldName();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (CreateStoreActivity.this, android.R.layout.select_dialog_item, commercial);
            //spinner_sub_category.getBackground().setColorFilter(Color.parseColor("#b6b6b6"), PorterDuff.Mode.SRC_ATOP);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            serviceName.setAdapter(adapter);
            //rely_sub_cat.setVisibility(View.VISIBLE);
            //tv_select_service_type.setVisibility(View.VISIBLE);
        } else {
            category = "4";
                   /* rely_sub_cat.setVisibility(View.GONE);
                    tv_select_service_type.setVisibility(View.GONE);*/
            serviceName.setText("");
            changeFieldName();
        }

    }

    private void changeFieldName() {
        if (category.equalsIgnoreCase("2")) {
            productName.setHint("Car Brand");
            description.setHint("Car Model");
            etYears.setVisibility(View.VISIBLE);
        } else {
            productName.setHint("Product Name");
            description.setHint("Description");
            etYears.setVisibility(View.GONE);
        }
    }


    /*
     * **********************************************************************************************
     * Here is code that will GET Runtime Permission.
     ************************************************************************************************
     */
    private void checkPermissions() {
        Dexter.withContext(CreateStoreActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            builder.show();
                            //Toast.makeText(SignUp.this, "All permission granted", Toast.LENGTH_SHORT).show();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            new AlertDialog.Builder(CreateStoreActivity.this)
                                    .setTitle("Permission Required")
                                    .setMessage("These permission is mandatory to select pictures in this app")
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            imgUri = data.getData();
            if (uri != null && this != null) {
                currentPhotoPath = getPath(this, uri);
                file = new File(currentPhotoPath);
                paramsFile.put(Consts1.PRODUCT_IMAGE, file);
                Log.d("@imgPath", currentPhotoPath);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    isSelected = true;
                    tvUploadImg.setText(currentPhotoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                isSelected = false;
                Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getPath(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private void validateFields() {
        if (serviceName.getText().toString().isEmpty() ||
                productName.getText().toString().isEmpty() ||
                description.getText().toString().isEmpty() ||
                price.getText().toString().isEmpty() ||
                type.size() == 0) {

            if (serviceName.getText().toString().isEmpty())
                serviceName.setError("Please set service name");
            else if (productName.getText().toString().isEmpty())
                productName.setError("Please set product name");
            else if (description.getText().toString().isEmpty())
                description.setError("Please set description");
            else if (price.getText().toString().isEmpty())
                price.setError("Please set price");
            else if (type.size() == 0)
                Toast.makeText(this, "Please select a service type!", Toast.LENGTH_SHORT).show();
        } else {
            params = new HashMap<>();
            for (int i = 0; i < type.size(); i++) {
                s_type = s_type.concat(type.get(i) + " ");
            }
            String sub_cat_name = "";
            String[] splitArray = serviceName.getText().toString().split("\\+");
            for (int i = 0; i < splitArray.length; i++) {
                sub_cat_name = splitArray[0];
            }
            if (add_vp) {
                setVP();
            }
            params.put(Consts1.ARTIST_ID, userDTO1.getUser_id());
            params.put(Consts1.CATEGORY_ID, category);
            params.put(Consts1.SERVICE_NAME, serviceName.getText().toString());
            params.put(Consts1.SERVICE_TYPE, s_type.trim());
            params.put(Consts1.PRODUCT_NAME, productName.getText().toString());
            params.put(Consts1.PRODUCT_DESCRIPTION, description.getText().toString());
            params.put(Consts1.PRODUCT_PRICE, price.getText().toString());
            params.put(Consts1.SUB_CATEGORY, sub_cat_name);
            params.put(Consts1.YEARS, etYears.getText().toString());
            submitServiceApi();
        }
    }

    private void submitServiceApi() {
        ProjectUtils.showCustomProgressDialog(CreateStoreActivity.this, getResources().getString(R.string.please_wait));
        new HttpsRequest1(Consts1.ADD_SERVICE, params, paramsFile, this).imagePost("adddd", new Helper1() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.hideDialog();
                if (flag) {
                    try {
                        ProjectUtils.showToast(CreateStoreActivity.this, msg);
                        if (response != null) {
                        }
                        setResult(RESULT_OK);
                        finish();
                        //Toast.makeText(baseActivity1, "Service Added Successfully!", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ProjectUtils.showToast(CreateStoreActivity.this, msg);
                }
            }
        });
    }

    /*
     * **********************************************************************************************
     * here is functions that will create folder into Internal Storage
     ************************************************************************************************
     *
     */
    public void createFolder() {
        String dirpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LockSmithAppVolumePricing/";
        File f = new File(dirpath);
        if (!f.exists())
            if (!f.mkdir()) {
                //Toast.makeText(CreateStoreActivity.this, f.getName() + "Can't create Folder !", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(CreateStoreActivity.this, f.getName() + "Folder Created !", Toast.LENGTH_SHORT).show();
            }
        else {
            //Toast.makeText(context, f.getName() + " Folder already exits !", Toast.LENGTH_SHORT).show();
        }
        editor.apply();
    }

    private void setVP() {
        if (et_qty.getText().toString().isEmpty() || et_total_p.getText().toString().isEmpty() || et_unit_p.getText().toString().isEmpty()) {
            if (et_qty.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "missing volume pricing quantity", Toast.LENGTH_SHORT).show();
            } else if (et_unit_p.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "missing volume pricing unit price", Toast.LENGTH_SHORT).show();
            } else if (et_qty.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "missing volume pricing total price", Toast.LENGTH_SHORT).show();
            }

        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("quantity", et_qty.getText().toString());
                jsonObject.put("unit_price", et_unit_p.getText().toString());
                jsonObject.put("total_price", et_total_p.getText().toString());
                Log.e("jsonObject", "onClick: " + jsonObject);
                jsonVPArray.put(jsonObject);
                Log.e("jsonProductArray", "onClick: " + jsonVPArray.toString());
                params.put(Consts1.VOLUME_PRICING, String.valueOf(jsonVPArray));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //writeDataIntoFile(jsonVPArray.toString());
        }

    }


    /*
     * **********************************************************************************************
     * here is functions that will write data into Text File
     ************************************************************************************************
     *
     */
    public boolean writeDataIntoFile(String fileContent) {
        try {
            //--------get path of root folder---------
            String dirpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LockSmithAppVolumePricing/";
            //-------check if folder exist, if don't create it--------------
            File root = new File(dirpath);
            if (!root.exists()) {
                if (root.mkdirs()) {
                    return false;
                }
            }
            //---------Create file inside specified folder------------
            File f = new File(dirpath + "data.txt");
            if (!f.exists()) {
                //--------------write content to file----------
                if (!f.createNewFile())
                    return false;
            }
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(f);
            outputStream.write(fileContent.getBytes());
            outputStream.close();
            paramsFile.put(Consts1.VOLUME_PRICING, f);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}