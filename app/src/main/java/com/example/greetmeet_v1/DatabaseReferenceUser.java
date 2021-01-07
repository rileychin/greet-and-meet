package com.example.greetmeet_v1;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseReferenceUser {
    static final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
    static DatabaseReference user;

    static DatabaseReference getUser(){
        user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        return user;
    }

}
