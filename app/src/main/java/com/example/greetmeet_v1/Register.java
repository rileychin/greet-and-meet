package com.example.greetmeet_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;

    TextView mRegistrationTitle;
    static EditText mPersonName, mSetEmail, mSetPassword, mTestPassword;
    Button mNext, mBack;
    private String mainemail, mainpassword;
    private static final String TAG = "Register";
    public static final String EMAIL = "email", PASSWORD = "password", NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mRegistrationTitle = findViewById(R.id.RegistrationTitle);
        mPersonName = findViewById(R.id.PersonName);
        mSetEmail = findViewById(R.id.SetEmail);
        mSetPassword = findViewById(R.id.SetPassword);
        mTestPassword = findViewById(R.id.TestPassword);
        mNext = findViewById(R.id.Next);
        mBack = findViewById(R.id.Back);
        mAuth = FirebaseAuth.getInstance();
        Intent previntent = getIntent();
        mainemail = previntent.getStringExtra(MainActivity.EMAIL);
        mainpassword = previntent.getStringExtra(MainActivity.PASSWORD);

        if (!mainemail.isEmpty()){
            mSetEmail.setText(mainemail);
        }
        if (!mainpassword.isEmpty()){
            mSetPassword.setText(mainpassword);
        }

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password1 = mSetPassword.getText().toString().trim();
                String password2 = mTestPassword.getText().toString().trim();
                String email =  mSetEmail.getText().toString().trim();
                String name = mPersonName.getText().toString().trim();
                if (TextUtils.isEmpty(password1)){
                    mSetPassword.setError("Password is required");
                }
                if (TextUtils.isEmpty(password2)){
                    mTestPassword.setError("Password is required");
                }
                if (TextUtils.isEmpty(email)){
                    mSetEmail.setError("Email is required");
                }
                if (TextUtils.isEmpty(name)){
                    mPersonName.setError("Name is required");
                }else if (password1.length()<6) {
                    mSetPassword.setError("More than 5 characters are required");
                }else if (password2.length()<6){
                    mTestPassword.setError("More than 5 characters are required");
                }else if (!password1.equals(password2)) {
                    mSetPassword.setError("Passwords do not match");
                    mSetPassword.setText("");
                    mTestPassword.setError("Passwords do not match");
                    mTestPassword.setText("");
                }else{
                    Intent intent = new Intent(Register.this,CategoriesRegistration.class);
                    intent.putExtra(NAME,name);
                    intent.putExtra(EMAIL,email);
                    intent.putExtra(PASSWORD,password1);
                    startActivity(intent);
                }
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =  mSetEmail.getText().toString().trim();
                String password1 = mSetPassword.getText().toString().trim();
                Intent intent = new Intent(Register.this,MainActivity.class);
                intent.putExtra(EMAIL,email);
                intent.putExtra(PASSWORD,password1);
                startActivity(intent);
            }
        });

    }

}