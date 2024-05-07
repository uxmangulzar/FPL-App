package com.locksmith.fpl.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.locksmith.fpl.DTO.CategoryDTO;
import com.locksmith.fpl.DTO.PostedJobDTO;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.adapter.ChooseTopicAdapter;
import com.locksmith.fpl.utils.ProjectUtils;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditPostJobActivity extends AppCompatActivity implements View.OnClickListener {
    CardView select_topic;
    BottomSheetDialog bottomSheetDialog;
    ChooseTopicAdapter chooseTopicAdapter;
    TextView at_topic_txt;
    ArrayList<String> topic_list = new ArrayList<>();
    private String TAG = EditPostJobActivity.class.getSimpleName();
    private Context mContext;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private ArrayList<CategoryDTO> categoryDTOS;
    private HashMap<String, String> parmsadd = new HashMap<>();
    private ImageView ivImg;
    private ImageButton ivBack;
    private EditText etCommet, etTitle, etAddress, etPrice, etQuantity;
    private RelativeLayout llPicture, llPost;
    private LinearLayout layout_image_selection;
    Uri picUri;
    int PICK_FROM_GALLERY = 2;
    int CROP_GALLERY_IMAGE = 4;
    BottomSheet.Builder builder;
    private File image;
    HashMap<String, File> parmsFile = new HashMap<>();
    private PostedJobDTO postedJobDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post_job);
        mContext = EditPostJobActivity.this;
        getSupportActionBar().hide();
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        parmsadd.put(Consts.USER_ID, userDTO.getUser_id());
        parmsadd.put(Consts.LATI, userDTO.getLive_lat());
        parmsadd.put(Consts.LONGI, userDTO.getLive_long());
        if (getIntent().hasExtra(Consts.POST_JOB_DTO)) {
            postedJobDTO = (PostedJobDTO) getIntent().getSerializableExtra(Consts.POST_JOB_DTO);
        }
        setUiAction();
    }

    public void setUiAction() {
        ivBack = findViewById(R.id.ivBack);
        etPrice = findViewById(R.id.etPrice);
        etTitle = findViewById(R.id.etTitle);
        etCommet = findViewById(R.id.etCommet);
        etAddress = findViewById(R.id.etAddress);
        etQuantity = findViewById(R.id.etQuantity);
        ivImg = findViewById(R.id.ivImg);
        llPicture = findViewById(R.id.llPicture);
        layout_image_selection = findViewById(R.id.layout_image_selection);
        llPost = findViewById(R.id.llPost);
        ivBack.setOnClickListener(this);
        etAddress.setOnClickListener(this);
        llPicture.setOnClickListener(this);
        llPost.setOnClickListener(this);
        select_topic = findViewById(R.id.select_topic);
        at_topic_txt = findViewById(R.id.at_topic_txt);
        topic_list.add("Residential");
        topic_list.add("Commercial");
        topic_list.add("Automotive");
        topic_list.add("Other");
        select_topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetDialog();
            }
        });
        builder = new BottomSheet.Builder(EditPostJobActivity.this).sheet(R.menu.menu_cards);
        builder.title(getResources().getString(R.string.take_image));
        builder.listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.gallery_cards:
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), PICK_FROM_GALLERY);
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

        if (NetworkManager.isConnectToInternet(mContext)) {
            showData();
        } else {
            ProjectUtils.showLong(mContext, getResources().getString(R.string.internet_concation));
        }

    }

    public void showData() {
        etCommet.setText(postedJobDTO.getDescription());
        etTitle.setText(postedJobDTO.getTitle());
        etAddress.setText(postedJobDTO.getAddress());
        etPrice.setText(postedJobDTO.getPrice());
        etQuantity.setText(postedJobDTO.getQuantity());
        if (postedJobDTO.getCategory_id().equalsIgnoreCase("67")) {
            at_topic_txt.setText("Commercial");
        } else if (postedJobDTO.getCategory_id().equalsIgnoreCase("66")) {
            at_topic_txt.setText("Residential");
        } else if (postedJobDTO.getCategory_id().equalsIgnoreCase("68")) {
            at_topic_txt.setText("Automotive");
        }
        Glide.with(mContext).load(postedJobDTO.getAvtar())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivImg);

    }

    /*
     * **********************************************************************************************
     * Here is code that will GET Runtime Permission.
     ************************************************************************************************
     */
    private void checkPermissions() {
        Dexter.withContext(EditPostJobActivity.this)
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
                            new AlertDialog.Builder(EditPostJobActivity.this)
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

        if (requestCode == CROP_GALLERY_IMAGE) {
            if (data != null) {
                layout_image_selection.setVisibility(View.GONE);
                ivImg.setVisibility(View.VISIBLE);
                picUri = Uri.parse(data.getExtras().getString("resultUri"));
                Log.e("image 1", picUri + "");
                Glide.with(mContext).load(picUri).placeholder(R.drawable.imagethumbnail)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivImg);
                String path = getPath(EditPostJobActivity.this, picUri);
                if (path != null) {
                    Log.e("@Picture Path@", "" + path);
                    try {
                        image = new File(path);
                        Log.e("@File Path@", "" + String.valueOf(image));
                        parmsFile.put(Consts.AVTAR, image);
                        Log.e("@File params@", "" + String.valueOf(parmsFile));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                Uri tempUri = data.getData();
                Log.e("front tempUri", "" + tempUri);
                if (tempUri != null) {
                    //    image = new File(ConvertUriToFilePath.getPathFromURI(PostJob.this, tempUri));
                    Log.e("image 2", image + "");
                    startCropping(tempUri, CROP_GALLERY_IMAGE);
                } else {

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }


    }


    public void startCropping(Uri uri, int requestCode) {

        Intent intent = new Intent(EditPostJobActivity.this, BasicActivity.class);
        intent.putExtra("imageUri", uri.toString());
        intent.putExtra("requestCode", requestCode);
        startActivityForResult(intent, requestCode);
    }

    private void openBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(EditPostJobActivity.this);
        View view = LayoutInflater.from(EditPostJobActivity.this).inflate(R.layout.bottom_selection_dailog, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        TextView textView = view.findViewById(R.id.choose_topic_txt);
        textView.setText("Select Category");
        RelativeLayout cancel_dialog = view.findViewById(R.id.cancel_dialog);
        RecyclerView choose_topic_recycler = view.findViewById(R.id.choose_topic_recycler);
        LinearLayoutManager rvCouponManager = new LinearLayoutManager(EditPostJobActivity.this, LinearLayoutManager.VERTICAL, false);
        choose_topic_recycler.setLayoutManager(rvCouponManager);
        chooseTopicAdapter = new ChooseTopicAdapter(EditPostJobActivity.this, choose_topic_recycler, topic_list, at_topic_txt,"1");
        choose_topic_recycler.setAdapter(chooseTopicAdapter);
        //chooseTopicAdapter.notifyDataSetChanged();
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llPicture:
                checkPermissions();
                break;
            case R.id.llPost:
                submitForm();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;

        }
    }

    public void submitForm() {

        if (!validateCategory()) {
            return;
        } else if (!validateTitle()) {
            return;
        } else if (!validatePrice()) {
            return;
        } else if (!validateAddress()) {
            return;
        } else if (!validateComment()) {
            return;
        } else if (!validateQuantity()) {
            return;
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                addPost();
                Toast.makeText(mContext, "Added", Toast.LENGTH_SHORT).show();

            } else {
                ProjectUtils.showLong(mContext, getResources().getString(R.string.internet_concation));
            }
        }
    }

    public boolean validateCategory() {
        if (at_topic_txt.getText().equals("ALL CATEGORIES")) {
            ProjectUtils.showToast(EditPostJobActivity.this, "Please select a category");
            return false;
        } else {
            return true;
        }
    }

    public boolean validateComment() {
        if (!ProjectUtils.isEditTextFilled(etCommet)) {
            etCommet.setError(getResources().getString(R.string.val_des));
            etCommet.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validateAddress() {
        if (!ProjectUtils.isEditTextFilled(etAddress)) {
            etAddress.setError(getResources().getString(R.string.val_address));
            etAddress.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validateTitle() {
        if (!ProjectUtils.isEditTextFilled(etTitle)) {
            etTitle.setError(getResources().getString(R.string.val_title));
            etTitle.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validatePrice() {
        if (!ProjectUtils.isEditTextFilled(etPrice)) {
            etPrice.setError(getResources().getString(R.string.val_price));
            etPrice.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validateQuantity() {
        if (!ProjectUtils.isEditTextFilled(etQuantity)) {
            etQuantity.setError(getResources().getString(R.string.val_qty));
            etQuantity.requestFocus();
            return false;
        } else if (Integer.parseInt(etQuantity.getText().toString()) <= 0) {
            etQuantity.setError(getResources().getString(R.string.val_qty_0));
            etQuantity.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    public void addPost() {
        if (at_topic_txt.getText().toString().equalsIgnoreCase("Residential")) {
            parmsadd.put(Consts.CATEGORY_ID, "66");
        } else if (at_topic_txt.getText().toString().equalsIgnoreCase("Commercial")) {
            parmsadd.put(Consts.CATEGORY_ID, "67");
        } else if (at_topic_txt.getText().toString().equalsIgnoreCase("Automotive")) {
            parmsadd.put(Consts.CATEGORY_ID, "68");
        } else if (at_topic_txt.getText().toString().equalsIgnoreCase("Others")) {
            parmsadd.put(Consts.CATEGORY_ID, "69");
        }
        parmsadd.put(Consts.JOB_ID, postedJobDTO.getJob_id());
        parmsadd.put(Consts.TITLE, ProjectUtils.getEditTextValue(etTitle));
        parmsadd.put(Consts.PRICE, ProjectUtils.getEditTextValue(etPrice));
        parmsadd.put(Consts.DESCRIPTION, ProjectUtils.getEditTextValue(etCommet));
        parmsadd.put(Consts.ADDRESS, ProjectUtils.getEditTextValue(etAddress));
        parmsadd.put(Consts.QUANTITY, ProjectUtils.getEditTextValue(etQuantity));
        ProjectUtils.showCustomProgressDialog(EditPostJobActivity.this, getResources().getString(R.string.please_wait));

        new HttpsRequest(Consts.EDIT_POST_JOB_API, parmsadd, parmsFile, mContext).imagePost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.hideDialog();
                if (flag) {
                    ProjectUtils.showLong(mContext, msg);
                } else {
                    ProjectUtils.showLong(mContext, msg);
                }
            }

        });
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

}