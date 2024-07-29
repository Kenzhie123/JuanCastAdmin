package com.example.juancastadmin;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.juancastadmin.helper.Tools;
import com.example.juancastadmin.model.APAArtist;
import com.example.juancastadmin.model.Poll;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddPoll extends AppCompatActivity{

    Button AP_BackButton;
    Button AP_NextButton;
    TextView AP_DatePickerFrom;
    TextView AP_DatePickerTo;
    ImageView AP_BannerImage;
    Button AP_AddBannerImageButton;
    EditText AP_PollTitle;
    EditText AP_PollNote;
    TextView APA_Overlay;
    ProgressBar APA_ProgressBar;
    ProgressBar AP_BannerProgressBar;

    Calendar calendarDateFrom;
    Calendar calendarDateTo;
    Date dateNow = Calendar.getInstance().getTime();

    FirebaseFirestore db;
    FirebaseStorage storage;
    Bitmap banner;
    Poll pollToEdit;

    public void addPoll(ArrayList<String> artistList,ArrayList<String> addedTagList)
    {
        enableProgress();
        Map<String, Object> pollMap = new HashMap<>();
        pollMap.put("poll_title",AP_PollTitle.getText().toString().trim());
        pollMap.put("date_from", Tools.dateToString(calendarDateFrom.getTime()));
        pollMap.put("date_to", Tools.dateToString(calendarDateTo.getTime()));
        pollMap.put("note",AP_PollNote.getText().toString().trim());
        pollMap.put("artists",artistList);
        pollMap.put("tag_list",addedTagList);



        db.collection("voting_polls").add(pollMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    final int[] count = {1};
                    for(String artistID: artistList)
                    {
                        Map<String,Object> artistVotes = new HashMap<>();
                        artistVotes.put("sun_votes", 0);
                        artistVotes.put("star_votes",0);
                        db.collection("voting_polls")
                                .document(task.getResult().getId())
                                .collection("votes")
                                .document(artistID)
                                .set(artistVotes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if(count[0] == artistList.size())
                                        {
                                            StorageReference reference = storage.getReference();
                                            StorageReference bannerReference = reference.child("voting_poll_banners").child(task.getResult().getId());
                                            Bitmap pollBannerBitmap = ((BitmapDrawable)AP_BannerImage.getDrawable()).getBitmap();
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            pollBannerBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                                            byte[] data = baos.toByteArray();
                                            bannerReference.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(getApplicationContext(),"Poll successfully added",Toast.LENGTH_LONG).show();
                                                        disableProgress();
                                                        finish();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(getApplicationContext(),"Poll adding unsuccessful: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                        disableProgress();
                                                    }
                                                }
                                            });
                                        }
                                        else
                                        {
                                            count[0]++;
                                        }
                                    }
                                });

                    }

                }
            }
        });
    }


    public void updatePoll(ArrayList<String> artistList,ArrayList<String> addedTagList)
    {
        enableProgress();
        Map<String, Object> pollMap = new HashMap<>();
        pollMap.put("poll_title",AP_PollTitle.getText().toString().trim());
        pollMap.put("date_from", Tools.dateToString(calendarDateFrom.getTime()));
        pollMap.put("date_to", Tools.dateToString(calendarDateTo.getTime()));
        pollMap.put("note",AP_PollNote.getText().toString().trim());
        pollMap.put("artists",artistList);
        pollMap.put("tag_list",addedTagList);

        db.collection("voting_polls").document(pollToEdit.getId()).update(pollMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    StorageReference reference = storage.getReference();
                    StorageReference bannerReference = reference.child("voting_poll_banners").child(pollToEdit.getId());
                    Bitmap pollBannerBitmap = ((BitmapDrawable)AP_BannerImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    pollBannerBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    byte[] data = baos.toByteArray();
                    bannerReference.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {

                                Toast.makeText(getApplicationContext(),"Poll successfully updated",Toast.LENGTH_LONG).show();
                                disableProgress();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Poll update unsuccessful: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                disableProgress();
                            }
                        }
                    });
                }
            }
        });
    }

    public void setToEdit(String pollID)
    {
        AP_AddBannerImageButton.setVisibility(View.GONE);
        AP_BannerProgressBar.setVisibility(View.VISIBLE);
        db.collection("voting_polls").document(pollID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if(doc != null)
                    {
                        Map<String,Object> data = doc.getData();


                        pollToEdit = new Poll(doc.getId(),
                                (String)data.get("poll_title"),
                                Tools.StringToDate((String)data.get("date_from")),
                                Tools.StringToDate((String)data.get("date_to")),
                                (String)data.get("note"),
                                (ArrayList<String>)data.get("artists"),
                                (ArrayList<String>)data.get("tag_list"));

                        Log.d("DATATAG", (String) data.get("poll_title"));
                        Log.d("DATATAG",pollToEdit.toString());
                        calendarDateFrom = Calendar.getInstance();
                        calendarDateTo = Calendar.getInstance();
                        calendarDateFrom.setTime(pollToEdit.getDateFrom());
                        calendarDateTo.setTime(pollToEdit.getDateTo());
                        AP_PollTitle.setText(pollToEdit.getTitle());
                        AP_DatePickerFrom.setText(Tools.dateToString(pollToEdit.getDateFrom()));
                        AP_DatePickerTo.setText(Tools.dateToString(pollToEdit.getDateTo()));
                        AP_PollNote.setText(pollToEdit.getNote());
                        storage = FirebaseStorage.getInstance();
                        StorageReference reference = storage.getReference().child("voting_poll_banners").child(pollToEdit.getId());

                        Glide.with(getApplicationContext()).load(reference).diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                        AP_AddBannerImageButton.setVisibility(View.VISIBLE);
                                        AP_BannerProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                        AP_AddBannerImageButton.setVisibility(View.GONE);
                                        AP_BannerProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(AP_BannerImage);

                    }

                }
            }
        });
    }

    public void enable()
    {
        AP_BackButton.setEnabled(true);
        AP_NextButton.setEnabled(true);
        AP_DatePickerFrom.setEnabled(true);
        AP_DatePickerTo.setEnabled(true);
        AP_BannerImage.setEnabled(true);
        AP_AddBannerImageButton.setEnabled(true);
        AP_PollTitle.setEnabled(true);
        AP_PollNote.setEnabled(true);
    }

    public void disable()
    {
        AP_BackButton.setEnabled(false);
        AP_NextButton.setEnabled(false);
        AP_DatePickerFrom.setEnabled(false);
        AP_DatePickerTo.setEnabled(false);
        AP_BannerImage.setEnabled(false);
        AP_AddBannerImageButton.setEnabled(false);
        AP_PollTitle.setEnabled(false);
        AP_PollNote.setEnabled(false);
    }

    public void enableProgress()
    {
        disable();
        APA_Overlay.setVisibility(View.VISIBLE);
        APA_ProgressBar.setVisibility(View.VISIBLE);
    }

    public void disableProgress()
    {
        enable();
        APA_Overlay.setVisibility(View.GONE);
        APA_ProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_poll);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        AP_BackButton = findViewById(R.id.AP_BackButton);
        AP_NextButton = findViewById(R.id.AP_NextButton);
        AP_DatePickerFrom = findViewById(R.id.AP_DatePickerFrom);
        AP_DatePickerTo = findViewById(R.id.AP_DatePickerTo);
        AP_AddBannerImageButton = findViewById(R.id.AP_AddBannerImageButton);
        AP_BannerImage = findViewById(R.id.AP_BannerImage);
        AP_PollTitle = findViewById(R.id.AP_PollTitle);
        AP_PollNote = findViewById(R.id.AP_PollNote);
        APA_Overlay = findViewById(R.id.APA_Overlay);
        APA_ProgressBar = findViewById(R.id.APA_ProgressBar);
        AP_BannerProgressBar = findViewById(R.id.AP_BannerProgressBar);

        try{
            if(getIntent().getExtras().get("pollID") != null)
            {
                setToEdit(getIntent().getExtras().getString("pollID"));
            }
        }catch (Exception e)
        {

        }




        AP_BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AP_DatePickerFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddPoll.this);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        Calendar calendarDateFromTemp = Calendar.getInstance();
                        calendarDateFromTemp.set(year,month,dayOfMonth);

                        if(dateNow.before(calendarDateFromTemp.getTime()))
                        {
                            boolean changeDate = false;
                            if(calendarDateTo == null)
                            {
                                changeDate = true;
                            }
                            else
                            {
                                Date dateFrom = calendarDateFromTemp.getTime();
                                Date dateTo = calendarDateTo.getTime();

                                if(!dateFrom.before(dateTo))
                                {
                                    Toast.makeText(getApplicationContext(),"Date from cannot be later than date to", Toast.LENGTH_LONG).show();
                                }
                                else
                                {

                                    changeDate = true;
                                }
                            }


                            if(changeDate)
                            {
                                calendarDateFrom = calendarDateFromTemp;
                                AP_DatePickerFrom.setText(month+"/"+dayOfMonth+"/"+year);
                                AP_DatePickerFrom.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.text_color_dark));
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Cannot set date in the past", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                datePickerDialog.show();
            }
        });

        AP_DatePickerTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddPoll.this);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        Calendar calendarDateToTemp = Calendar.getInstance();
                        calendarDateToTemp.set(year,month,dayOfMonth);
                        if(dateNow.before(calendarDateToTemp.getTime())) {
                            boolean changeDate = false;
                            if (calendarDateFrom == null) {
                                changeDate = true;
                            } else {
                                Date dateFrom = calendarDateFrom.getTime();
                                Date dateTo = calendarDateToTemp.getTime();

                                if (!dateFrom.before(dateTo)) {
                                    Toast.makeText(getApplicationContext(), "Date from cannot be later than date to", Toast.LENGTH_LONG).show();
                                } else {
                                    calendarDateTo = calendarDateToTemp;
                                    changeDate = true;
                                }
                            }
                            if (changeDate) {
                                calendarDateTo = calendarDateToTemp;
                                AP_DatePickerTo.setText(month + "/" + dayOfMonth + "/" + year);
                                AP_DatePickerTo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_color_dark));
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Cannot set date in the past", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                datePickerDialog.show();
            }
        });


        AP_AddBannerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });

        AP_BannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });

        AP_NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddPollArtists.class);
                if(pollToEdit != null)
                {
                    Log.d("DATATAG","called");
                    intent.putExtra("artistsIDList",pollToEdit.getArtistIDList());
                    intent.putExtra("tagList",pollToEdit.getTagList());
                }
                startActivityForResult(intent,11);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10)
        {
            try{
                Uri uri = data.getData();
                banner = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                AP_BannerImage.setImageBitmap(banner);
                AP_AddBannerImageButton.setVisibility(View.GONE);
            }catch (Exception e )
            {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

        }
        if(requestCode == 11)
        {
            if(data != null && data.getStringArrayListExtra("artistList") != null && data.getStringArrayListExtra("addedTagList") != null)
            {
                ArrayList<String> artistList = (ArrayList<String>) data.getStringArrayListExtra("artistList");
                ArrayList<String> tagList = (ArrayList<String>) data.getStringArrayListExtra("addedTagList");
                if(pollToEdit != null)
                {
                    updatePoll(artistList,tagList);
                }
                else
                {
                    addPoll(artistList,tagList);
                }

            }

        }
    }


}