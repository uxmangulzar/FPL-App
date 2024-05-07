package com.locksmith.fpl.Dentist.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.locksmith.fpl.Dentist.ui.activity.CreateStoreActivity;
import com.locksmith.fpl.Dentist.ui.fragment.bottomSheetFragments.NewStateFragment;
import com.locksmith.fpl.R;

import java.util.ArrayList;

public class ChooseStateAdapter extends RecyclerView.Adapter<ChooseStateAdapter.CustomViewHolder> {
    Context context;
    public String[] topicList;
    TextView topic_selection_txt;
    int selected_pos;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    boolean match_available_state = false;
    String open_from;
    private int previousPosition = 0;
    private RecyclerView mRecyclerList = null;
    BottomSheetDialog bottomSheetDialog;
    String[] available_state;

    public ChooseStateAdapter(Context context, RecyclerView mRecyclerList, String[] topicList, TextView topic_selection_txt, BottomSheetDialog bottomSheetDialog, String[] available_state) {
        this.context = context;
        this.mRecyclerList = mRecyclerList;
        this.topicList = topicList;
        this.open_from = open_from;
        this.topic_selection_txt = topic_selection_txt;
        this.bottomSheetDialog = bottomSheetDialog;
        this.available_state = available_state;
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
        String t_name = topicList[position];
        holder.topicName.setText("" + t_name);
        holder.ts_img.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < available_state.length; i++) {
                    if (available_state[i].equalsIgnoreCase(topicList[position])) {
                        match_available_state = true;
                        topic_selection_txt.setText("" + topicList[position]);
                        bottomSheetDialog.dismiss();
                        return;
                    } else {
                        match_available_state = false;
                    }
                }
                if (match_available_state) {
                } else {
                    newStateDialog();
                    bottomSheetDialog.dismiss();
                }


            }
        });
    }

    private void newStateDialog() {
        NewStateFragment newStateFragment = NewStateFragment.newInstance();
        newStateFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "newStateFragment");
    }

    @Override
    public int getItemCount() {
        return topicList.length;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView topicName;
        public ImageView ts_img;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            topicName = itemView.findViewById(R.id.t_txt);
            ts_img = itemView.findViewById(R.id.ts_img);
        }
    }
}