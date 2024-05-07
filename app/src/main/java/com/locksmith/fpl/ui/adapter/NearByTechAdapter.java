package com.locksmith.fpl.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.locksmith.fpl.DTO.NearbyTechDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.utils.ProjectUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NearByTechAdapter extends RecyclerView.Adapter<NearByTechAdapter.CustomViewHolder> {
    Context mContext;
    public ArrayList<NearbyTechDTO> tech_list;

    public NearByTechAdapter(Context mContext, ArrayList<NearbyTechDTO> tech_list) {
        this.mContext = mContext;
        this.tech_list = tech_list;

    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.nearby_tech_list_design, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.tech_name.setText("" + tech_list.get(position).getName());
        double i = Double.parseDouble(tech_list.get(position).getDistance());
        holder.tech_distance.setText(new DecimalFormat("##.##").format(i) + " Miles Away");
        Glide.with(mContext).
                load(tech_list.get(position).getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.tech_profile_img);
    }

    @Override
    public int getItemCount() {
        return tech_list.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView tech_name, tech_distance;
        public CircleImageView tech_profile_img;
        public RatingBar tech_rate;
        public RelativeLayout chat_btn;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            tech_name = itemView.findViewById(R.id.tech_name);
            tech_distance = itemView.findViewById(R.id.tech_distance);
            tech_profile_img = itemView.findViewById(R.id.tech_profile_img);
            tech_rate = itemView.findViewById(R.id.tech_rate);
            chat_btn = itemView.findViewById(R.id.chat_btn);
        }
    }
}