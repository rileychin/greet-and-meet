package com.example.greetmeet_v1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimeZone;



public class EventDetails extends AppCompatActivity {
    EditText eventName,eventDesc,eventHost,eventLocation,eventDate;
    ImageView eventImage, newEventImage;
    RecyclerView listofusers;
    Uri mImageUri;
    ProgressBar progressBar;
    DatabaseReference ref,ref2;
    ArrayList<Group> list;
    ArrayList<String> Listofusers;
    Button edit,attend,bookmark,save,cancel,delete,SetDate, SetTime,textView3;
    int Date1,Hour1,Minute1,Month1,Year1;
    int Date2,Hour2,Minute2,Month2,Year2;
    private final int PICK_IMAGE_REQUEST = 1;
    boolean test1 = false, test2 = false;
    String customname;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            if(mImageUri != null){
                eventImage.setVisibility(View.INVISIBLE);
                Picasso.with(EventDetails.this)
                        .load(mImageUri)
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(newEventImage, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        newEventImage.setVisibility(View.VISIBLE);
                                        Animations.fadeInAndShowImage(newEventImage);
                                    }

                                    @Override
                                    public void onError() {
                                        newEventImage.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getApplicationContext(), "Image cannot be displayed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
            }
            else{
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Bundle extras = getIntent().getExtras();
        final String id = extras.getString("groupId").trim();
        ref = FirebaseDatabase.getInstance().getReference().child("Group").child(id);
        ref2 = FirebaseDatabase.getInstance().getReference().child("Group").child(id).child("attendees");


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = mAuth.getCurrentUser();
        final DatabaseReference user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        final DatabaseReference attending = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid()).child("attending");
        final DatabaseReference bookmarked = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid()).child("bookmarked");
        //Toast.makeText(EventDetails.this, id, Toast.LENGTH_SHORT).show();
        textView3 = (Button) findViewById(R.id.textView3);
        listofusers = (RecyclerView)findViewById(R.id.listofusers);
        listofusers.setVisibility(View.GONE);
        Listofusers = new ArrayList<>();


        eventName = (EditText)findViewById(R.id.eventName);
        eventDesc = (EditText)findViewById(R.id.eventDesc);
        eventHost = (EditText)findViewById(R.id.eventHost);
        eventLocation = (EditText)findViewById(R.id.eventLocation);

        eventImage = (ImageView)findViewById(R.id.eventImg);
        eventImage.setVisibility(View.INVISIBLE);

        newEventImage = (ImageView)findViewById(R.id.newEventImg);
        newEventImage.setVisibility(View.INVISIBLE);

        progressBar =  findViewById(R.id.progressBar);

        eventDate = (EditText)findViewById(R.id.eventDate);
        edit = (Button)findViewById(R.id.edit);
        save = (Button)findViewById(R.id.save);
        SetDate = (Button)findViewById(R.id.SetDate);
        SetTime = (Button)findViewById(R.id.SetTime);

        SetDate.setVisibility(View.GONE);
        SetTime.setVisibility(View.GONE);
        save.setVisibility(View.GONE);

        delete = (Button)findViewById(R.id.delete);
        delete.setVisibility(View.GONE);

        cancel = (Button)findViewById(R.id.cancel);
        cancel.setVisibility(View.GONE);

        bookmark = (Button)findViewById(R.id.bookMark);
        attend = (Button)findViewById(R.id.attend);



        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listofusers.setLayoutManager(layoutManager1);

        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAdapter adapter = new CustomAdapter(Listofusers);
                listofusers.setAdapter(adapter);
                listofusers.setVisibility(View.VISIBLE);
                Toast.makeText(EventDetails.this, "All attendees are on display now", Toast.LENGTH_SHORT).show();
                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Listofusers.clear();
                        for (DataSnapshot dss : snapshot.getChildren()){
                            String names = dss.getValue(String.class);
                            Listofusers.add(names);
                        }
                        adapter.notifyDataSetChanged();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });



        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //set event picture
                newEventImage.setVisibility(View.INVISIBLE);
                String imgUrl = snapshot.child("groupImgURL").getValue().toString();
                Picasso.with(EventDetails.this)
                        .load(imgUrl)
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(eventImage, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        eventImage.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        Animations.fadeInAndShowImage(eventImage);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Image cannot be displayed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );

                //set event name
                String name = snapshot.child("groupName").getValue().toString();
                eventName.setText(name);

                //set event desc
                String desc = snapshot.child("groupDesc").getValue().toString();
                eventDesc.setText(desc);

                //set event location
                String location = snapshot.child("groupLoc").getValue().toString();
                eventLocation.setText(location);

                //set event host name
                String hostid = snapshot.child("host").child("name").getValue().toString();
                eventHost.setText("Host: " + hostid);

                //set event date
                if (snapshot.child("dateCreated").exists()){
                    String day = snapshot.child("dateCreated").child("0").getValue().toString();
                    String month = snapshot.child("dateCreated").child("1").getValue().toString();
                    String year = snapshot.child("dateCreated").child("2").getValue().toString();
                    String hour = snapshot.child("dateCreated").child("3").getValue().toString();
                    String minute = snapshot.child("dateCreated").child("4").getValue().toString();
                    Date2 = Integer.parseInt(day);
                    Year2 = Integer.parseInt(year);
                    Month2 = Integer.parseInt(month);
                    Hour2 = Integer.parseInt(hour);
                    Minute2 = Integer.parseInt(minute);
                    Calendar calendar1 = Calendar.getInstance();

                    calendar1.set(Calendar.YEAR, Year2);
                    calendar1.set(Calendar.MONTH,Month2);
                    calendar1.set(Calendar.DATE,Date2);
                    calendar1.set(Calendar.HOUR_OF_DAY, Hour2);
                    calendar1.set(Calendar.MINUTE,Minute2);

                    eventDate.setText(DateFormat.format("yyyy-MM-dd hh:mm a", calendar1).toString().toUpperCase());
                }
                //set visibility of edit
                if (!fuser.getUid().equals(snapshot.child("host").child("id").getValue().toString())) {
                    edit.setVisibility(View.GONE);
                } else{
                    edit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        eventName.setEnabled(false);
        eventDesc.setEnabled(false);
        eventLocation.setEnabled(false);
        eventHost.setEnabled(false);
        eventDate.setEnabled(false);
        //eventHost.setText(host)

        eventImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        eventImage.setClickable(false);

        newEventImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        //visible only to host of the event
        edit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                eventImage.setClickable(true);
                mImageUri = null;
                eventName.setEnabled(true);
                eventDesc.setEnabled(true);
                eventLocation.setEnabled(true);
                eventDate.setEnabled(true);
                bookmark.setVisibility(View.GONE);
                attend.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                eventDate.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                SetDate.setVisibility(View.VISIBLE);
                SetTime.setVisibility(View.VISIBLE);
                listofusers.setVisibility(View.GONE);
                textView3.setVisibility(View.GONE);

                        SetDate.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View v) {
                                test1 = true;
                                Calendar newcalendar = Calendar.getInstance();
                                int year = newcalendar.get(Calendar.YEAR);
                                int month = newcalendar.get(Calendar.MONTH);
                                int date = newcalendar.get(Calendar.DATE);

                                DatePickerDialog datePickerDialog = new DatePickerDialog(EventDetails.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        Year1 = year;
                                        Month1 = month;
                                        Date1 = dayOfMonth;

                                        Calendar calendar1 = Calendar.getInstance();
                                        calendar1.set(Calendar.YEAR, year);
                                        calendar1.set(Calendar.MONTH,month);
                                        calendar1.set(Calendar.DATE,dayOfMonth);
                                        CharSequence dateformatting = DateFormat.format("EEEE, dd MMM yyyy",calendar1);
                                        SetDate.setText(dateformatting);
                                    }
                                },year,month,date);
                                datePickerDialog.show();
                            }
                        });
                        SetTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                test2 = true;
                                Calendar calendar = Calendar.getInstance();
                                int hour = calendar.get(Calendar.HOUR);
                                int minute1 = calendar.get(Calendar.MINUTE);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(EventDetails.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                        Hour1 = hourOfDay;
                                        Minute1 = minute;

                                        Calendar calendar1 = Calendar.getInstance();
                                        calendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar1.set(Calendar.MINUTE,minute);
                                        CharSequence charSequence = DateFormat.format("hh:mm a",calendar1);
                                        SetTime.setText(charSequence);
                                    }
                                },hour, minute1, false);
                                timePickerDialog.show();
                            }
                        });





                save.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if ((!eventName.getText().toString().trim().isEmpty() &&
                                !eventDesc.getText().toString().trim().isEmpty() &&
                                !eventLocation.getText().toString().trim().isEmpty() &&
                                test1&&
                                test2) || mImageUri != null){
                            if (!eventName.getText().toString().trim().isEmpty() &&
                                    !eventDesc.getText().toString().trim().isEmpty() &&
                                    !eventLocation.getText().toString().trim().isEmpty() &&
                                    test1&&
                                    test2){
                                ArrayList<Integer> newDateDetails = new ArrayList<>();
                                newDateDetails.add(Date1);
                                newDateDetails.add(Month1);
                                newDateDetails.add(Year1);
                                newDateDetails.add(Hour1);
                                newDateDetails.add(Minute1);

                                ref.child("groupName").setValue(eventName.getText().toString().trim());
                                ref.child("groupDesc").setValue(eventDesc.getText().toString().trim());
                                ref.child("groupLoc").setValue(eventLocation.getText().toString().trim());
                                ref.child("dateCreated").setValue(newDateDetails);

                            }
                            if (mImageUri != null){
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
                                StorageReference fileReference = storageReference.child(id
                                        + "." + getFileExtension(mImageUri));
                                fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setProgress(0);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }, 500);

                                        storageReference.child(id
                                                + "." + getFileExtension(mImageUri)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String url = uri.toString();
                                                ref.child("groupImgURL").setValue(url);
                                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                builder.setTitle("Saved!");
                                                builder.setNeutralButton("Back", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                        eventImage.setVisibility(View.VISIBLE);
                                                        eventImage.setClickable(false);
                                                        newEventImage.setVisibility(View.GONE);
                                                        bookmark.setVisibility(View.VISIBLE);
                                                        attend.setVisibility(View.VISIBLE);
                                                        edit.setVisibility(View.VISIBLE);
                                                        eventDate.setVisibility(View.VISIBLE);
                                                        textView3.setVisibility(View.VISIBLE);
                                                        listofusers.setVisibility(View.GONE);
                                                        save.setVisibility(View.GONE);
                                                        delete.setVisibility(View.GONE);
                                                        cancel.setVisibility(View.GONE);
                                                        SetDate.setVisibility(View.GONE);
                                                        SetTime.setVisibility(View.GONE);
                                                        eventName.setEnabled(false);
                                                        eventDesc.setEnabled(false);
                                                        eventLocation.setEnabled(false);
                                                        eventDate.setEnabled(false);
                                                    }
                                                });
                                                //initialize alert dialog
                                                AlertDialog alertDialog = builder.create();
                                                //show alert dialog
                                                alertDialog.show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Log.e("uri","downloadurifailure");
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EventDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        double progress = (100.0*taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        progressBar.setProgress((int) progress);
                                    }
                                });
                            }else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setTitle("Saved!");
                                builder.setNeutralButton("Back", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        eventImage.setVisibility(View.VISIBLE);
                                        eventImage.setClickable(false);
                                        newEventImage.setVisibility(View.GONE);
                                        bookmark.setVisibility(View.VISIBLE);
                                        attend.setVisibility(View.VISIBLE);
                                        edit.setVisibility(View.VISIBLE);
                                        eventDate.setVisibility(View.VISIBLE);
                                        textView3.setVisibility(View.VISIBLE);
                                        listofusers.setVisibility(View.VISIBLE);
                                        save.setVisibility(View.GONE);
                                        delete.setVisibility(View.GONE);
                                        cancel.setVisibility(View.GONE);
                                        SetDate.setVisibility(View.GONE);
                                        SetTime.setVisibility(View.GONE);
                                        eventName.setEnabled(false);
                                        eventDesc.setEnabled(false);
                                        eventLocation.setEnabled(false);
                                        eventDate.setEnabled(false);
                                    }
                                });
                                //initialize alert dialog
                                AlertDialog alertDialog = builder.create();
                                //show alert dialog
                                alertDialog.show();
                            }
                        }
                        else{
                            Toast.makeText(EventDetails.this, "Missing Data Fields and No image selected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


        //attend button alert dialog
        attend.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                //display_success_attend(builder);

                attending.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child(id).exists()){
                            display_success_attend(builder);
                            attending.child(id).setValue(id);
                            TimeZone tz = TimeZone.getDefault();
                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setData(CalendarContract.Events.CONTENT_URI);
                            intent.putExtra(CalendarContract.Events.TITLE, eventName.getText().toString().trim());
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, eventDesc.getText().toString().trim());
                            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, eventLocation.getText().toString().trim());
                            intent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
                            intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                            Calendar calendar2 = Calendar.getInstance();

                            calendar2.set(Calendar.YEAR, Year2);
                            calendar2.set(Calendar.MONTH,Month2);
                            calendar2.set(Calendar.DATE,Date2);
                            calendar2.set(Calendar.HOUR_OF_DAY, Hour2);
                            calendar2.set(Calendar.MINUTE,Minute2);

                            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar2.getTimeInMillis());
                            startActivity(intent);
                            user.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    customname = snapshot.child("name").getValue().toString().trim();
                                    ref.child("attendees").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            ref.child("attendees").child(
                                                    String.valueOf(snapshot.getChildrenCount())
                                            ).setValue(customname);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
//                            Group.Groupadd(FirebaseDatabase.getInstance().getReference("Group").child())
                            //attending.setValue(map);
                        }
                        else{
                            display_failure_attend(builder);
                            attending.child(id).setValue(null);
                            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dss: snapshot.getChildren()){
                                        String name = dss.getValue().toString();
                                        if (name.equals(customname)){
                                            dss.getRef().setValue(null);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

//                            attending.setValue(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

            }

        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(v.getContext());
                //display_succes_bookmark(builder2);
                //Toast.makeText(EventDetails.this, bookmarked.child(id).toString(), Toast.LENGTH_SHORT).show();
                bookmarked.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.child(id).exists()){
                            display_succes_bookmark(builder2);
                            bookmarked.child(id).setValue(id);
                            TimeZone tz = TimeZone.getDefault();
                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setData(CalendarContract.Events.CONTENT_URI);
                            intent.putExtra(CalendarContract.Events.TITLE, eventName.getText().toString().trim());
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, eventDesc.getText().toString().trim());
                            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, eventLocation.getText().toString().trim());
                            intent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
                            intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                            Calendar calendar2 = Calendar.getInstance();

                            calendar2.set(Calendar.YEAR, Year2);
                            calendar2.set(Calendar.MONTH,Month2);
                            calendar2.set(Calendar.DATE,Date2);
                            calendar2.set(Calendar.HOUR_OF_DAY, Hour2);
                            calendar2.set(Calendar.MINUTE,Minute2);

                            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar2.getTimeInMillis());
                            startActivity(intent);

                        }
                        else{
                            display_failure_bookmark(builder2);
                            bookmarked.child(id).setValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                eventImage.setVisibility(View.VISIBLE);
                eventImage.setClickable(false);
                newEventImage.setVisibility(View.GONE);
                bookmark.setVisibility(View.VISIBLE);
                attend.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                eventDate.setVisibility(View.VISIBLE);
                textView3.setVisibility(View.VISIBLE);
                listofusers.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                SetDate.setVisibility(View.GONE);
                SetTime.setVisibility(View.GONE);
                eventName.setEnabled(false);
                eventDesc.setEnabled(false);
                eventLocation.setEnabled(false);
                eventDate.setEnabled(false);

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String imgUrl = snapshot.child("groupImgURL").getValue().toString();
                        Picasso.with(EventDetails.this)
                                .load(imgUrl)
                                .placeholder(R.mipmap.ic_launcher)
                                .fit()
                                .centerCrop()
                                .into(eventImage);

                        String name = snapshot.child("groupName").getValue().toString();
                        eventName.setText(name);

                        String desc = snapshot.child("groupDesc").getValue().toString();
                        eventDesc.setText(desc);

                        String location = snapshot.child("groupLoc").getValue().toString();
                        eventLocation.setText(location);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder3 = new AlertDialog.Builder(v.getContext());
                builder3.setTitle("Are you sure?");
                builder3.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(EventDetails.this,MainFeed.class);
                        intent.putExtra("deletedId",id);
                        startActivity(intent);
                        finish();

//                        Toast.makeText(EventDetails.this, "Group deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder3.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                //initialize alert dialog
                AlertDialog alertDialog = builder3.create();
                //show alert dialog
                alertDialog.show();
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.ic_account) {
                    Intent intent3 = new Intent(EventDetails.this,MyProfile.class);
                    startActivity(intent3);
                    //finish();
                }

                if (id == R.id.ic_home) {
                    Intent intent1 = new Intent(EventDetails.this, MainFeed.class);
                    startActivity(intent1);
                    //finish();
                }


                if (id == R.id.ic_search){
                    startActivity(new Intent(EventDetails.this, SearchActivity.class));
                }

                if (id == R.id.ic_group) {
                    Intent intent2 = new Intent(EventDetails.this, CreateGroup.class);
                    startActivity(intent2);
                }
                return true;
            }
        });

    }
    public void display_succes_bookmark(AlertDialog.Builder builder2){
        builder2.setTitle("Added to Bookmarks");
        builder2.setMessage("");
        builder2.setPositiveButton("Go to bookmarks", new DialogInterface.OnClickListener() {
            @Override
            //intent to bookmarks
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(EventDetails.this,MyProfile.class);
                startActivity(intent);
            }
        });
        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //initialize alert dialog
        AlertDialog alertDialog = builder2.create();
        //show alert dialog
        alertDialog.show();
    }
    public void display_failure_bookmark(AlertDialog.Builder builder2){
        builder2.setTitle("Unbookmarked!");
        builder2.setMessage("");
        builder2.setNeutralButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //initialize alert dialog
        AlertDialog alertDialog = builder2.create();
        //show alert dialog
        alertDialog.show();
    }

    public void display_success_attend(AlertDialog.Builder builder){
        builder.setTitle("Attendance confirmed!");
        builder.setMessage("");
        builder.setNeutralButton("Back",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //initialize alert dialog
        AlertDialog alertDialog = builder.create();
        //show alert dialog
        alertDialog.show();
    }

    public void display_failure_attend(AlertDialog.Builder builder){
        builder.setTitle("Attendance canceled");
        builder.setMessage("");
        builder.setNeutralButton("Back",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //initialize alert dialog
        AlertDialog alertDialog = builder.create();
        //show alert dialog
        alertDialog.show();
    }
    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
        ArrayList<String> ListOfUsers;

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView UserNames;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                UserNames = (TextView) itemView.findViewById(R.id.Recycleview1);

            }
        }

        public CustomAdapter(ArrayList<String> listOfUsers) {
            ListOfUsers = listOfUsers;
        }

        @NonNull
        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userdisplay, parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
            holder.UserNames.setText(ListOfUsers.get(position));
        }

        @Override
        public int getItemCount() {
            return ListOfUsers.size();
        }


    }
}

