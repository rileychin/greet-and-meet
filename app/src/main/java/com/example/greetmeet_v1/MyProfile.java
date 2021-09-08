package com.example.greetmeet_v1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormatSymbols;

public class MyProfile extends AppCompatActivity {
    Button logout,upload;
    FirebaseAuth mAuth;
    private ProgressBar profileProgressbar;
    private ImageView imageView;
    private TextView userName,userEmail,userID,userJoindate;
    private StorageTask mUploadTask;
    private DatabaseReference groupRef,user;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    FirebaseUser fuser;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
            final FirebaseUser fuser = mAuth.getCurrentUser();
            final DatabaseReference user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
            if(mImageUri != null){
                StorageReference fileReference = mStorageRef.child(fuser.getUid()
                        + "." + getFileExtension(mImageUri));

                mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override

                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        upload.setVisibility(View.GONE);
                        profileProgressbar.setVisibility(View.GONE);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                profileProgressbar.setProgress(0);
                            }
                        }, 500);
                        Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                        user.child("profilePic").setValue(true);
                        //set a new value for profile pic id.
                        mStorageRef.child(fuser.getUid()
                                + "." + getFileExtension(mImageUri)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //getting special token to retrieve image back from firebase storage.
                                String url = uri.toString();
                                user.child("imageURL").setValue(url);
                                Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e("uri","downloadurifailure");
                            }
                        });
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                upload.setVisibility(View.VISIBLE);
                                profileProgressbar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                upload.setVisibility(View.INVISIBLE);
                                profileProgressbar.setVisibility(View.VISIBLE);
                                profileProgressbar.setProgress((int) progress);
                            }
                        });

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

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        groupRef = FirebaseDatabase.getInstance().getReference("Group");
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = mAuth.getCurrentUser();
        user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());


        imageView = (ImageView)findViewById(R.id.profile_image);
        userName = (TextView)findViewById(R.id.userName);
        userEmail = (TextView)findViewById(R.id.userEmail);
        userID = (TextView)findViewById(R.id.userID);
        userJoindate = (TextView)findViewById(R.id.userJoindate);
        logout = (Button)findViewById(R.id.logout);
        upload = (Button)findViewById(R.id.upload);
        profileProgressbar = (ProgressBar) findViewById(R.id.profileprogressBar);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                // Sign-out successful.
                Toast.makeText(MyProfile.this, "Signed Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (((Boolean) snapshot.child("profilePic").getValue()) && snapshot.child("imageURL").getValue() != null){
                    imageView.setVisibility(View.INVISIBLE);

                    upload.setVisibility(View.GONE);
                    String imgUrl = snapshot.child("imageURL").getValue().toString();
                    Picasso.with(MyProfile.this)
                            .load(imgUrl)
                            .placeholder(R.mipmap.ic_launcher)
                            .fit()
                            .centerCrop()
                            .into(imageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            imageView.setVisibility(View.VISIBLE);
                                            profileProgressbar.setVisibility(View.GONE);
                                            Animations.fadeInAndShowImage(imageView);
                                        }

                                        @Override
                                        public void onError() {
                                            profileProgressbar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "Image cannot be displayed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                }else{
                    imageView.setVisibility(View.INVISIBLE);
                    upload.setVisibility(View.VISIBLE);
                    Toast.makeText(MyProfile.this,"No Profile Pic",Toast.LENGTH_LONG).show();
                }

                String name = snapshot.child("name").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String id = snapshot.child("id").getValue().toString();
                String dateTime = snapshot.child("dateTime").getValue().toString();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Calendar calendar = Calendar.getInstance();
                try {
                    Date date = formatter.parse(dateTime);
                    formatter.applyPattern("dd/MM/yyyy");
                    calendar.setTime(date);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    String month_name = new DateFormatSymbols().getMonths()[month];
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    String dateString = day + " " + month_name + " " + year;
                    userJoindate.setText(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                userName.setText(name);
                userEmail.setText(email);
                userID.setText(id);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    Intent intent1 = new Intent(MyProfile.this, MyEvents.class);
                    startActivity(intent1);
                    finish();
                }

                if (id == R.id.ic_home) {
                    Intent intent3 = new Intent(MyProfile.this, MainFeed.class);
                    intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent3);
                    finish();
                }
              
                if (id == R.id.ic_search){
                    startActivity(new Intent(MyProfile.this, SearchActivity.class));
                }


                return true;
            }
        });

        //adding groups created to horizontal recycler view

    }


    protected void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = mAuth.getCurrentUser();
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
    }
}