package com.locksmith.fpl.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.locksmith.fpl.Dentist.ui.activity.CreateStoreActivity;
import com.locksmith.fpl.R;

import java.util.ArrayList;

public class ChooseTopicAdapter extends RecyclerView.Adapter<ChooseTopicAdapter.CustomViewHolder> {
    Context context;
    public ArrayList<String> topicList;
    TextView topic_selection_txt;
    int selected_pos;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    boolean click = false;
    String open_from;
    private int previousPosition = 0;
    private RecyclerView mRecyclerList = null;

    public ChooseTopicAdapter(Context context, RecyclerView mRecyclerList, ArrayList<String> topicList, TextView topic_selection_txt, String open_from) {
        this.context = context;
        this.mRecyclerList = mRecyclerList;
        this.topicList = topicList;
        this.open_from = open_from;
        this.topic_selection_txt = topic_selection_txt;
        preferences = context.getSharedPreferences("TS_PREF", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_topic_selection, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        String t_name = topicList.get(position);
        holder.topicName.setText("" + t_name);
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView topicName;
        public ImageView ts_img;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            topicName = itemView.findViewById(R.id.t_txt);
            ts_img = itemView.findViewById(R.id.ts_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imageView = null;
                    View view = null;
                    if (getAdapterPosition() == previousPosition) {
                        view = mRecyclerList.findViewHolderForAdapterPosition(previousPosition).itemView;
                        imageView = (ImageView) view.findViewById(R.id.ts_img);
                        imageView.setImageResource(R.drawable.ic_check);
                        previousPosition = getAdapterPosition();
                        topic_selection_txt.setText("" + topicList.get(getAdapterPosition()));
                        if (open_from.equals("0")) {
                            if (CreateStoreActivity.getInstance() != null) {
                                CreateStoreActivity.getInstance().setServicesName("" + topic_selection_txt.getText().toString());
                            }
                        }
                    } else {
                        topic_selection_txt.setText("" + topicList.get(getAdapterPosition()));
                        view = mRecyclerList.findViewHolderForAdapterPosition(previousPosition).itemView;
                        imageView = (ImageView) view.findViewById(R.id.ts_img);
                        imageView.setImageResource(R.drawable.ts_img);
                        view = mRecyclerList.findViewHolderForAdapterPosition(getAdapterPosition()).itemView;
                        imageView = (ImageView) view.findViewById(R.id.ts_img);
                        imageView.setImageResource(R.drawable.ic_check);
                        previousPosition = getAdapterPosition();
                        if (open_from.equals("0")) {
                            if (CreateStoreActivity.getInstance() != null) {
                                CreateStoreActivity.getInstance().setServicesName("" + topic_selection_txt.getText().toString());
                            }
                        }
                    }
                }
            });
        }
    }
}