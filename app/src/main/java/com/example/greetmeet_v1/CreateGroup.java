package com.example.greetmeet_v1;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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


public class CreateGroup extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProgressBar mProgressBar;
    Button createGroup;
    EditText groupName, groupDesc, groupLoc, DateTimeSelector;
    Spinner categorySpinner;
    ImageView groupPic;
    private Uri mImageUri;
    Bitmap bitmap;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    int Image_Request_Code = 1;
    ProgressDialog progressDialog;
    int Date,Hour,Minute,Month,Year;
    ArrayList<Integer> date_and_time;
    ArrayList<Users> groupattendees;
    Group group;
    private StorageTask mUploadTask;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser fuser = mAuth.getCurrentUser();
    final DatabaseReference user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Group");
        createGroup = (Button) findViewById(R.id.createGroup);
        groupName = (EditText) findViewById(R.id.gName);
        groupDesc = (EditText) findViewById(R.id.gDesc);
        groupLoc = (EditText) findViewById(R.id.gLoc);
        groupPic = (ImageView) findViewById(R.id.groupPic);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
//        imgUpload = (Button)findViewById(R.id.imgUpload);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
//        TimeSelector = (Button) findViewById(R.id.TimeSelector);
//        DateSelector = (Button) findViewById(R.id.DateSelector);
        DateTimeSelector = (EditText)findViewById(R.id.DateTimeSelector);
        String[] categories = new String[]{"Game", "Tech", "Sports","Education","Recreation","Food","General"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(adapter);
        progressDialog = new ProgressDialog(CreateGroup.this);

        //Date & Time selector combined into 1 selection
        DateTimeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateGroup.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Year = year;
                        Month = month;
                        Date = dayOfMonth;

                        int hour = calendar.get(Calendar.HOUR);
                        int minute1 = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateGroup.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                Hour = hourOfDay;
                                Minute = minute;

                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar1.set(Calendar.MINUTE,minute);
                                calendar1.set(Calendar.YEAR, year);
                                calendar1.set(Calendar.MONTH,month);
                                calendar1.set(Calendar.DATE,dayOfMonth);
                                CharSequence charSequence = DateFormat.format("dd MMM yyyy hh:mm a",calendar1);
                                DateTimeSelector.setText(charSequence);
                            }
                        },hour, minute1, false);
                        timePickerDialog.show();
                    }
                },year, month,date);
                datePickerDialog.show();
            }
        });
//
//        DateSelector.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int date = calendar.get(Calendar.DATE);
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateGroup.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
//                        Year = year;
//                        Month = month;
//                        Date = dayOfMonth;
//
//
//
//                        Calendar calendar1 = Calendar.getInstance();
//                        calendar1.set(Calendar.YEAR, year);
//                        calendar1.set(Calendar.MONTH,month);
//                        calendar1.set(Calendar.DATE,dayOfMonth);
//                        CharSequence dateformatting = DateFormat.format("EEEE, dd MMM yyyy",calendar1);
//                        DateSelector.setText(dateformatting);
//                    }
//                },year, month,date);
//                datePickerDialog.show();
//            }
//        });
//        TimeSelector.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                int hour = calendar.get(Calendar.HOUR);
//                int minute1 = calendar.get(Calendar.MINUTE);
//                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateGroup.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
//                        Hour = hourOfDay;
//                        Minute = minute;
//
//                        Calendar calendar1 = Calendar.getInstance();
//                        calendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                        calendar1.set(Calendar.MINUTE,minute);
//                        CharSequence charSequence = DateFormat.format("hh:mm a",calendar1);
//                        TimeSelector.setText(charSequence);
//                    }
//                },hour, minute1, false);
//                timePickerDialog.show();
//            }
//        });

        groupPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        group = new Group();
        createGroup.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                if (groupName.getText().toString().trim().isEmpty() || groupDesc.getText().toString().trim().isEmpty() || groupLoc.getText().toString().trim().isEmpty()){
                    Toast.makeText(CreateGroup.this,"Missing Fields!",Toast.LENGTH_LONG).show();
                }
                else{
                    final String groupId  = databaseReference.push().getKey();
                    group.setGroupId(groupId);

                    if ((mUploadTask != null && mUploadTask.isInProgress())){
                        Toast.makeText(CreateGroup.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        group.setGroupName(groupName.getText().toString().trim());
                        group.setGroupDesc(groupDesc.getText().toString().trim());
                        group.setGroupLoc(groupLoc.getText().toString().trim());
                        group.setCategorySpinner(categorySpinner.getSelectedItem().toString().trim());
                        ArrayList<Integer> date_and_time = new ArrayList<>();
                        date_and_time.add(Date); //index 0 DAY
                        date_and_time.add(Month); //index 1 MONTH
                        date_and_time.add(Year); //index 2 YEAR
                        date_and_time.add(Hour); //index 3 HOUR
                        date_and_time.add(Minute); //index 4 MINUTE
                        group.setDateCreated(date_and_time);

                        UploadImage(groupId,group);
                    }



                }

            }

        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.ic_events) {
                    Intent intent3 = new Intent(CreateGroup.this, MyEvents.class);
                    startActivity(intent3);
                    //finish();
                }

                if (id == R.id.ic_home) {
                    Intent intent1 = new Intent(CreateGroup.this, MainFeed.class);
                    startActivity(intent1);
                    //finish();
                }
                if (id == R.id.ic_search){
                    startActivity(new Intent(CreateGroup.this, SearchActivity.class));
                }

                return true;
            }
        });

    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).fit().into(groupPic);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void UploadImage(String groupId,Group group){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = mAuth.getCurrentUser();
        final DatabaseReference user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());

        if (mImageUri != null){
            StorageReference fileReference = storageReference.child(groupId
                    + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(0);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }, 500);

                    storageReference.child(groupId
                            + "." + getFileExtension(mImageUri)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            databaseReference.child(groupId).child("groupImgURL").setValue(url);
                            group.setGroupImgURL(url);

                            //confirmation and moving to next page
                            //******* CRASHES SOMEWHERE HERE because the activity myprofile is not killed and the ondatachange calls a null object
                            // I added a new method group called complete to circumvent this problem while we find the right way to terminate myprofile correctly
                            user.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String name = snapshot.child("name").getValue().toString();
                                    ArrayList<String> attendees = new ArrayList<>();
                                    group.setAttendees(attendees);
                                    attendees.add(name);
                                    databaseReference.child(groupId).setValue(group);
                                    HashMap<String,String> map = new HashMap<String,String>();
                                    map.put("name",name);
                                    map.put("id",fuser.getUid());
//                                    databaseReference.child(groupId).child("host").child("name").setValue(name);
//                                    databaseReference.child(groupId).child("host").child("id").setValue(fuser.getUid());
                                    databaseReference.child(groupId).child("host").setValue(map);
                                    //databaseReference.child(groupId).child("hostId").setValue(fuser.getUid());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            startActivity(new Intent(getApplicationContext(), CreateGroupConfirmation.class));
                            //finish();

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateGroup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    double progress = (100.0*taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressBar.setProgress((int) progress);
                }
            });
        }
        else{
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

}