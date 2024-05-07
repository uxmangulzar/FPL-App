package com.locksmith.fpl.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.locksmith.fpl.DTO.TicketCommentDTO;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.network.NetworkManager;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.adapter.AdapterViewCommentTicket;
import com.locksmith.fpl.utils.ProjectUtils;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class CommentOneByOne extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{
    private String TAG = CommentOneByOne.class.getSimpleName();
    private ListView lvComment;
    private EditText etMessage;
    private ImageView buttonSendMessage, IVback, emojiButton;
    private AdapterViewCommentTicket adapterViewCommentTicket;
    private String id = "";
    private ArrayList<TicketCommentDTO> ticketCommentDTOSList;
    private InputMethodManager inputManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EmojiconEditText edittextMessage;
    private EmojIconActions emojIcon;
    private RelativeLayout relative;
    private Context mContext;
    private HashMap<String, String> parmsGet = new HashMap<>();
    private TextView tvNameHedar;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private String ticket_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_comment_one_by_one);
//        try {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                // Do something for lollipop and above versions
//                getWindow().setStatusBarColor(getResources().getColor(R.color.));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        mContext = CommentOneByOne.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getIntent().hasExtra(Consts.TICKET_ID)){
            ticket_id = getIntent().getStringExtra(Consts.TICKET_ID);
            parmsGet.put(Consts.TICKET_ID, ticket_id);
            parmsGet.put(Consts.USER_ID, userDTO.getUser_id());
        }
        setUiAction();
    }
    public void setUiAction() {
        //tvNameHedar = (TextView) findViewById(R.id.tvNameHedar);
        relative = (RelativeLayout) findViewById(R.id.relative);
        edittextMessage = (EmojiconEditText) findViewById(R.id.edittextMessage);
        emojiButton = (ImageView) findViewById(R.id.emojiButton);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        lvComment = (ListView) findViewById(R.id.lvComment);
        etMessage = (EditText) findViewById(R.id.etMessage);
        buttonSendMessage = (ImageView) findViewById(R.id.buttonSendMessage);
        IVback = (ImageView) findViewById(R.id.IVback);
        buttonSendMessage.setOnClickListener(this);
        IVback.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.e("Runnable", "FIRST");
                                        if (NetworkManager.isConnectToInternet(mContext)) {
                                            swipeRefreshLayout.setRefreshing(true);
                                            getComment();

                                        } else {
                                            ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                                        }
                                    }
                                }
        );

        emojIcon = new EmojIconActions(this, relative, edittextMessage, emojiButton, "#495C66", "#DCE1E2", "#E6EBEF");
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });

    }

    public boolean validateMessage() {
        if (edittextMessage.getText().toString().trim().length() <= 0) {
            edittextMessage.setError(getResources().getString(R.string.val_comment));
            edittextMessage.requestFocus();
            return false;
        } else {
            edittextMessage.setError(null);
            edittextMessage.clearFocus();
            return true;
        }
    }

   public void submit() {
        if (!validateMessage()) {
            return;
        } else {
//            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                    InputMethodManager.HIDE_NOT_ALWAYS);
            if (NetworkManager.isConnectToInternet(mContext)) {
                doComment();
            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSendMessage:
                submit();
                break;
            case R.id.IVback:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getComment();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
    }

    public void getComment() {
        //ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_TICKET_COMMENTS_API, parmsGet, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                //ProjectUtils.pauseProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    try {
                        ticketCommentDTOSList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<TicketCommentDTO>>() {
                        }.getType();
                        ticketCommentDTOSList = (ArrayList<TicketCommentDTO>) new Gson().fromJson(response.getJSONArray("ticket_comment").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    ProjectUtils.showToast(mContext, msg);
                }
            }
        });
    }

    public void showData()
    {
        adapterViewCommentTicket = new AdapterViewCommentTicket(mContext, ticketCommentDTOSList, userDTO);
        lvComment.setAdapter(adapterViewCommentTicket);
        adapterViewCommentTicket.notifyDataSetChanged();
        lvComment.setSelection(ticketCommentDTOSList.size() - 1);
    }


    public void doComment() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.ADD_TICKET_COMMENTS_API, getParamDO(), mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    edittextMessage.setText("");
                    getComment();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }
            }
        });
    }

    public HashMap<String, String> getParamDO() {
        HashMap<String, String> values = new HashMap<>();
        values.put(Consts.TICKET_ID, ticket_id);
        values.put(Consts.USER_ID, userDTO.getUser_id());
        values.put(Consts.COMMENT, ProjectUtils.getEditTextValue(edittextMessage));
        Log.e("POST", values.toString());
        return values;
    }

}
