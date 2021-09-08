package com.example.greetmeet_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


public class CategoriesRegistration extends AppCompatActivity {
    String mPersonName, mSetEmail, mSetPassword;
    ListView listView;
    Button mFinish;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_registration);
        Intent previntent = getIntent();
        mPersonName = previntent.getStringExtra(Register.NAME);
        mSetEmail = previntent.getStringExtra(Register.EMAIL);
        mSetPassword= previntent.getStringExtra(Register.PASSWORD);
        mFinish = findViewById(R.id.Finish);
        listView = (ListView)findViewById(R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemChecked(2, true);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, Categories));
        databaseReference = FirebaseDatabase.getInstance().getReference("Group");
        final ArrayList<String> CategoriesSelected = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View v, int i, long l) {
                String selectedFromList =(String) (listView.getItemAtPosition(i));
                if (CategoriesSelected.contains(selectedFromList)){
                    CategoriesSelected.remove(selectedFromList);
                }else{
                    CategoriesSelected.add(selectedFromList);
                }
            }
        });
        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mSetEmail;
                String password = mSetPassword;
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                final String strDate = dateFormat.format(date);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()) {
                                    FirebaseUser fuser = mAuth.getCurrentUser();
                                    Users user = new Users(mPersonName,fuser.getUid());
                                    user.setEmail(mSetEmail);
                                    user.setCategories(CategoriesSelected);
                                    user.setDateTime(strDate);
                                    user.setId(mAuth.getUid());
                                    user.setProfilePic(false);
                                    FirebaseDatabase.getInstance().getReference().child("User").child(fuser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });


                                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CategoriesRegistration.this, "Registration successful, verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CategoriesRegistration.this, "Registration successful, verification Email not sent", Toast.LENGTH_SHORT).show();
                                        }
                                        });
                                    startActivity(new Intent(CategoriesRegistration.this, ProfileUpload.class));
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getApplicationContext(), "User exists",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    };
    private static  String[] Categories = new String[] {
            "Music", "Gaming", "Sports", "Studies", "Events"
    };
}