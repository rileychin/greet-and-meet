package com.example.greetmeet_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreateGroupConfirmation extends AppCompatActivity {
    Button buttonFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_confirmation);

        buttonFeed = (Button)findViewById(R.id.buttonFeed);

        buttonFeed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent start = new Intent(CreateGroupConfirmation.this, MainFeed.class);
                startActivity(start);
                finish();
            }
        });
    }
}