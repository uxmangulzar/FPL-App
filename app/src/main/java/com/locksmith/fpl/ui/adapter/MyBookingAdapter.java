package com.locksmith.fpl.ui.adapter;

/**
 * Created by Usman on 12/01/21.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.locksmith.fpl.DTO.UserBooking;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.ui.activity.BookingDetailActivity;
import com.locksmith.fpl.ui.fragment.MyBookingsFragment;
import java.util.ArrayList;


public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.MyViewHolder> {
    private String TAG = MyBookingAdapter.class.getSimpleName();
    MyBookingsFragment myBooking;
    private Context mContext;
    private ArrayList<UserBooking> userBookingList;
    private UserDTO userDTO;

    public MyBookingAdapter(MyBookingsFragment myBooking, ArrayList<UserBooking> userBookingList, UserDTO userDTO) {
        this.myBooking = myBooking;
        mContext = myBooking.getActivity();
        this.userBookingList = userBookingList;
        this.userDTO = userDTO;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_list_design, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.req_service_name.setText("" + userBookingList.get(position).getCategory_name());
        holder.tvLocation.setText("" + userBookingList.get(position).getArtistLocation());
        holder.tvName.setText("" + userBookingList.get(position).getArtistName());

        if (userBookingList.get(position).getJob_id().equals("0")) {
            holder.req_id_txt.setText("#DTXEFR");
        } else {
            holder.req_id_txt.setText("" + userBookingList.get(position).getJob_id());
        }
        holder.req_service_price.setText(userBookingList.get(position).getCurrency_type() + "" + userBookingList.get(position).getPrice());
        if (userBookingList.get(position).getBooking_type().equalsIgnoreCase("1")) {
            holder.tvWork.setText("Commercial");
        } else if (userBookingList.get(position).getBooking_type().equalsIgnoreCase("2")) {
            holder.tvWork.setText("Residential");
        } else if (userBookingList.get(position).getBooking_type().equalsIgnoreCase("3")) {
            holder.tvWork.setText("Automotive");
        }
        Glide.with(mContext).
                load(userBookingList.get(position).getArtistImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivArtist);

        if (userBookingList.get(position).getBooking_flag().equalsIgnoreCase("0")) {
            //holder.tvStatus.setText(mContext.getResources().getString(R.string.waiting_artist));
            holder.layout_in_progress.setVisibility(View.VISIBLE);
            holder.response_text.setText("" + mContext.getResources().getString(R.string.waiting_artist));
            holder.layout_complete.setVisibility(View.GONE);

        } else if (userBookingList.get(position).getBooking_flag().equalsIgnoreCase("1")) {
            //holder.tvStatus.setText(mContext.getResources().getString(R.string.request_acc));
            holder.layout_in_progress.setVisibility(View.VISIBLE);
            holder.response_text.setText("" + mContext.getResources().getString(R.string.request_acc));
            holder.layout_complete.setVisibility(View.GONE);
        } else if (userBookingList.get(position).getBooking_flag().equalsIgnoreCase("3")) {
            //holder.tvStatus.setText(mContext.getResources().getString(R.string.work_inpro));
            holder.layout_in_progress.setVisibility(View.VISIBLE);
            holder.response_text.setText("" + mContext.getResources().getString(R.string.work_inpro));
            holder.layout_complete.setVisibility(View.GONE);
        } else if (userBookingList.get(position).getBooking_flag().equalsIgnoreCase("4")) {
            holder.layout_in_progress.setVisibility(View.GONE);
            holder.layout_complete.setVisibility(View.VISIBLE);
            if (userBookingList.get(position).getAva_rating().equalsIgnoreCase("")) {
                holder.complete_price_txt.setVisibility(View.GONE);
                holder.complete_rating.setVisibility(View.GONE);
                holder.req_service_price.setVisibility(View.VISIBLE);
            } else {
                holder.complete_price_txt.setVisibility(View.VISIBLE);
                holder.complete_rating.setVisibility(View.VISIBLE);
                holder.complete_rating.setRating(Float.parseFloat(userBookingList.get(position).getAva_rating()));
                holder.req_service_price.setVisibility(View.GONE);
                holder.complete_price_txt.setText("" + userBookingList.get(position).getPrice());
            }
        }
        holder.bookingClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BookingDetailActivity.class);
                intent.putExtra("br_id", userBookingList.get(position).getJob_id());
                intent.putExtra("br_booking_flag", userBookingList.get(position).getBooking_flag());
                intent.putExtra("br_s_name", userBookingList.get(position).getCategory_name());
                intent.putExtra("br_s_cat_type", userBookingList.get(position).getBooking_type());
                intent.putExtra("br_s_price", userBookingList.get(position).getPrice());
                intent.putExtra("br_job_complete", userBookingList.get(position).getJobDone());
                intent.putExtra("br_complete_ratio", userBookingList.get(position).getCompletePercentages());
                intent.putExtra("br_tech_image", userBookingList.get(position).getArtistImage());
                intent.putExtra("br_tech_name", userBookingList.get(position).getArtistName());
                intent.putExtra("br_tech_loc", userBookingList.get(position).getArtistLocation());
                intent.putExtra("br_user_id", userBookingList.get(position).getUser_id());
                intent.putExtra("br_tech_id", userBookingList.get(position).getArtist_id());
                intent.putExtra("br_currency_type", userBookingList.get(position).getCurrency_type());
                mContext.startActivity(intent);
            }
        });
        /*
         ////***** This logic code for last item of recyclerview that behind the
          BottomApp Bar to show Upper side of the bottom Bar *****///
        if (position == getItemCount() - 1) {
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) holder.bookingClick.getLayoutParams();
            params1.setMargins(44, 24, 44, 120);
            holder.bookingClick.setLayoutParams(params1);
            //Toast.makeText(context, "Last Item", Toast.LENGTH_SHORT).show();
        } else {
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) holder.bookingClick.getLayoutParams();
            params1.setMargins(44, 24, 44, 24);
            holder.bookingClick.setLayoutParams(params1);
        }

    }

    @Override
    public int getItemCount() {

        return userBookingList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivArtist;
        public TextView tvStatus, tvWork, req_id_txt, tvLocation, complete_price_txt, response_text, req_service_name, req_service_price;
        public LinearLayout layout_in_progress, rate_layout;
        public TextView tvName;
        public RelativeLayout layout_complete;
        public CardView bookingClick;
        public RatingBar complete_rating;

        public MyViewHolder(View view) {
            super(view);
            ivArtist = view.findViewById(R.id.ivArtist);
            req_id_txt = view.findViewById(R.id.req_id_txt);
            tvWork = view.findViewById(R.id.tvWork);
            req_service_name = view.findViewById(R.id.req_service_name);
            req_service_price = view.findViewById(R.id.req_service_price);
            tvName = view.findViewById(R.id.artistName);
            tvLocation = view.findViewById(R.id.tvLocation);
            bookingClick = view.findViewById(R.id.bookingClick);
            response_text = view.findViewById(R.id.response_text);
            complete_price_txt = view.findViewById(R.id.complete_price_txt);
            layout_in_progress = view.findViewById(R.id.layout_in_progress);
            rate_layout = view.findViewById(R.id.rate_layout);
            layout_complete = view.findViewById(R.id.layout_complete);
            complete_rating = view.findViewById(R.id.complete_rating);
        }
    }

}