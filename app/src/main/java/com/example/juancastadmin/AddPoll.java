package com.example.juancastadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
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

import com.example.juancastadmin.helper.Tools;
import com.example.juancastadmin.model.APAArtist;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
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

    Calendar calendarDateFrom;
    Calendar calendarDateTo;
    Date dateNow = Calendar.getInstance().getTime();


    FirebaseFirestore db;
    FirebaseStorage storage;
    Bitmap banner;

    public void addPoll(ArrayList<String> artistList)
    {
        enableProgress();
        Map<String, Object> pollMap = new HashMap<>();
        pollMap.put("poll_title",AP_PollTitle.getText().toString().trim());
        pollMap.put("date_from", Tools.dateToString(calendarDateFrom.getTime()));
        pollMap.put("date_to", Tools.dateToString(calendarDateTo.getTime()));
        pollMap.put("note",AP_PollNote.getText().toString().trim());
        pollMap.put("artists",artistList);


        db.collection("voting_polls").add(pollMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
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
                                AP_DatePickerFrom.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.normal_text_color));
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
                                AP_DatePickerTo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.normal_text_color));
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
            ArrayList<String> artistList = (ArrayList<String>) data.getStringArrayListExtra("artistList");
            addPoll(artistList);
        }
    }


}