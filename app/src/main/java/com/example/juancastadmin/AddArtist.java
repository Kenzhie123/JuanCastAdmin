package com.example.juancastadmin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.juancastadmin.listadapters.AddedTagListAdapter;
import com.example.juancastadmin.listadapters.TagsListAdapter;
import com.example.juancastadmin.model.Artist;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    public Button AA_DeleteArtistButton;
    public TextView AA_Title;
    public TextView AA_ArtistAddProfileImage;
    public TextView AA_Overlay;
    public ProgressBar AA_ProgressBar;
    public ImageView AA_ArtistProfileImage;
    public EditText AA_ArtistName;
    public ProgressBar AA_ProfileProgressBar;


    public ArrayList<String> tagList;
    public ArrayList<String> addedTagList;

    public TagsListAdapter tagsListAdapter;
    public AddedTagListAdapter addedTagListAdapter;


    public Bitmap artistProfile;
    boolean imageSet = false;
    boolean setUpdate = false;
    String artistID;
    Artist currentUpdateArtist;

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
        AA_TagListRecyclerView.setLayoutManager(new FlexboxLayoutManager(getApplicationContext(),FlexDirection.ROW));
        AA_AddedTagsListRecyclerView.setAdapter(addedTagListAdapter);
        AA_AddedTagsListRecyclerView.setLayoutManager(new FlexboxLayoutManager(getApplicationContext(),FlexDirection.ROW));
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

    public void addArtistToDB()
    {


        Map<String,Object> artistMap = new HashMap<>();
        artistMap.put("artist_name", AA_ArtistName.getText().toString().trim());
        artistMap.put("tags", addedTagList);

        db.collection("artists").add(artistMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    if(AA_ArtistProfileImage.getDrawable() != null)
                    {
                        if(artistProfile != null)
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
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Artist Added", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Failed to add artist", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateArtistInDB()
    {
        Map<String,Object> updateArtist = new HashMap<>();
        updateArtist.put("artist_name",AA_ArtistName.getText().toString().trim());
        updateArtist.put("tags",addedTagList);
        db.collection("artists").document(currentUpdateArtist.getArtistID()).update(updateArtist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Bitmap artistProfileBitmap = ((BitmapDrawable)AA_ArtistProfileImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    artistProfileBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    byte[] data = baos.toByteArray();
                    StorageReference ref = reference.child("artists/" + currentUpdateArtist.getArtistID());
                    ref.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Artist Updated", Toast.LENGTH_LONG).show();
                                disableProgress();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Failed to update artist", Toast.LENGTH_LONG).show();
                                disableProgress();
                            }
                        }
                    });
                }
            }
        });
    }

    public void deleteArtist()
    {
        enableProgress();
        db.collection("artists").document(currentUpdateArtist.getArtistID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    StorageReference ref = reference.child("artists/" + currentUpdateArtist.getArtistID());
                    ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Artist Successfully Deleted",Toast.LENGTH_LONG).show();
                                disableProgress();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Artist Successfully Deleted",Toast.LENGTH_LONG).show();
                                disableProgress();
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    public void setTagsToUpdate()
    {
        initTagList();

        ArrayList<String> copyTags = new ArrayList<>(tagList);
        for(String tag : copyTags){
            if(addedTagList.contains(tag))
            {
                tagList.remove(tag);
            }
        }
    }

    public void setToUpdate()
    {
        if(setUpdate)
        {
            AA_ProfileProgressBar.setVisibility(View.VISIBLE);
            AA_ArtistAddProfileImage.setVisibility(View.GONE);

            db.collection("artists").document(artistID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {

                        DocumentSnapshot data = task.getResult();
                        currentUpdateArtist = new Artist(data.getId(),(String)data.getData().get("artist_name"),(ArrayList<String>)data.getData().get("tags"));
                        addedTagList = (ArrayList<String>) data.getData().get("tags");
                        AA_ArtistName.setText(currentUpdateArtist.getArtistName());
                        StorageReference reference = storage.getReference().child("artists/" + currentUpdateArtist.getArtistID());
                        Glide.with(getApplicationContext()).load(reference).diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                        AA_ProfileProgressBar.setVisibility(View.GONE);
                                        AA_ArtistAddProfileImage.setVisibility(View.VISIBLE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                        AA_ArtistAddProfileImage.setVisibility(View.GONE);
                                        AA_ProfileProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(AA_ArtistProfileImage);

                        setTagsToUpdate();
                        AA_AddArtistButton.setText("UPDATE");
                        AA_Title.setText("Update Artist");
                    }
                }
            });
        }
    }




    public void disable(){

        AA_BackButton.setEnabled(false);
        AA_ArtistAddProfileImage.setEnabled(false);
        AA_ArtistName.setEnabled(false);
        AA_TagListRecyclerView.setEnabled(false);
        AA_TagListRecyclerView.setClickable(false);
        AA_AddedTagsListRecyclerView.setEnabled(false);
        AA_AddedTagsListRecyclerView.setClickable(false);
        AA_AddArtistButton.setEnabled(false);
    }

    public void enable(){
        AA_BackButton.setEnabled(true);
        AA_ArtistAddProfileImage.setEnabled(true);
        AA_ArtistName.setEnabled(true);
        AA_TagListRecyclerView.setEnabled(true);
        AA_TagListRecyclerView.setClickable(true);
        AA_AddedTagsListRecyclerView.setEnabled(true);
        AA_AddedTagsListRecyclerView.setClickable(true);
        AA_AddArtistButton.setEnabled(true);
    }

    public void enableProgress()
    {
        disable();
        AA_ProgressBar.setVisibility(View.VISIBLE);
        AA_Overlay.setVisibility(View.VISIBLE);
    }
    public void disableProgress()
    {
        enable();
        AA_ProgressBar.setVisibility(View.GONE);
        AA_Overlay.setVisibility(View.GONE);
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
        AA_DeleteArtistButton = findViewById(R.id.AA_DeleteArtistButton);
        AA_ArtistAddProfileImage = findViewById(R.id.AA_ArtistAddProfileImage);
        AA_ArtistProfileImage = findViewById(R.id.AA_ArtistProfileImage);
        AA_ArtistName = findViewById(R.id.AA_ArtistName);
        AA_ProfileProgressBar = findViewById(R.id.AA_ProfileProgressBar);
        AA_ProgressBar = findViewById(R.id.AA_ProgressBar);
        AA_Overlay = findViewById(R.id.AA_Overlay);
        AA_Title = findViewById(R.id.AA_Title);

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
        AA_DeleteArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddArtist.this);
                builder.setTitle("Delete Artist");
                builder.setMessage("Are you sure you want to delete this artist?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteArtist();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
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
                enableProgress();
                if(!setUpdate)
                {
                    addArtistToDB();
                }
                else
                {

                    updateArtistInDB();
                }
            }
        });

        if(getIntent().getExtras() != null)
        {
            setUpdate = getIntent().getExtras().getBoolean("isUpdate");
            artistID = getIntent().getExtras().getString("artistID");
            AA_DeleteArtistButton.setVisibility(View.VISIBLE);
            setToUpdate();
        }
        else
        {
            initTagList();
        }


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
                imageSet = true;
            }catch (Exception e)
            {
//                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

        }
    }
}