package com.example.juancastadmin.listadapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.juancastadmin.AddPoll;
import com.example.juancastadmin.R;
import com.example.juancastadmin.helper.Tools;
import com.example.juancastadmin.model.Poll;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PollListAdapter extends RecyclerView.Adapter<PollListAdapter.PollListViewHolder> {

    Context context;
    ArrayList<Poll> pollList;
    FirebaseStorage storage;
    public PollListAdapter(Context context, ArrayList<Poll> pollList)
    {
        this.context = context;
        this.pollList = pollList;

        storage = FirebaseStorage.getInstance();
    }


    @NonNull
    @Override
    public PollListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PollListViewHolder(LayoutInflater.from(context).inflate(R.layout.polllist_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PollListViewHolder holder, int position) {
        Poll specPoll = pollList.get(holder.getAdapterPosition());
        StorageReference storageRef = storage.getReference();
        StorageReference reference = storageRef.child("voting_poll_banners").child(specPoll.getId());

        holder.PLI_BannerProgressBar.setVisibility(View.VISIBLE);
        Glide.with(context).load(reference).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                holder.PLI_BannerProgressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                holder.PLI_BannerProgressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.PLI_Banner);

        holder.PLI_Title.setText(specPoll.getTitle());
        String dateRange = Tools.dateToString(specPoll.getDateFrom(),"MMMM d, yyyy") + "-" + Tools.dateToString(specPoll.getDateTo(),"MMMM d, yyyy");
        holder.PLI_DateRange.setText(dateRange);

        String artistCount = specPoll.getArtistIDList().size() + " Artists";
        holder.PLI_ArtistCount.setText(artistCount);


        holder.PLI_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddPoll.class);
                intent.putExtra("pollID",specPoll.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pollList.size();
    }

    public class PollListViewHolder extends RecyclerView.ViewHolder{
        ImageView PLI_Banner;
        TextView PLI_Title;
        TextView PLI_DateRange;
        TextView PLI_ArtistCount;
        ProgressBar PLI_BannerProgressBar;
        Button PLI_Edit;
        public PollListViewHolder(@NonNull View itemView) {
            super(itemView);
            PLI_Banner = itemView.findViewById(R.id.PLI_Banner);
            PLI_Title = itemView.findViewById(R.id.PLI_Title);
            PLI_DateRange = itemView.findViewById(R.id.PLI_DateRange);
            PLI_ArtistCount = itemView.findViewById(R.id.PLI_ArtistCount);
            PLI_BannerProgressBar = itemView.findViewById(R.id.PLI_BannerProgressBar);
            PLI_Edit = itemView.findViewById(R.id.PLI_Edit);
        }
    }
}
