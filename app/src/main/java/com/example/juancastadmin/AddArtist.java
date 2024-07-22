package com.example.juancastadmin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juancastadmin.listadapters.AddedTagListAdapter;
import com.example.juancastadmin.listadapters.TagsListAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class AddArtist extends AppCompatActivity {



    public RecyclerView AA_AddedTagsListRecyclerView;
    public RecyclerView AA_TagListRecyclerView;
    public Button AA_BackButton;

    public ArrayList<String> tagList;
    public ArrayList<String> addedTagList;

    public TagsListAdapter tagsListAdapter;
    public AddedTagListAdapter addedTagListAdapter;


    public void initTagList()
    {
        String[] tags = getResources().getStringArray(R.array.tags);

        tagList.addAll(Arrays.asList(tags));

        tagsListAdapter = new TagsListAdapter(getApplicationContext(),tagList,this);
        addedTagListAdapter = new AddedTagListAdapter(getApplicationContext(),addedTagList,this);
        AA_TagListRecyclerView.setAdapter(tagsListAdapter);
        AA_TagListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        AA_AddedTagsListRecyclerView.setAdapter(addedTagListAdapter);
        AA_AddedTagsListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
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
    }

    public void removeFromTagList(int index){
        tagList.add(addedTagList.get(index));
        addedTagList.remove(index);
        refreshTagList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_artist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tagList = new ArrayList<>();
        addedTagList = new ArrayList<>();

        AA_AddedTagsListRecyclerView = findViewById(R.id.AA_AddedTagsListRecyclerView);
        AA_TagListRecyclerView = findViewById(R.id.AA_TagListRecyclerView);
        AA_BackButton = findViewById(R.id.AA_BackButton);


        AA_BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initTagList();

    }
}