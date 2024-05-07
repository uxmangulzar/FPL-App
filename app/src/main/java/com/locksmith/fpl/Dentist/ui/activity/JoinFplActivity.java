package com.locksmith.fpl.Dentist.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.locksmith.fpl.Dentist.DTO.UserDTO1;
import com.locksmith.fpl.Dentist.https.HttpsRequest1;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.interfacess.Helper1;
import com.locksmith.fpl.Dentist.preferences.SharedPrefrence1;
import com.locksmith.fpl.Dentist.ui.adapter.ChooseStateAdapter;
import com.locksmith.fpl.Dentist.ui.fragment.bottomSheetFragments.EmptyFieldFragment;
import com.locksmith.fpl.Dentist.ui.fragment.bottomSheetFragments.NewStateFragment;
import com.locksmith.fpl.R;
import com.locksmith.fpl.utils.ProjectUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;


public class JoinFplActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    EditText f_name, l_name, company_name, usa_id, license_number, upload_id, upload_license;
    CardView select_state;
    RelativeLayout submit_fpl;
    TextView at_topic_txt;
    BottomSheetDialog bottomSheetDialog;
    ChooseStateAdapter chooseStateAdapter;
    private SharedPrefrence1 prefrence;
    private UserDTO1 userDTO1;
    int gallery_permission_code = 4,
            REQUEST_IMAGE_FOR_DRIVER_LISENCE = 5, REQUEST_IMAGE_FOR_LISENCE = 6;
    Uri imgUri, imgUriForDriverLisence, imgUriForLisence;
    boolean isSelected = false, isSelectedForDriverLisence = false, isSelectedForLisence = false;
    private String currentPhotoPath = "", currentPhotoPathForDriverLisence = "", currentPhotoPathForLisence = "";
    private HashMap<String, String> paramsForReq;
    private HashMap<String, File> paramsFileForReq = new HashMap<>();
    File file, fileForDriverLisence, fileForLisence;
    private Bitmap bitmap, bitmapForDriverLisence, bitmapForLisence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_fpl);
        f_name = findViewById(R.id.fname);
        l_name = findViewById(R.id.lname);
        usa_id = findViewById(R.id.usa_id);
        at_topic_txt = findViewById(R.id.at_topic_txt);
        company_name = findViewById(R.id.company_name);
        license_number = findViewById(R.id.license_number);
        upload_id = findViewById(R.id.upload_id);
        upload_license = findViewById(R.id.upload_license);
        select_state = findViewById(R.id.select_state);
        submit_fpl = findViewById(R.id.submit_fpl);
        prefrence = SharedPrefrence1.getInstance(JoinFplActivity.this);
        userDTO1 = prefrence.getParentUser(Consts1.USER_DTO);

        f_name.setOnFocusChangeListener(this::onFocusChange);
        l_name.setOnFocusChangeListener(this::onFocusChange);
        usa_id.setOnFocusChangeListener(this::onFocusChange);
        company_name.setOnFocusChangeListener(this::onFocusChange);
        license_number.setOnFocusChangeListener(this::onFocusChange);

        upload_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageForReq(REQUEST_IMAGE_FOR_DRIVER_LISENCE);
            }
        });
        upload_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageForReq(REQUEST_IMAGE_FOR_LISENCE);
            }
        });
        submit_fpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFieldsForReq();
            }
        });
        select_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetDialog();
            }
        });

    }

    private void emptyFieldDialog() {
        EmptyFieldFragment emptyFieldFragment = EmptyFieldFragment.newInstance();
        emptyFieldFragment.show(getSupportFragmentManager(), "emptyFieldFragment");
    }

    private void newStateDialog() {
        NewStateFragment newStateFragment = NewStateFragment.newInstance();
        newStateFragment.show(getSupportFragmentManager(), "newStateFragment");
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        switch (v.getId()) {
            case R.id.fname:
                f_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_colorfull_join_fpl));
                l_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                usa_id.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                license_number.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                company_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                break;
            case R.id.lname:
                l_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_colorfull_join_fpl));
                usa_id.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                license_number.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                company_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                f_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                break;
            case R.id.usa_id:
                l_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                license_number.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                company_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                f_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                usa_id.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_colorfull_join_fpl));
                break;
            case R.id.license_number:
                l_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                company_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                f_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                usa_id.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                license_number.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_colorfull_join_fpl));
                break;
            case R.id.company_name:
                l_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                license_number.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                f_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                usa_id.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_join_fpl));
                company_name.setBackground(ContextCompat.getDrawable(JoinFplActivity.this, R.drawable.edittext_colorfull_join_fpl));
                break;

        }

    }

    private void openBottomSheetDialog() {
        String[] state_list = getResources().getStringArray(R.array.list_of_states);
        String[] available_state = getResources().getStringArray(R.array.str_available_states);
        Log.e("TAG", "openBottomSheetDialog: state lists" + state_list);
        bottomSheetDialog = new BottomSheetDialog(JoinFplActivity.this);
        View view = LayoutInflater.from(JoinFplActivity.this).inflate(R.layout.bottom_selection_dailog, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        TextView textView = view.findViewById(R.id.choose_topic_txt);
        textView.setText("Select  State");
        RelativeLayout cancel_dialog = view.findViewById(R.id.cancel_dialog);
        RecyclerView choose_topic_recycler = view.findViewById(R.id.choose_topic_recycler);
        LinearLayoutManager rvCouponManager = new LinearLayoutManager(JoinFplActivity.this, LinearLayoutManager.VERTICAL, false);
        choose_topic_recycler.setLayoutManager(rvCouponManager);
        chooseStateAdapter = new ChooseStateAdapter(JoinFplActivity.this, choose_topic_recycler, state_list, at_topic_txt, bottomSheetDialog, available_state);
        choose_topic_recycler.setAdapter(chooseStateAdapter);
        //chooseStateAdapter.notifyDataSetChanged();
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void selectImageForReq(int i) {
        int permissionCheck = ContextCompat.checkSelfPermission(JoinFplActivity.this,
                READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    JoinFplActivity.this,
                    new String[]{READ_EXTERNAL_STORAGE},
                    gallery_permission_code
            );
        } else {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_FOR_DRIVER_LISENCE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            imgUriForDriverLisence = data.getData();
            if (uri != null && this != null) {
                currentPhotoPathForDriverLisence = getPath(JoinFplActivity.this, uri);
                fileForDriverLisence = new File(currentPhotoPathForDriverLisence);
                paramsFileForReq.put(Consts1.DRIVER_LISCENCE_IMAGE, fileForDriverLisence);
                Log.d("p", currentPhotoPath);
                try {
                    bitmapForDriverLisence = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    isSelectedForDriverLisence = true;
                    upload_id.setText(currentPhotoPathForDriverLisence);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                isSelectedForDriverLisence = false;
                Toast.makeText(JoinFplActivity.this, "Image not selected", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_IMAGE_FOR_LISENCE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            imgUriForLisence = data.getData();
            if (uri != null && this != null) {
                currentPhotoPathForLisence = getPath(JoinFplActivity.this, uri);
                fileForLisence = new File(currentPhotoPathForLisence);
                paramsFileForReq.put(Consts1.LISCENCE_IMAGE, fileForLisence);
                Log.d("p", currentPhotoPath);
                try {
                    bitmapForLisence = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    isSelectedForLisence = true;
                    upload_license.setText(currentPhotoPathForLisence);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                isSelectedForLisence = false;
                Toast.makeText(JoinFplActivity.this, "Image not selected", Toast.LENGTH_SHORT).show();
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

    private void validateFieldsForReq() {
        if (f_name.getText().toString().isEmpty() ||
                l_name.getText().toString().isEmpty() ||
                usa_id.getText().toString().isEmpty() ||
                license_number.getText().toString().isEmpty()
                || !isSelectedForLisence
                || !isSelectedForDriverLisence) {

            if (f_name.getText().toString().isEmpty())
                f_name.setError("Please set first name");
            else if (l_name.getText().toString().isEmpty())
                l_name.setError("Please set last name");
            else if (license_number.getText().toString().isEmpty())
                license_number.setError("Please set licence #");
            else if (usa_id.getText().toString().isEmpty())
                usa_id.setError("Please Enter company name ");
            else if (!isSelectedForDriverLisence) {
                Toast.makeText(JoinFplActivity.this, "Upload Driving License", Toast.LENGTH_SHORT).show();
            } else if (!isSelectedForLisence) {
                Toast.makeText(JoinFplActivity.this, "Upload License", Toast.LENGTH_SHORT).show();
            }
        } else {
            paramsForReq = new HashMap<>();
            paramsForReq.put(Consts1.ARTIST_ID, userDTO1.getUser_id());
            paramsForReq.put(Consts1.FIRST_NAME, f_name.getText().toString());
            paramsForReq.put(Consts1.LAST_NAME, l_name.getText().toString());
            paramsForReq.put(Consts1.COMPANY_NAME, company_name.getText().toString() + "");
            paramsForReq.put(Consts1.LICENCE_NUMBER, license_number.getText().toString());
            paramsForReq.put(Consts1.SECURITY_NUMBER, usa_id.getText().toString());
            paramsForReq.put(Consts1.STATE, at_topic_txt.getText().toString());
            submitFormForReq();
        }
    }

    private void submitFormForReq() {
        ProjectUtils.showProgressDialog(JoinFplActivity.this, true, getResources().getString(R.string.please_wait));
        new HttpsRequest1(Consts1.JOIN_FPL, paramsForReq, paramsFileForReq, JoinFplActivity.this).imagePost("TAG", new Helper1() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        ProjectUtils.showToast(JoinFplActivity.this, msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ProjectUtils.showToast(JoinFplActivity.this, msg);
                }
            }
        });
    }
}