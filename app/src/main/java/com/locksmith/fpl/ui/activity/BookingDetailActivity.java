package com.locksmith.fpl.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.preferences.SharedPrefrence;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingDetailActivity extends AppCompatActivity {
    @BindView(R.id.r_id)
    TextView r_id;
    @BindView(R.id.status_txt)
    TextView status_txt;
    @BindView(R.id.rq_s_name)
    TextView rq_s_name;
    @BindView(R.id.rq_s_cat_name)
    TextView rq_s_cat_name;
    @BindView(R.id.rq_s_price)
    TextView rq_s_price;
    @BindView(R.id.job_complete)
    TextView job_complete;
    @BindView(R.id.complete_ratio)
    TextView complete_ratio;
    @BindView(R.id.tech_name)
    TextView tech_name;
    @BindView(R.id.d_cancel_booking)
    RelativeLayout d_cancel_booking;
    @BindView(R.id.d_chat_booking)
    RelativeLayout d_chat_booking;
    @BindView(R.id.tech_image)
    ImageView tech_image;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    Bundle data;
    String br_id, br_booking_flag, br_s_name, br_s_cat_type, br_s_price, br_job_complete,
            br_complete_ratio, br_tech_image, br_tech_loc, br_tech_name, br_user_id, br_tech_id, br_currency_type;
    private HashMap<String, String> paramsDecline;
    private UserDTO userDTO;
    private HashMap<String, String> paramsBookingOp;
    private HashMap<String, String> paramsBookingFlag;
    private SharedPrefrence prefrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);
        ButterKnife.bind(this);
        prefrence = SharedPrefrence.getInstance(BookingDetailActivity.this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        try {
            data = getIntent().getExtras();
            if (data != null) {
                Log.e("@BDetailData@", "Data = " + data.toString());
                br_id = data.getString("br_id");
                br_booking_flag = data.getString("br_booking_flag");
                br_s_name = data.getString("br_s_name");
                br_s_cat_type = data.getString("br_s_cat_type");
                br_s_price = data.getString("br_s_price");
                br_job_complete = data.getString("br_job_complete");
                br_complete_ratio = data.getString("br_complete_ratio");
                br_tech_image = data.getString("br_tech_image");
                br_tech_loc = data.getString("br_tech_loc");
                br_tech_name = data.getString("br_tech_name");
                br_user_id = data.getString("br_user_id");
                br_tech_id = data.getString("br_tech_id");
                br_currency_type = data.getString("br_currency_type");
                r_id.setText("" + br_id);
                rq_s_name.setText("" + br_s_name);
                rq_s_price.setText(br_currency_type + "" + br_s_price);
                job_complete.setText("" + br_job_complete);
                complete_ratio.setText("" + br_complete_ratio);
                tech_name.setText("" + br_tech_name);
                if (br_id.equals("0")) {
                    r_id.setText("#FXEXW6");
                }
                if (br_booking_flag.equalsIgnoreCase("0")) {
                    status_txt.setText(getResources().getString(R.string.waiting_artist));
                } else if (br_booking_flag.equalsIgnoreCase("1")) {
                    status_txt.setText(getResources().getString(R.string.request_acc));
                } else if (br_booking_flag.equalsIgnoreCase("3")) {
                    status_txt.setText(getResources().getString(R.string.work_inpro));
                } else if (br_booking_flag.equalsIgnoreCase("4")) {
                    status_txt.setText(getResources().getString(R.string.complete));
                }
                if (br_s_cat_type.equalsIgnoreCase("1")) {
                    rq_s_cat_name.setText("Commercial");
                } else if (br_s_cat_type.equalsIgnoreCase("2")) {
                    rq_s_cat_name.setText("Residential");
                } else if (br_s_cat_type.equalsIgnoreCase("3")) {
                    rq_s_cat_name.setText("Automotive");
                }
                Glide.with(BookingDetailActivity.this).
                        load(br_tech_image)
                        .placeholder(R.drawable.dummyuser_image)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(tech_image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        d_cancel_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (br_booking_flag.equalsIgnoreCase("0")) {

                } else {

                }
            }
        });
        d_chat_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
}