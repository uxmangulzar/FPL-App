package com.locksmith.fpl.Dentist.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.locksmith.fpl.R;
import com.locksmith.fpl.ui.activity.BookingDetailActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TechBookingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TechBookingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RelativeLayout bl;
    LinearLayout no_booking_lay;
    View tech_book_list_design;
    public TechBookingsFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyBookingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TechBookingsFragment newInstance(String param1, String param2) {
        TechBookingsFragment fragment = new TechBookingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tech_bookings, container, false);
        tech_book_list_design = view.findViewById(R.id.tech_book_list_design);
        no_booking_lay = view.findViewById(R.id.no_booking_lay);
        no_booking_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tech_book_list_design.setVisibility(View.VISIBLE);
                no_booking_lay.setVisibility(View.GONE);
                tech_book_list_design.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tech_book_list_design.setVisibility(View.GONE);
                        no_booking_lay.setVisibility(View.VISIBLE);
                        startActivity(new Intent(getContext(), BookingDetailActivity.class));
                    }
                });
            }
        });
        return view;
    }
}