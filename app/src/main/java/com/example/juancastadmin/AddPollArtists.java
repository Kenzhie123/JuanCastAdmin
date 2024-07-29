package com.example.juancastadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juancastadmin.listadapters.APAArtistListAdapter;
import com.example.juancastadmin.listadapters.AddedTagListAdapter;
import com.example.juancastadmin.listadapters.TagsListAdapter;
import com.example.juancastadmin.model.APAArtist;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class AddPollArtists extends AppCompatActivity {

    Button APA_BackButton;
    Button APA_AddPollButton;


    RecyclerView APA_AddedTagListRecyclerView;
    RecyclerView APA_TagListRecyclerView;
    RecyclerView APA_ArtistListRecyclerView;

    public ArrayList<String> tagList;
    public ArrayList<String> addedTagList;
    public ArrayList<APAArtist> artistList;
    public ArrayList<APAArtist> addedArtist;

    public TagsListAdapter tagsListAdapter;
    public AddedTagListAdapter addedTagListAdapter;
    public APAArtistListAdapter artistListAdapter;

    FirebaseFirestore db;

    AddPoll addPoll;

    public void initTagList()
    {
        String[] tags = getResources().getStringArray(R.array.tags);

        tagList = new ArrayList<>();
        addedTagList = new ArrayList<>();
        tagList.addAll(Arrays.asList(tags));

        tagsListAdapter = new TagsListAdapter(getApplicationContext(),tagList,this);
        addedTagListAdapter = new AddedTagListAdapter(getApplicationContext(),addedTagList,this);
        APA_TagListRecyclerView.setAdapter(tagsListAdapter);
        APA_TagListRecyclerView.setLayoutManager(new FlexboxLayoutManager(getApplicationContext(), FlexDirection.ROW));
        APA_AddedTagListRecyclerView.setAdapter(addedTagListAdapter);
        APA_AddedTagListRecyclerView.setLayoutManager(new FlexboxLayoutManager(getApplicationContext(),FlexDirection.ROW));
    }
    public void refreshTagList()
    {
        tagsListAdapter.notifyDataSetChanged();
        addedTagListAdapter.notifyDataSetChanged();
        refreshArtistList();
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

    public void initPollArtistsList()
    {
        artistList = new ArrayList<>();
        addedArtist = new ArrayList<>();
        db.collection("artists").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot document : task.getResult().getDocuments())
                    {
                        APAArtist artist = new APAArtist(
                                (String)document.getId(),
                                (String)document.getData().get("artist_name"),
                                (ArrayList<String>)document.getData().get("tags"),
                                true);
                        artistList.add(artist);
                        addedArtist.add(artist);
                    }

                    artistListAdapter = new APAArtistListAdapter(getApplicationContext(),addedArtist);
                    APA_ArtistListRecyclerView.setAdapter(artistListAdapter);
                    APA_ArtistListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    try{
                        if(getIntent().getExtras().get("artistsIDList") != null && getIntent().getExtras().get("tagList") != null)
                        {
                            setToUpdate(getIntent().getExtras().getStringArrayList("artistsIDList"),getIntent().getExtras().getStringArrayList("tagList"));
                        }
                    }catch (Exception e)
                    {
                        Log.d("DATATAG",e.getMessage());
                    }

                }
            }
        });
    }

    public void refreshArtistList()
    {
        if(!addedTagList.isEmpty())
        {
            addedArtist.clear();
            for(String tag : addedTagList)
            {
                for(APAArtist artist: artistList)
                {
                    for (String artistTag :artist.getTags())
                    {
                        if(tag.equals(artistTag))
                        {
                            if(!addedArtist.contains(artist))
                            {
                                addedArtist.add(artist);
                            }
                        }
                    }
                }
            }

        }
        else
        {
            addedArtist.clear();
            addedArtist.addAll(artistList);
        }
        artistListAdapter.notifyDataSetChanged();
    }

    public void setToUpdate(ArrayList<String> artistsIDList,ArrayList<String> updateTagList)
    {
        addedArtist.clear();
        APA_AddPollButton.setText("Update");
        for(APAArtist apaArtist : artistList)
        {
            for(String id :artistsIDList)
            {
                if(apaArtist.getArtistID().equals(id)){
                    addedArtist.add(apaArtist);
                }
            }

        }

        for(String tag : updateTagList)
        {
            tagList.remove(tag);
            addedTagList.add(tag);
        }
        tagsListAdapter.notifyDataSetChanged();
        addedTagListAdapter.notifyDataSetChanged();
        artistListAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_poll_artists);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        APA_AddedTagListRecyclerView = findViewById(R.id.APA_AddedTagListRecyclerView);
        APA_TagListRecyclerView = findViewById(R.id.APA_TagListRecyclerView);
        APA_ArtistListRecyclerView = findViewById(R.id.APA_ArtistListRecyclerView);
        APA_BackButton = findViewById(R.id.APA_BackButton);
        APA_AddPollButton = findViewById(R.id.APA_AddPollButton);



        APA_BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        APA_AddPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> artistIDListTemp = new ArrayList<>();
                for(APAArtist artist : addedArtist)
                {
                    if(artist.isChecked()){
                        artistIDListTemp.add(artist.getArtistID());
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("artistList",artistIDListTemp);
                intent.putExtra("addedTagList",addedTagList);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        initTagList();
        initPollArtistsList();


    }
}