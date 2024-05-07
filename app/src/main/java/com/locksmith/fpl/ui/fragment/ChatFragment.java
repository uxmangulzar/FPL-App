package com.locksmith.fpl.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.locksmith.fpl.ui.activity.ChatActivity;
import com.locksmith.fpl.R;


public class ChatFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String OPEN_FRAGMENT = "open_fragment";

    // TODO: Rename and change types of parameters
    private String OP_USF;
    private RelativeLayout nbt_layout, inbox_layout;
    private View nbt_line, inbox_line;
    private TextView nbt_txt, inbox_txt;
    private Context context;
    private boolean tech_sel_frg = false, inbox_sel_frg = false;
    NearByTechFragment nearByTechFragment;
    InboxFragment inboxFragment;
    public ChatFragment() {
    }

    public ChatFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    public static ChatFragment newInstance(String param1) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(OPEN_FRAGMENT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            OP_USF = getArguments().getString(OPEN_FRAGMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        nbt_layout = view.findViewById(R.id.nbt_rel_layout);
        inbox_layout = view.findViewById(R.id.inbox_rel_layout);
        nbt_line = view.findViewById(R.id.nbt_line);
        nbt_txt = view.findViewById(R.id.nbtt);
        inbox_line = view.findViewById(R.id.inbox_line);
        inbox_txt = view.findViewById(R.id.inbox_txt);
        nbt_layout.setOnClickListener(this::onClick);
        inbox_layout.setOnClickListener(this::onClick);
        nearByTechFragment = new NearByTechFragment();
        inboxFragment = new InboxFragment();
        ///*** 0 for Technician fragment
        if (OP_USF.equals("0")) {
            tech_sel_frg = true;
            inbox_sel_frg = false;
            nbt_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
            inbox_txt.setTextColor(getResources().getColor(R.color.heading_top));
            nbt_line.setVisibility(View.VISIBLE);
            inbox_line.setVisibility(View.GONE);
            loadFragment(nearByTechFragment, "NRB_TF");
        }
        ///*** 1 for Inbox fragment
        else if (OP_USF.equals("1")) {
            tech_sel_frg = false;
            inbox_sel_frg = true;
            nbt_txt.setTextColor(getResources().getColor(R.color.heading_top));
            inbox_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
            nbt_line.setVisibility(View.GONE);
            inbox_line.setVisibility(View.VISIBLE);
            loadFragment(inboxFragment, "INB_TF");
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nbt_rel_layout:
                if (tech_sel_frg) {
                } else {
                    tech_sel_frg = true;
                    inbox_sel_frg = false;
                    nbt_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                    inbox_txt.setTextColor(getResources().getColor(R.color.heading_top));
                    nbt_line.setVisibility(View.VISIBLE);
                    inbox_line.setVisibility(View.GONE);
                    loadFragment(nearByTechFragment, "NRB_TF");
                }
                break;
            case R.id.inbox_rel_layout:
                if (inbox_sel_frg) {
                } else {
                    tech_sel_frg = false;
                    inbox_sel_frg = true;
                    nbt_txt.setTextColor(getResources().getColor(R.color.heading_top));
                    inbox_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                    nbt_line.setVisibility(View.GONE);
                    inbox_line.setVisibility(View.VISIBLE);
                    loadFragment(inboxFragment, "INB_TF");
                }
                break;
        }
    }

    private void loadFragment(Fragment fragment, String f_Tag) {
        if (fragment != null) {
            Bundle b = new Bundle();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container_chat, fragment, f_Tag);
            transaction.commit();
        }
    }
}