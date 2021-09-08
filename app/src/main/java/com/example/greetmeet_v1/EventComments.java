package com.example.greetmeet_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EventComments extends AppCompatActivity {

    ImageView imgCurrentUser;
    DatabaseReference user,ref;
    FirebaseUser fuser;
    FirebaseDatabase firebaseDatabase;
    Button btnAddComment;
    EditText editTextComment;
    TextView eventTitle;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;

    String PostKey,userURL,userName;
    static String COMMENT_KEY = "Comment" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_comments);

        RvComment = findViewById(R.id.rv_comment);
        eventTitle = findViewById(R.id.post_detail_title);

        imgCurrentUser = findViewById(R.id.post_detail_currentuser_img);

        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);

        PostKey = getIntent().getExtras().getString("eventId");

        firebaseDatabase = FirebaseDatabase.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        ref = FirebaseDatabase.getInstance().getReference().child("Group").child(PostKey);


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventTitle.setText(snapshot.child("groupName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (((Boolean) snapshot.child("profilePic").getValue()) && snapshot.child("imageURL").getValue() != null) {
                    Glide.with(EventComments.this).load(snapshot.child("imageURL").getValue()).into(imgCurrentUser);
                    userURL = snapshot.child("imageURL").getValue().toString();
                }
                userName = snapshot.child("name").getValue().toString();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey).push();
                String comment_content = editTextComment.getText().toString();
                String uid = fuser.getUid();
                String uname = userName;
                String uimg = userURL;

                Comment comment = new Comment(comment_content,uid,uimg,uname);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("comment added");
                        editTextComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("fail to add comment: " + e.getMessage());
                    }
                });
            }
        });

        iniRvComment();

    }

    private void iniRvComment(){

        RvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment) ;
                }
                commentAdapter = new CommentAdapter(getApplicationContext(),listComment);
                RvComment.setAdapter(commentAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }


    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }
}