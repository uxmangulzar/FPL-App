package com.locksmith.fpl.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.activity.BasicActivity;
import com.locksmith.fpl.ui.activity.SignInActivityForUser;
import com.locksmith.fpl.utils.AndroidMultiPartEntity;
import com.locksmith.fpl.utils.ImageCompression;
import com.locksmith.fpl.utils.MainFragment;
import com.locksmith.fpl.utils.MainFragment;
import com.locksmith.fpl.utils.ProjectUtils;
import com.locksmith.fpl.utils.SessionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private ImageView ivPersonalInfoChange, ivPasswordChange, ivAddressChange;
    private CircleImageView ivProfile;
    private RelativeLayout rely_password_Change;
    private BottomSheetDialog bottomSheetDialog;
    private TextView etName, etEmail, etMobile, etGender, etAddress, etAddressSelect, etCity, etCountry, u_name, u_email;
    private RelativeLayout tvYes, tvNo, tvYesPass, tvNoPass, tvSave, personal_info_layout;
    private EditText etNameD, etEmailD, etMobileD, etOldPassD, etNewPassD, etConfrimPassD, etAddressHome, etCityD, etCountryD, etAddressWork;
    private HashMap<String, String> params;
    private HashMap<String, File> paramsFile = new HashMap<>();
    private RelativeLayout RRsncbar;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    SessionManager sessionManager;
    AlertDialog alertDialog;
    long totalSize = 0;
    private String TAG = SettingFragment.class.getSimpleName();
    BottomSheet.Builder builder;
    Uri picUri;
    int PICK_FROM_CAMERA = 1, PICK_FROM_GALLERY = 2;
    int CROP_CAMERA_IMAGE = 3, CROP_GALLERY_IMAGE = 4;
    String imageName;
    String pathOfImage;
    Bitmap bm;
    ImageCompression imageCompression;
    byte[] resultByteArray;
    File file;
    Bitmap bitmap = null;
    private RelativeLayout cvSignOut, cvAccountDelete;
    private View view;
    private HashMap<String, String> paramsDeleteImg = new HashMap<>();
    private double lats = 0.0;
    private double longs = 0.0;
    public static float screen_width = 0;
    boolean isImageFitToScreen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screen_width = metrics.widthPixels;
        setUiAction(view);
        return view;
    }

    public void setUiAction(View v) {
        cvSignOut = v.findViewById(R.id.cvSignOut);
        cvAccountDelete = v.findViewById(R.id.cvDeleteAccount);
        rely_password_Change = v.findViewById(R.id.rely_password_change);
        personal_info_layout = v.findViewById(R.id.personal_info_layout);
        ivProfile = v.findViewById(R.id.ivProfile);
        etName = v.findViewById(R.id.etName);
        etEmail = v.findViewById(R.id.etEmail);
        etMobile = v.findViewById(R.id.etMobile);
        etAddress = v.findViewById(R.id.etAddress);
        u_name = v.findViewById(R.id.u_name);
        u_email = v.findViewById(R.id.u_email);
        etAddressSelect = v.findViewById(R.id.etAddressSelect);
        sessionManager = new SessionManager(getContext());
        ivProfile.setOnClickListener(this);
        rely_password_Change.setOnClickListener(this);
        cvSignOut.setOnClickListener(this);
        cvAccountDelete.setOnClickListener(this);
        etAddress.setOnClickListener(this);
        etAddressSelect.setOnClickListener(this);
        personal_info_layout.setOnClickListener(this);
        showData();
        builder = new BottomSheet.Builder(getActivity()).sheet(R.menu.menu_cards);
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
    }

    /*
     * **********************************************************************************************
     * Here is code that All Click on Settings Handle.
     ************************************************************************************************
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivProfile:
                checkPermissions();
                break;
            case R.id.personal_info_layout:
                dialogPersonalProfile();
                break;
            case R.id.rely_password_change:
                dialogPassword();
                break;
            case R.id.cvSignOut:
                confirmLogout();
                break;
            case R.id.cvDeleteAccount:
                confirmDeleteAccount();
                break;
        }
    }

    private void dialogPersonalProfile() {
        bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.change_personal_info_dialog, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        etNameD = (EditText) view.findViewById(R.id.etFullName);
        etEmailD = (EditText) view.findViewById(R.id.etEmail);
        etMobileD = (EditText) view.findViewById(R.id.etMobile);
        etAddressHome = (EditText) view.findViewById(R.id.etHomeAddress);
        etAddressWork = (EditText) view.findViewById(R.id.etWorkAddress);
        tvSave = (RelativeLayout) view.findViewById(R.id.tvSave);
        tvNo = (RelativeLayout) view.findViewById(R.id.tvNo);
        ImageButton ivBack = view.findViewById(R.id.ivBack);
        etNameD.setText(userDTO.getName());
        etEmailD.setText(userDTO.getEmail_id());
        etMobileD.setText(userDTO.getMobile_no());
        etAddressHome.setText(userDTO.getAddress());
        etAddressWork.setText(userDTO.getOffice_address());
        tvSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        params = new HashMap<>();
                        params.put(Consts.USER_ID, userDTO.getUser_id());
                        params.put(Consts.ADDRESS, ProjectUtils.getEditTextValue(etAddressHome));
                        params.put(Consts.MOBILE, ProjectUtils.getEditTextValue(etMobileD));
                        //params.put(Consts.NAME, ProjectUtils.getEditTextValue(etNameD));
                        params.put(Consts.OFFICE_ADDRESS, ProjectUtils.getEditTextValue(etAddressWork));
                        if (NetworkManager.isConnectToInternet(getActivity())) {
                            updateProfile();
                            bottomSheetDialog.dismiss();
                        } else {
                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                        }
                    }
                });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
    }
    /*
     * **********************************************************************************************
     * Here is code that will GET Runtime Permission.
     ************************************************************************************************
     */
    private void checkPermissions() {
        Dexter.withContext(getContext())
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
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Permission Required")
                                    .setMessage("These permission is mandatory to select pictures in this app")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getContext().getPackageName(), null));
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
                picUri = Uri.parse(data.getExtras().getString("resultUri"));
                Log.e(TAG, "cropped image "+picUri.toString());
                Glide.with(getActivity()).load(picUri).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.imagethumbnail).into(ivProfile);
                String path = getPath(getContext(),picUri);
                if (path!=null) {
                    Log.e("@Picture Path@", "" + path);
                    try {
                        file = new File(path);
                        Log.e("@File Path@", "" + String.valueOf(file));
                        paramsFile.put(Consts.IMAGE, file);
                        params = new HashMap<>();
                        params.put(Consts.USER_ID, userDTO.getUser_id());
                        if (NetworkManager.isConnectToInternet(getActivity())) {
                            Log.e("@", "params file= "+paramsFile.toString());
                            Log.e("@", "params = "+params.toString());
                            //new UploadFileToServer().execute();
                            updateProfileImage();
                        } else {
                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //File file = new File(String.valueOf(picUri));
            }
        }
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                Uri tempUri = data.getData();
                Log.e("front tempUri", "" + tempUri);
                if (tempUri != null) {
                    startCropping(tempUri, CROP_GALLERY_IMAGE);
                } else {

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void startCropping(Uri uri, int requestCode) {
        Intent intent = new Intent(getActivity(), BasicActivity.class);
        intent.putExtra("imageUri", uri.toString());
        intent.putExtra("requestCode", requestCode);
        startActivityForResult(intent, requestCode);
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

    public void showData() {
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        Glide.with(getActivity()).
                load(userDTO.getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivProfile);
        u_name.setText("" + userDTO.getName());
        u_email.setText("" + userDTO.getEmail_id());
        etName.setText(userDTO.getName());
        etEmail.setText(userDTO.getEmail_id());
        etMobile.setText(userDTO.getMobile_no());
        if (userDTO.getGender().equalsIgnoreCase("0")) {
            //etGender.setText(getResources().getString(R.string.female));
        } else if (userDTO.getGender().equalsIgnoreCase("1")) {
            //etGender.setText(getResources().getString(R.string.male));
        } else {
            // etGender.setText("");
        }
        etAddress.setText(userDTO.getAddress());
        etAddressSelect.setText(userDTO.getOffice_address());
    }
    public void dialogPassword() {
        bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.change_password_dialog, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        etOldPassD = (EditText) view.findViewById(R.id.etOldPassD);
        etNewPassD = (EditText) view.findViewById(R.id.etNewPassD);
        etConfrimPassD = (EditText) view.findViewById(R.id.etConfrimPassD);
        tvYesPass = (RelativeLayout) view.findViewById(R.id.tvYesPass);
        tvNoPass = (RelativeLayout) view.findViewById(R.id.tvNoPass);
        ImageButton ivBack = view.findViewById(R.id.ivBack);
        tvNoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        tvYesPass.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        params = new HashMap<>();
                        params.put(Consts.USER_ID, userDTO.getUser_id());
                        params.put(Consts.PASSWORD, ProjectUtils.getEditTextValue(etOldPassD));
                        params.put(Consts.NEW_PASSWORD, ProjectUtils.getEditTextValue(etNewPassD));
                        if (NetworkManager.isConnectToInternet(getActivity())) {
                            updateProfile();
                            bottomSheetDialog.dismiss();
                        } else {
                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                        }
                    }
                });

    }

    private void confirmDeleteAccount() {
        new ProjectUtils().showPopDialog(getActivity(), getResources().getString(R.string.del_account), new ProjectUtils.onDialogClickListener() {
            @Override
            public void dialogClick(boolean click) {
                //Toast.makeText(HomeActivity.this, "yes click by Home Activity", Toast.LENGTH_SHORT).show();
                deleteAccount();
            }
        });
    }

    private void deleteAccount() {
        ProjectUtils.showCustomProgressDialog(getActivity(), getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.DELETE_ACCOUNT, getparm(), getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.hideDialog();
                if (flag) {
                    ProjectUtils.showToast(getActivity(), msg);
                    //ProjectUtils.hideDialog();
                    prefrence.clearAllPreferences();
                    Intent intent = new Intent(getActivity(), SignInActivityForUser.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }
            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        return parms;
    }

    public void updateProfile() {
        ProjectUtils.showCustomProgressDialog(getActivity(), getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.UPDATE_PROFILE_API, params, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                //Log.e(TAG, "backResponse: = "+response.toString());
                ProjectUtils.hideDialog();
                if (flag) {
                    Log.e(TAG, "backResponse: = "+response.toString());
                    try {
                        ProjectUtils.showToast(getActivity(), msg);
                        userDTO = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);
                        prefrence.setParentUser(userDTO, Consts.USER_DTO);
                        showData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }
            }
        });
    }
    private void updateProfileImage() {
        ProjectUtils.showCustomProgressDialog(getActivity(), getResources().getString(R.string.update_profile_wait));
        new HttpsRequest(Consts.UPDATE_PROFILE_API, params,paramsFile, getActivity()).imagePost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                //Log.e(TAG, "backResponse: = "+response.toString());
                ProjectUtils.hideDialog();
                if (flag) {
                    Log.e(TAG, "backResponse: = "+response.toString());
                    try {
                        ProjectUtils.showToast(getActivity(), msg);
                        userDTO = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);
                        prefrence.setParentUser(userDTO, Consts.USER_DTO);
                        showData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }
            }
        });
//        AndroidNetworking.upload(Consts.BASE_URL+Consts.UPDATE_PROFILE_API)
//                .addMultipartFile(Consts.IMAGE, file)
//                .addMultipartParameter(Consts.USER_ID, userDTO.getUser_id())
//                .setPriority(Priority.HIGH)
//                .build()
//                .setUploadProgressListener(new UploadProgressListener() {
//                    @Override
//                    public void onProgress(long bytesUploaded, long totalBytes) {
//                        float progress = (float) (bytesUploaded / totalBytes * 100);
//                    }
//                })
//                .getAsString(new StringRequestListener() {
//                    @Override
//                    public void onResponse(String response) {
//                        ProjectUtils.hideDialog();
//                        Log.e("@@Server Response@@", response);
//                    }
//                    @Override
//                    public void onError(ANError anError) {
//                        ProjectUtils.hideDialog();
//                        anError.printStackTrace();
//                        Log.e("@", "onError body = "+anError.getErrorBody()+"onError message = "+anError.getMessage()+"onError message = "+anError.getErrorDetail());
//                        Toast.makeText(getContext(), getResources().getString(R.string.str_error_u), Toast.LENGTH_SHORT).show();
//                    }
//                });
    }


    public void confirmLogout() {
        try {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("loginpref", MODE_PRIVATE).edit();
            editor.putString("user", "");
            editor.apply();
            new ProjectUtils().showPopDialog(getActivity(), getResources().getString(R.string.logout_msg), new ProjectUtils.onDialogClickListener() {
                @Override
                public void dialogClick(boolean click) {
                    //Toast.makeText(HomeActivity.this, "yes click by Home Activity", Toast.LENGTH_SHORT).show();
                    ProjectUtils.hideDialog();
                    prefrence.clearAllPreferences();
                    Intent intent = new Intent(getActivity(), SignInActivityForUser.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void deleteImage() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        paramsDeleteImg.put(Consts.USER_ID, userDTO.getUser_id());
        new HttpsRequest(Consts.DELETE_PROFILE_IMAGE_API, paramsDeleteImg, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    userDTO.setImage("");
                    prefrence.setParentUser(userDTO, Consts.USER_DTO);
                    showData();
                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }


            }
        });
    }
    ///////////////****************Upload Image To Server Using HTTPClient***************////////////////////
    ///////////////****************Upload Image To Server Code Start here***************////////////////////
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... voids) {
            return uploadFile();
        }
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Consts.BASE_URL+Consts.UPDATE_PROFILE_API);

            Log.e("@@Uploading URL@@", httppost.toString());
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                //File sourceFile = new File(file);
                // Adding file data to http body
                entity.addPart(Consts.IMAGE, new FileBody(file));
                // Extra parameters if you want to pass to server
                entity.addPart(Consts.USER_ID, new StringBody(userDTO.getUser_id()));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    Log.e("@@Upoading Response@@", responseString);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                    Log.e("@@Upoading Response@@", responseString);
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("@Upload Image Response@", "Response from server:" + s);
            super.onPostExecute(s);
        }
    }


}
