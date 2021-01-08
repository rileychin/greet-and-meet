package com.example.greetmeet_v1;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class LoginTest extends AppCompatActivity {
    Button mLogout,mCheckVerified;
    FirebaseAuth mAuth;
    TextView textemail, textname, textdatetime,textverified;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_test);
        textemail = findViewById(R.id.TextEmail);
        textname = findViewById(R.id.TextName);
        textdatetime = findViewById(R.id.TextDateTime);
        textverified = findViewById(R.id.TextVerified);
        mCheckVerified = findViewById(R.id.CheckVerified);
        mLogout = findViewById(R.id.Logout);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = mAuth.getCurrentUser();
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        user.addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                                           String email = snapshot.child("email").getValue().toString();
                                           textemail.setText("Email: "+email);

                                           String name = snapshot.child("name").getValue().toString();
                                           textname.setText("Name: "+name);

                                           String datetime = snapshot.child("dateTime").getValue().toString();
                                           textdatetime.setText("Date and Time joined: "+datetime);
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError error) {

                                       }
                                   });
        if (fuser.isEmailVerified()){
            textverified.setText("Email verified!");
        } else{
            textverified.setText("Please verify your email");
        }
        mCheckVerified.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fuser.reload();
                if (fuser.isEmailVerified()){
                    textverified.setText("Email verified!");
                } else{
                    textverified.setText("Please verify your email");
                }
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mAuth.signOut();
                // Sign-out successful.
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}