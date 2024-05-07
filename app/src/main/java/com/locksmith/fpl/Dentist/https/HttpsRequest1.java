package com.locksmith.fpl.Dentist.https;

import android.content.Context;
import android.util.Log;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.locksmith.fpl.Dentist.interfacess.Consts1;
import com.locksmith.fpl.Dentist.interfacess.Helper1;
import com.locksmith.fpl.Dentist.jsonparser.JSONParser1;
import com.locksmith.fpl.utils.ProjectUtils;


import org.json.JSONObject;

import java.io.File;
import java.util.Map;

/**
 * Created by Usman on 20/3/18.
 */

public class HttpsRequest1 {
    private String match;
    private Map<String, String> params;
    private Map<String, File> fileparams;
    private Context ctx;
    private JSONObject jObject;

    public HttpsRequest1(String match, Map<String, String> params, Context ctx) {
        this.match = match;
        this.params = params;
        this.ctx = ctx;
    }

    public HttpsRequest1(String match, Map<String, String> params, Map<String, File> fileparams, Context ctx) {
        this.match = match;
        this.params = params;
        this.fileparams = fileparams;
        this.ctx = ctx;
    }

    public HttpsRequest1(String match, Context ctx) {
        this.match = match;
        this.ctx = ctx;
    }

    public HttpsRequest1(String match, JSONObject jObject, Context ctx) {
        this.match = match;
        this.jObject = jObject;
        this.ctx = ctx;


    }


    public void stringPostJson(final String TAG, final Helper1 h) {
        AndroidNetworking.post(Consts1.BASE_URL + match)
                .addJSONObjectBody(jObject)
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, " response body --->" + response.toString());
                        Log.e(TAG, " response body --->" + jObject.toString());
                        JSONParser1 jsonParser1 = new JSONParser1(ctx, response);
                        if (jsonParser1.RESULT) {
                            h.backResponse(jsonParser1.RESULT, jsonParser1.MESSAGE, response);
                        } else {
                            h.backResponse(jsonParser1.RESULT, jsonParser1.MESSAGE, null);
                        }



                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtils.pauseProgressDialog();
                        Log.e(TAG, " error body --->" + anError.getErrorBody() + " error msg --->" + anError.getMessage());
                    }
                });
    }

    public void stringPost(final String TAG, final Helper1 h) {
        AndroidNetworking.post(Consts1.BASE_URL + match)
                .addBodyParameter(params)
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, " response body --->" + response.toString());
                        Log.e(TAG, " param --->" + params.toString());
                        JSONParser1 jsonParser1 = new JSONParser1(ctx, response);
                        if (jsonParser1.RESULT) {
                            h.backResponse(jsonParser1.RESULT, jsonParser1.MESSAGE, response);
                        } else {
                            h.backResponse(jsonParser1.RESULT, jsonParser1.MESSAGE, null);
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        ProjectUtils.pauseProgressDialog();
                        Log.e("appliedd", " error body --->" + anError.getErrorBody() + " error msg --->" + anError.getMessage());
                    }
                });
    }
    public void stringGet(final String TAG, final Helper1 h) {
        AndroidNetworking.get(Consts1.BASE_URL + match)
                .setTag("test")

                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, " response body --->" + response.toString());
                        JSONParser1 jsonParser1 = new JSONParser1(ctx, response);
                        if (jsonParser1.RESULT) {

                            h.backResponse(jsonParser1.RESULT, jsonParser1.MESSAGE, response);
                        } else {
                            h.backResponse(jsonParser1.RESULT, jsonParser1.MESSAGE, null);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtils.pauseProgressDialog();
                        Log.e(TAG, " error body --->" + anError.getErrorBody() + " error msg --->" + anError.getMessage());
                    }
                });
    }

    public void imagePost(final String TAG, final Helper1 h) {
        AndroidNetworking.upload(Consts1.BASE_URL + match)
                .addMultipartFile(fileparams)
                .addMultipartParameter(params)
                .setTag("uploadTest")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        Log.e("Byte", bytesUploaded + "  !!! " + totalBytes);
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, " response body --->" + response.toString());
                        Log.e(TAG, " param --->" + params.toString());
                        JSONParser1 jsonParser1 = new JSONParser1(ctx, response);
                        if (jsonParser1.RESULT) {
                            h.backResponse(jsonParser1.RESULT, jsonParser1.MESSAGE, response);
                        } else {
                            h.backResponse(jsonParser1.RESULT, jsonParser1.MESSAGE, null);
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        ProjectUtils.pauseProgressDialog();
                        Log.e(TAG, " error body --->" + anError.getErrorBody() + " error msg --->" + anError.getMessage());
                    }
                });
    }

}