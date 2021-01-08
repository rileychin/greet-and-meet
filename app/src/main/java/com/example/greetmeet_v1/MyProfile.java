package com.example.greetmeet_v1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.util.ArrayList;

//implement AsyncTask here to run onCreate finish then run onStart()
public class MyProfile extends AppCompatActivity {
    private RecyclerView createdView, attending_bookmarked_recyclerView;
    private HorizontalAdapterClass adapter;
    Button logout,btnAttending,btnBookmarked,upload;
    FirebaseAuth mAuth;
    HorizontalAdapterClass.RecyclerViewClickListener listener;
    AdapterClass.RecyclerViewClickListener listener2;
    private ProgressBar profileProgressbar;
    private ImageView imageView;
    private TextView userName;
    private StorageTask mUploadTask;
    private DatabaseReference groupRef,user;
    private ArrayList<Group> groupsCreated;
    private ArrayList<Group> groupsAttending;
    private ArrayList<Group> groupsBookmarked;
    private ArrayList<String> groupsAttendingName,groupsBookmarkedName;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    FirebaseUser fuser;
    private int state;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        groupsCreated = new ArrayList<>();
        groupsAttending = new ArrayList<>();
        groupsBookmarked = new ArrayList<>();
        groupsAttendingName = new ArrayList<>();
        groupsBookmarkedName = new ArrayList<>();
        groupRef = FirebaseDatabase.getInstance().getReference("Group");
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = mAuth.getCurrentUser();
        user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());


        imageView = (ImageView)findViewById(R.id.profile_image);
        userName = (TextView)findViewById(R.id.userName);
        logout = (Button)findViewById(R.id.logout);
        btnAttending = (Button)findViewById(R.id.btnAttending);
        btnBookmarked = (Button)findViewById(R.id.btnBookmarked);
        upload = (Button)findViewById(R.id.upload);
        profileProgressbar = (ProgressBar) findViewById(R.id.profileprogressBar);
        profileProgressbar.setVisibility(View.GONE);

        state=1; //state that switches between attending and bookmarked state


        //recycler view for created groups
        LinearLayoutManager layoutManager1
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        createdView = findViewById(R.id.groupsLayout);
        createdView.setLayoutManager(layoutManager1);

        //recycler view for attending/bookmarked groups
        attending_bookmarked_recyclerView = (RecyclerView)findViewById(R.id.attending_bookmarked_recycerView);
        attending_bookmarked_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                // Sign-out successful.
                Toast.makeText(MyProfile.this, "Signed Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        //display attending events
        btnAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterClass adapterClass1 = new AdapterClass(groupsAttending,listener2);
                attending_bookmarked_recyclerView.setAdapter(adapterClass1);
                Toast.makeText(MyProfile.this,"displaying attendings",Toast.LENGTH_LONG).show();
                state = 1;
            }
        });
        //display bookmarked events
        btnBookmarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterClass adapterClass1 = new AdapterClass(groupsBookmarked,listener2);
                attending_bookmarked_recyclerView.setAdapter(adapterClass1);
                Toast.makeText(MyProfile.this,"displaying bookmarked",Toast.LENGTH_LONG).show();
                state = 0;
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

        createdView.addOnItemTouchListener(new RecyclerItemClickListener(this, createdView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MyProfile.this,EventDetails.class);
                intent.putExtra("groupId",groupsCreated.get(position).getGroupId());
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        attending_bookmarked_recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, attending_bookmarked_recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MyProfile.this,EventDetails.class);
                //check whether attending or bookmarked is selected at first
                if (state == 1){
                    intent.putExtra("groupId",groupsAttending.get(position).getGroupId());
                }
                else{
                    intent.putExtra("groupId",groupsBookmarked.get(position).getGroupId());
                }
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


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
                                            Animations.fadeInAndShowImage(imageView);
                                        }

                                        @Override
                                        public void onError() {
                                            Toast.makeText(getApplicationContext(), "Image cannot be displayed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                }else{
                    imageView.setVisibility(View.INVISIBLE);
                    upload.setVisibility(View.VISIBLE);
                    Toast.makeText(MyProfile.this,"No Profile Pic",Toast.LENGTH_LONG).show();
                }

                //goes to here only after second activity click
                if(snapshot.child("bookmarked").exists()){
                    for(DataSnapshot ds : snapshot.child("bookmarked").getChildren()){
                        groupsBookmarkedName.add(ds.getValue().toString());
                    }
                    groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                            //createGroups list
                            if (dataSnapshot.exists()){
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    if (!ds.getValue(Group.class).getDeleted()){
                                        try {
                                            String groupId = ds.getValue(Group.class).getGroupId();
                                            if (groupsBookmarkedName.contains(groupId)) {
                                                groupsBookmarked.add(0, ds.getValue(Group.class));
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                AdapterClass adapterClass1 = new AdapterClass(groupsBookmarked,listener2);
                                attending_bookmarked_recyclerView.setAdapter(adapterClass1);

                            }

                        }

                        public void onCancelled(@NonNull DatabaseError databaseError){
                            Toast.makeText(MyProfile.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if(snapshot.child("attending").exists()){
                    for(DataSnapshot ds: snapshot.child("attending").getChildren()){
                        groupsAttendingName.add(ds.getValue().toString());
                    }
                    groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                            //createGroups list
                            if (dataSnapshot.exists()){
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    if (!ds.getValue(Group.class).getDeleted()){
                                        try {
                                            String groupId = ds.getValue(Group.class).getGroupId();
                                            if (groupsAttendingName.contains(groupId)) {
                                                groupsAttending.add(0, ds.getValue(Group.class));
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                AdapterClass adapterClass1 = new AdapterClass(groupsAttending,listener2);
                                attending_bookmarked_recyclerView.setAdapter(adapterClass1);
                            }

                        }

                        public void onCancelled(@NonNull DatabaseError databaseError){
                            Toast.makeText(MyProfile.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                String name = snapshot.child("name").getValue().toString();
                userName.setText(name);
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
                //if (id == R.id.ic_account) {
                //Intent intent1 = new Intent(MyProfile.this, MyProfile.class);
                //startActivity(intent1);
                //finish();
                //}

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
        Toast.makeText(MyProfile.this,groupsAttendingName.toString(), Toast.LENGTH_LONG).show();
        if (groupRef!=null){
            groupRef.addValueEventListener(new ValueEventListener(){
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                    //createGroups list
                    if (dataSnapshot.exists()){
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            if (!ds.getValue(Group.class).getDeleted()){
                                try {
                                    String groupId = ds.getValue(Group.class).getGroupId();
                                    String hostId = dataSnapshot.child(groupId).child("host").child("id").getValue().toString();
                                    if (fuser.getUid().equals(hostId)) {
                                        groupsCreated.add(0, ds.getValue(Group.class));
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        HorizontalAdapterClass adapterClass = new HorizontalAdapterClass(groupsCreated,listener);
                        createdView.setAdapter(adapterClass);
                    }

                }

                public void onCancelled(@NonNull DatabaseError databaseError){
                    Toast.makeText(MyProfile.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    protected void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = mAuth.getCurrentUser();
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        if (groupsCreated != null || groupsAttending != null || groupsBookmarked != null){
            groupsCreated.clear();
            groupsAttending.clear();
            groupsBookmarked.clear();
        }


    }
}