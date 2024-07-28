package com.example.juancastadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.juancastadmin.helper.Tools;
import com.example.juancastadmin.listadapters.AddedTagListAdapter;
import com.example.juancastadmin.listadapters.PollListAdapter;
import com.example.juancastadmin.listadapters.TagsListAdapter;
import com.example.juancastadmin.model.Artist;
import com.example.juancastadmin.model.Poll;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class PollFragment extends Fragment {


    public PollFragment() {
        // Required empty public constructor
    }

    Button P_AddPollButton;
    EditText P_SearchField;
    RecyclerView P_TagListRecyclerView;
    RecyclerView P_AddedTagListRecyclerView;
    RecyclerView P_PollListRecyclerView;
    SwipeRefreshLayout P_RefreshLayout;

    FirebaseFirestore db;

    public ArrayList<Poll> pollList;
    public ArrayList<Poll> displayedPollList;
    public PollListAdapter pollListAdapter;

    public ArrayList<String> tagList;
    public ArrayList<String> addedTagList;
    public TagsListAdapter tagsListAdapter;
    public AddedTagListAdapter addedTagListAdapter;
    public void initTagList()
    {
        String[] tags = getResources().getStringArray(R.array.tags);
        tagList = new ArrayList<>();
        addedTagList = new ArrayList<>();
        tagList.addAll(Arrays.asList(tags));

        tagsListAdapter = new TagsListAdapter(getActivity().getApplicationContext(),tagList,this);
        addedTagListAdapter = new AddedTagListAdapter(getActivity().getApplicationContext(),addedTagList,this);
        P_TagListRecyclerView.setAdapter(tagsListAdapter);
        P_TagListRecyclerView.setLayoutManager(new FlexboxLayoutManager(getActivity().getApplicationContext(), FlexDirection.ROW));
        P_AddedTagListRecyclerView.setAdapter(addedTagListAdapter);
        P_AddedTagListRecyclerView.setLayoutManager(new FlexboxLayoutManager(getActivity().getApplicationContext(),FlexDirection.ROW));
    }
    public void refreshTagList()
    {
        tagsListAdapter.notifyDataSetChanged();
        addedTagListAdapter.notifyDataSetChanged();
    }
    public void addToTagList(int index)
    {
        addedTagList.add(tagList.get(index));
        tagList.remove(index);
        refreshTagList();
        filterPollList();
    }
    public void removeFromTagList(int index){
        tagList.add(addedTagList.get(index));
        addedTagList.remove(index);
        refreshTagList();
        filterPollList();
    }



    public void initPollList()
    {
        try{
            pollList = new ArrayList<>();
            displayedPollList = new ArrayList<>();
            P_RefreshLayout.setRefreshing(true);
            db.collection("voting_polls").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot doc : task.getResult().getDocuments())
                        {
                            Poll poll = new Poll(
                                    doc.getId(),
                                    (String)doc.get("poll_title"),
                                    Tools.StringToDate((String)doc.get("date_from")),
                                    Tools.StringToDate((String)doc.get("date_to")),
                                    (String)doc.get("note"),
                                    (ArrayList<String>)doc.get("artists"),
                                    (ArrayList<String>)doc.get("tag_list"));
                            pollList.add(poll);
                        }

                        displayedPollList.addAll(pollList);

                        try{
                            pollListAdapter = new PollListAdapter(getActivity().getApplicationContext(),displayedPollList);
                            P_PollListRecyclerView.setAdapter(pollListAdapter);
                            P_PollListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false));
                            filterPollList();
                        }catch (Exception e)
                        {
                            Log.d("DATATAG",e.getMessage());
                        }

                    }
                }
            });
        }catch (Exception e)
        {

        }

    }

    public void filterPollList()
    {

        if(!addedTagList.isEmpty())
        {
            displayedPollList.clear();
            for(String tag : addedTagList)
            {
                for(Poll poll : pollList)
                {
                    if(poll.getTagList().contains(tag))
                    {
                        displayedPollList.add(poll);
                    }
                }
            }

        }
        else
        {
            for(Poll poll : pollList)
            {
                if(!displayedPollList.contains(poll))
                {
                    displayedPollList.add(poll);
                }
            }
        }
        pollListAdapter.notifyDataSetChanged();
        P_RefreshLayout.setRefreshing(false);


    }

    public void setToSearch(String search)
    {
        displayedPollList.clear();
        for(Poll poll : pollList)
        {
            if(poll.getTitle().toLowerCase().contains(search.toLowerCase()))
            {
                displayedPollList.add(poll);
            }
        }
        pollListAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_poll, container, false);


        P_AddPollButton = v.findViewById(R.id.P_AddPollButton);
        P_TagListRecyclerView = v.findViewById(R.id.P_TagListRecyclerView);
        P_AddedTagListRecyclerView = v.findViewById(R.id.P_AddedTagListRecyclerView);
        P_PollListRecyclerView = v.findViewById(R.id.P_PollListRecyclerView);
        P_RefreshLayout = v.findViewById(R.id.P_RefreshLayout);
        P_SearchField = v.findViewById(R.id.P_SearchField);

        db = FirebaseFirestore.getInstance();
        initTagList();
        initPollList();


        P_AddPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),AddPoll.class);
                startActivity(intent);
            }
        });

        P_RefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initPollList();
            }
        });

        P_SearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setToSearch(s.toString());
            }
        });

        return v;
    }
}