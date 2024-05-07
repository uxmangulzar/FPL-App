package com.locksmith.fpl.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.locksmith.fpl.DTO.TicketCommentDTO;
import com.locksmith.fpl.DTO.UserDTO;
import com.locksmith.fpl.R;
import com.locksmith.fpl.utils.ProjectUtils;
import java.util.ArrayList;

/**
 * Created by Usman on 1/10/21.
 */

public class AdapterViewCommentTicket extends BaseAdapter {
    private Context mContext;
    private ArrayList<TicketCommentDTO> ticketCommentDTOSList;
    private UserDTO userDTO;

    public AdapterViewCommentTicket(Context mContext, ArrayList<TicketCommentDTO> ticketCommentDTOSList, UserDTO userDTO) {
        this.mContext = mContext;
        this.ticketCommentDTOSList = ticketCommentDTOSList;
        this.userDTO = userDTO;

    }

    @Override
    public int getCount() {
        return ticketCommentDTOSList.size();
    }

    @Override
    public Object getItem(int postion) {
        return ticketCommentDTOSList.get(postion);
    }

    @Override
    public long getItemId(int postion) {
        return postion;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        //ViewHolder holder = new ViewHolder();
        if (ticketCommentDTOSList.get(position).getRole() != userDTO.getRole()) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_view_comment, parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_view_comment_my, parent, false);

        }

        TextView textViewMessage = (TextView) view.findViewById(R.id.textViewMessage);
        TextView textViewTime = (TextView) view.findViewById(R.id.textViewTime);
        TextView tvName = (TextView) view.findViewById(R.id.tvName);

        textViewMessage.setText(ticketCommentDTOSList.get(position).getComment());
        tvName.setText(ticketCommentDTOSList.get(position).getUserName());

        try{
            textViewTime.setText(ProjectUtils.convertTimestampDateToTime(ProjectUtils.correctTimestamp(Long.parseLong(ticketCommentDTOSList.get(position).getCreated_at()))));

        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

}
