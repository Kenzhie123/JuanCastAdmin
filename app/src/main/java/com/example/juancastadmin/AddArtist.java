package com.example.juancastadmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juancastadmin.listadapters.AddedTagListAdapter;
import com.example.juancastadmin.listadapters.TagsListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddArtist extends AppCompatActivity {



    public RecyclerView AA_AddedTagsListRecyclerView;
    public RecyclerView AA_TagListRecyclerView;
    public Button AA_BackButton;
    public Button AA_AddArtistButton;
    public TextView AA_ArtistAddProfileImage;
    public ImageView AA_ArtistProfileImage;
    public EditText AA_ArtistName;

    public ArrayList<String> tagList;
    public ArrayList<String> addedTagList;

    public TagsListAdapter tagsListAdapter;
    public AddedTagListAdapter addedTagListAdapter;


    public Bitmap artistProfile;
    boolean imageUpdated = false;

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference reference;


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

    public void addUserToDB()
    {


        Map<String,Object> userMap = new HashMap<>();
        userMap.put("artist_name", AA_ArtistName.getText().toString().trim());
        userMap.put("tags", addedTagList);

        db.collection("artists").add(userMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    if(AA_ArtistProfileImage.getDrawable() != null)
                    {
                        DocumentReference docRef = task.getResult();
                        Bitmap artistProfileBitmap = ((BitmapDrawable)AA_ArtistProfileImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        artistProfileBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                        byte[] data = baos.toByteArray();
                        StorageReference ref = reference.child("artists/" + docRef.getId());
                        ref.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"Artist Added", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Failed to add artist", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Failed to add artist", Toast.LENGTH_LONG).show();
                }
            }
        });
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
        AA_AddArtistButton = findViewById(R.id.AA_AddArtistButton);
        AA_ArtistAddProfileImage = findViewById(R.id.AA_ArtistAddProfileImage);
        AA_ArtistProfileImage = findViewById(R.id.AA_ArtistProfileImage);
        AA_ArtistName = findViewById(R.id.AA_ArtistName);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();

        AA_BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AA_ArtistAddProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });

        AA_ArtistProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });

        AA_AddArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserToDB();
            }
        });

        initTagList();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10)
        {

            try{
                Uri uri = data.getData();
                artistProfile = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                AA_ArtistProfileImage.setImageBitmap(artistProfile);
                AA_ArtistAddProfileImage.setVisibility(View.GONE);
                imageUpdated = true;
            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

        }
    }
}