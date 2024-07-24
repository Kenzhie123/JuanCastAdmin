package com.example.juancastadmin.listadapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.juancastadmin.AddArtist;
import com.example.juancastadmin.R;
import com.example.juancastadmin.model.Artist;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtistListViewHolder>{

    FirebaseStorage storage;
    public ArrayList<Artist> artistList;
    public Context context;

    public ArtistListAdapter(Context context, ArrayList<Artist> artistList )
    {
        this.context = context;
        this.artistList = artistList;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ArtistListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistListViewHolder(LayoutInflater.from(context).inflate(R.layout.artist_recyclerview_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistListViewHolder holder, int position) {

        Artist currentArtist = artistList.get(holder.getAdapterPosition());
        StorageReference reference = storage.getReference().child("artists/" + currentArtist.getArtistID());
        String tagsText = "";

        for(String tag : currentArtist.getTags())
        {
            tagsText = (tagsText.equals("") ? tag : tagsText + ", " + tag);
        }

        holder.ARI_Name.setText(currentArtist.getArtistName());
        holder.ARI_Tags.setText(tagsText);

        Glide.with(context).load(reference).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                Log.d("DataTag",e.getMessage());
                return false;
            }

            @Override
            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                Log.d("DataTag","done");
                return false;
            }
        }).into(holder.ARI_ProfileImage);

        holder.ARI_Background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent update = new Intent(context, AddArtist.class);
                update.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                update.putExtra("isUpdate",true);
                update.putExtra("artistID",currentArtist.getArtistID());
                Log.d("DataTag",currentArtist.getArtistID());
                context.startActivity(update);
            }
        });
    }

    @Override
    public int getItemCount() {

        return artistList.size();
    }

    public class ArtistListViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout ARI_Background;
        TextView ARI_Name;
        ImageView ARI_ProfileImage;
        TextView ARI_Tags;
        public ArtistListViewHolder(@NonNull View itemView) {
            super(itemView);

            ARI_Name = itemView.findViewById(R.id.ARI_Name);
            ARI_ProfileImage = itemView.findViewById(R.id.ARI_ProfileImage);
            ARI_Tags = itemView.findViewById(R.id.ARI_Tags);
            ARI_Background = itemView.findViewById(R.id.ARI_Background);
        }
    }
}
