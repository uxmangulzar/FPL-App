package com.locksmith.fpl.ui.adapter;

/**
 * Created by Usman on 31/10/17.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.locksmith.fpl.DTO.TicketDTO;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.interfacess.Consts;
import com.locksmith.fpl.ui.activity.CommentOneByOne;
import com.locksmith.fpl.utils.ProjectUtils;

import java.util.ArrayList;


public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<TicketDTO> ticketDTOSList;
    private UserDTO userDTO;


    public TicketAdapter(Context context, ArrayList<TicketDTO> ticketDTOSList, UserDTO userDTO) {
        this.mContext = context;
        this.ticketDTOSList = ticketDTOSList;
        this.userDTO = userDTO;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_ticket, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        String upperString = ticketDTOSList.get(position).getReason().substring(0, 1).toUpperCase() + ticketDTOSList.get(position).getReason().substring(1).toLowerCase();
        holder.tvTicket.setText(""+upperString);


        try{
            holder.tvDate.setText(ProjectUtils.convertTimestampDateToTime(ProjectUtils.correctTimestamp(Long.parseLong(ticketDTOSList.get(position).getCraeted_at()))));

        }catch (Exception e){
            e.printStackTrace();
        }


        if (ticketDTOSList.get(position).getStatus().equalsIgnoreCase("0")) {
            holder.tvStatus.setText(mContext.getResources().getString(R.string.pending));
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.red_color));
            //holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_red));
        } else if (ticketDTOSList.get(position).getStatus().equalsIgnoreCase("1")) {
            holder.tvStatus.setText(mContext.getResources().getString(R.string.solve));
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.green));
            //holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
        } else if (ticketDTOSList.get(position).getStatus().equalsIgnoreCase("2")) {
            holder.tvStatus.setText(mContext.getResources().getString(R.string.close));
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            //holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
        }

        holder.rlClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, CommentOneByOne.class);
                in.putExtra(Consts.TICKET_ID, ticketDTOSList.get(position).getId());
                mContext.startActivity(in);
            }
        });
        /*
         ////***** This logic code for last item of recyclerview that behind the
          BottomApp Bar to show Upper side of the bottom Bar *****///
        if (position == getItemCount() - 1) {
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) holder.rlClick.getLayoutParams();
            params1.setMargins(0, 0, 0, 50);
            holder.rlClick.setLayoutParams(params1);
            //Toast.makeText(context, "Last Item", Toast.LENGTH_SHORT).show();
        } else {
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) holder.rlClick.getLayoutParams();
            params1.setMargins(0, 0, 0, 0);
            holder.rlClick.setLayoutParams(params1);
        }



    }

    @Override
    public int getItemCount() {

        return ticketDTOSList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTicket;
        public TextView tvDate, tvStatus;
        //public LinearLayout llStatus;
        public CardView rlClick;

        public MyViewHolder(View view) {
            super(view);
            //llStatus = view.findViewById(R.id.llStatus);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvTicket = view.findViewById(R.id.tvTicket);
            tvDate = view.findViewById(R.id.tvDate);
            rlClick = view.findViewById(R.id.rlClick);


        }
    }


}