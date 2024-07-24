package com.example.juancastadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PollFragment extends Fragment {


    public PollFragment() {
        // Required empty public constructor
    }

    Button P_AddPollButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_poll, container, false);


        P_AddPollButton = v.findViewById(R.id.P_AddPollButton);


        P_AddPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),AddPoll.class);
                startActivity(intent);
            }
        });

        return v;
    }
}