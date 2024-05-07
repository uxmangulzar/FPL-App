package com.locksmith.fpl.ui.adapter;

/**
 * Created by Usman on 05/01/21.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.locksmith.fpl.DTO.PostedJobDTO;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.https.HttpsRequest;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.interfacess.Helper;
import com.locksmith.fpl.preferences.SharedPrefrence;
import com.locksmith.fpl.ui.activity.EditPostJobActivity;
import com.locksmith.fpl.ui.fragment.MyRequestFragment;
import com.locksmith.fpl.utils.ProjectUtils;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.MyViewHolder> {
    //private String TAG = AppliedJobAdapter.class.getSimpleName();
    private HashMap<String, String> params;
    private HashMap<String, String> paramsComplete;
    private DialogInterface dialog_book;
    private Context mContext;
    MyRequestFragment myRequestFragment;
    private ArrayList<PostedJobDTO> objects = null;
    private ArrayList<PostedJobDTO> postedJobDTOSList;
    private UserDTO userDTO;
    private SharedPrefrence preferences;


    public JobsAdapter(Context context, MyRequestFragment requestFragment, ArrayList<PostedJobDTO> objects, UserDTO userDTO) {
        this.mContext = context;
        this.objects = objects;
        this.postedJobDTOSList = new ArrayList<PostedJobDTO>();
        this.postedJobDTOSList.addAll(objects);
        this.myRequestFragment = requestFragment;
        this.userDTO = userDTO;
        preferences = SharedPrefrence.getInstance(mContext);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_list_design, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//0 = pending, 1 = confirm , 2 = complete, 3 =reject

        holder.tvJobId.setText("" + objects.get(position).getJob_id());
        holder.tvTitle.setText(objects.get(position).getTitle());
        holder.tvDescription.setText(objects.get(position).getDescription());
        if (objects.get(position).getCategory_id().equalsIgnoreCase("66")) {
            holder.tvCategory.setText("Residential");
        } else if (objects.get(position).getCategory_id().equalsIgnoreCase("67")) {
            holder.tvCategory.setText("Commercial");
        } else if (objects.get(position).getCategory_id().equalsIgnoreCase("68")) {
            holder.tvCategory.setText("Automotive");
        }
        //holder.tvCategory.setText(objects.get(position).getCategory_name());
        holder.tvAddress.setText(objects.get(position).getAddress());
        holder.tvDate.setText("" + ProjectUtils.changeDateFormate(objects.get(position).getCreated_at()));
        holder.tvPrice.setText(objects.get(position).getCurrency_symbol() + "" + objects.get(position).getPrice());

        if (objects.get(position).getStatus().equalsIgnoreCase("0")) {
            holder.rlComplete.setVisibility(View.VISIBLE);
            //holder.tvStatus.setText(mContext.getResources().getString(R.string.open));
            holder.tv_offers.setVisibility(View.VISIBLE);
            // holder.textOne.setVisibility(View.VISIBLE);
            //holder.textOne.setText(objects.get(position).getApplied_job());
            //holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
        } else if (objects.get(position).getStatus().equalsIgnoreCase("1")) {
            holder.rlComplete.setVisibility(View.VISIBLE);
            //holder.tvStatus.setText(mContext.getResources().getString(R.string.confirm));
            //holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
        } else if (objects.get(position).getStatus().equalsIgnoreCase("2")) {
            holder.rlComplete.setVisibility(View.GONE);
            //holder.tvStatus.setText(mContext.getResources().getString(R.string.completed));
            //holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
        } else if (objects.get(position).getStatus().equalsIgnoreCase("3")) {
            holder.rlComplete.setVisibility(View.VISIBLE);
            //holder.tvStatus.setText(mContext.getResources().getString(R.string.rejected));
            //holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_dark_red));
        }

        Glide.with(mContext).
                load(objects.get(position).getAvtar())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivImage);

        holder.tv_offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 Intent in = new Intent(mContext, AppliedJob.class);
//                preferences.setValue(Consts.JOB_ID, objects.get(position).getJob_id());
//                mContext.startActivity(in);
            }
        });

        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objects.get(position).getIs_edit().equalsIgnoreCase("1")) {
                    Intent in = new Intent(mContext, EditPostJobActivity.class);
                    in.putExtra(Consts.POST_JOB_DTO, objects.get(position));
                    mContext.startActivity(in);
                } else {
                    ProjectUtils.showLong(mContext, mContext.getResources().getString(R.string.not_edit_job));
                }

            }
        });
        holder.tvDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params = new HashMap<>();
                params.put(Consts.JOB_ID, objects.get(position).getJob_id());
                params.put(Consts.STATUS, "4");
                rejectDialog(mContext.getResources().getString(R.string.delete) + " " + objects.get(position).getTitle(), mContext.getResources().getString(R.string.delete_job));
            }
        });
        holder.tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramsComplete = new HashMap<>();
                paramsComplete.put(Consts.JOB_ID, objects.get(position).getJob_id());
                paramsComplete.put(Consts.USER_ID, objects.get(position).getUser_id());
                completeDialog(mContext.getResources().getString(R.string.complete), mContext.getResources().getString(R.string.complete_job));
            }
        });
         /*
         ////***** This logic code for last item of recyclerview that behind the
          BottomApp Bar to show Upper side of the bottom Bar *****///
        if (position == getItemCount() - 1) {
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) holder.req_main_liner.getLayoutParams();
            params1.setMargins(44, 24, 44, 120);
            holder.req_main_liner.setLayoutParams(params1);
            //Toast.makeText(context, "Last Item", Toast.LENGTH_SHORT).show();
        } else {
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) holder.req_main_liner.getLayoutParams();
            params1.setMargins(44, 24, 44, 24);
            holder.req_main_liner.setLayoutParams(params1);
        }

    }

    @Override
    public int getItemCount() {

        return objects.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvJobId, tvPrice, tv_offers;
        public TextView tvDate, tvStatus, tvTitle, tvDescription, tvCategory, tvAddress;
        public LinearLayout llStatus, rlComplete;
        public RelativeLayout rlClick;
        public ImageView ivImage;
        public RelativeLayout tvDecline, tvEdit, tvComplete;
        public TextView textOne;
        public LinearLayout req_main_liner;

        public MyViewHolder(View view) {
            super(view);
            tvJobId = view.findViewById(R.id.tvJobId);
            req_main_liner = view.findViewById(R.id.req_main_liner);
            tvDate = view.findViewById(R.id.tvDate);
            ivImage = view.findViewById(R.id.ivImage);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDecline = view.findViewById(R.id.tvDecline);
            tvEdit = view.findViewById(R.id.tvEdit);
            rlComplete = view.findViewById(R.id.rlComplete);
            tvCategory = view.findViewById(R.id.tvCategory);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvComplete = view.findViewById(R.id.tvComplete);
            tv_offers = view.findViewById(R.id.tv_offers);

        }
    }


    public void reject() {

        new HttpsRequest(Consts.DELETE_JOB_API, params, mContext).stringPost("TAG", new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    myRequestFragment.getjobs();

                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void complete() {

        new HttpsRequest(Consts.JOB_COMPLETE_API, paramsComplete, mContext).stringPost("TAG", new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    myRequestFragment.getjobs();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void rejectDialog(String title, String msg) {
        try {
            new ProjectUtils().showPopDialog((Activity) mContext, msg, new ProjectUtils.onDialogClickListener() {
                @Override
                public void dialogClick(boolean click) {
                    //Toast.makeText(HomeActivity.this, "yes click by Home Activity", Toast.LENGTH_SHORT).show();
                    ProjectUtils.hideDialog();
                    reject();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void completeDialog(String title, String msg) {
        try {
            new ProjectUtils().showPopDialog((Activity) mContext, msg, new ProjectUtils.onDialogClickListener() {
                @Override
                public void dialogClick(boolean click) {
                    //Toast.makeText(HomeActivity.this, "yes click by Home Activity", Toast.LENGTH_SHORT).show();
                    ProjectUtils.hideDialog();
                    complete();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        objects.clear();
        if (charText.length() == 0) {
            objects.addAll(postedJobDTOSList);
        } else {
            for (PostedJobDTO postedJobDTO : postedJobDTOSList) {
                if (postedJobDTO.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    objects.add(postedJobDTO);
                }
            }
        }
        notifyDataSetChanged();
    }

}