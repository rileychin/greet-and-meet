package com.example.greetmeet_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
                startActivity(new Intent(SearchActivity.this, MyProfile.class));
                finish();
                }

                if (id == R.id.ic_home) {
                    //intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(new Intent(SearchActivity.this, MainFeed.class));
                    finish();
                }

                if (id == R.id.ic_group) {
                    //intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(new Intent(SearchActivity.this, CreateGroup.class));
                    finish(); // does not terminate properly for some reason
                }
                return true;
            }
        });
    }
}