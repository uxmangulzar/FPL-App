package com.locksmith.fpl.Dentist.ui.adapter;

/**
 * Created by Usman on 31/04/21.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.locksmith.fpl.Dentist.DTO.StoreDTO1;
import com.locksmith.fpl.Dentist.DTO.UserDTO1;
import com.locksmith.fpl.R;
import com.locksmith.fpl.preferences.SharedPrefrence;
import java.util.ArrayList;


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {
    private String TAG = ServiceAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<StoreDTO1> objects = null;
    private ArrayList<StoreDTO1> postedJobDTOSList;
    private UserDTO1 userDTO;
    private SharedPrefrence preferences;


    public ServiceAdapter(Context context, ArrayList<StoreDTO1> objects, UserDTO1 userDTO) {
        this.mContext = context;
        this.objects = objects;
        this.postedJobDTOSList = new ArrayList<StoreDTO1>();
        this.postedJobDTOSList.addAll(objects);
        this.userDTO = userDTO;
        preferences = SharedPrefrence.getInstance(mContext);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_list_design, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //0 = pending, 1 = approved
        holder.service_name.setText(objects.get(position).getSub_category());
        //holder.tvPriceVolume.setText(objects.get(position).getVolume_pricing());
        holder.service_price.setText("$ "+objects.get(position).getPrice());
        Glide.with(mContext).
                load(objects.get(position).getProduct_image())
                .placeholder(R.drawable.cat_list_img_one)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.s_img);

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView service_price,service_name;
        public RelativeLayout suggest_service_btn;
        public ImageView s_img;


        public MyViewHolder(View view) {
            super(view);
            suggest_service_btn = view.findViewById(R.id.suggest_service_btn);
            service_name = view.findViewById(R.id.service_name);
            service_price = view.findViewById(R.id.service_price);
            s_img = view.findViewById(R.id.s_img);
        }
    }

}