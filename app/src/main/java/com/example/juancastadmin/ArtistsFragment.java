package com.example.juancastadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.juancastadmin.model.Artist;
import com.example.juancastadmin.listadapters.ArtistListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ArtistsFragment extends Fragment {

    public ArtistsFragment() {
        // Required empty public constructor
    }

    FirebaseFirestore db;
    ArrayList<Artist> artistList;

    RecyclerView A_ArtistRecyclerView;
    Button A_AddArtistButton;


    public void setArtistList()
    {
        artistList = new ArrayList<>();
        db.collection("artists").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for(DocumentSnapshot d : documents)
                    {
                        Map<String,Object> dataInfo = d.getData();
                        Artist artist = new Artist( (String)dataInfo.get("artist_name"),(ArrayList<String>) dataInfo.get("tags"),d.getId());
                        Log.d("DataTag",artist.toString());
                        artistList.add(artist);
                    }

                    A_ArtistRecyclerView.setAdapter(new ArtistListAdapter(getActivity().getApplicationContext(),artistList));
                    A_ArtistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

                }
            }
        });



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artists, container, false);
        db = FirebaseFirestore.getInstance();


        A_ArtistRecyclerView = v.findViewById(R.id.A_ArtistRecyclerView);
        A_AddArtistButton = v.findViewById(R.id.A_AddArtistButton);


        A_AddArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),AddArtist.class);
                startActivity(intent);
            }
        });

        setArtistList();

        return v;
    }
}