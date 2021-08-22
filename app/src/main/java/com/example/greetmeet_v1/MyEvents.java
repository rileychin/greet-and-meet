package com.example.greetmeet_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyEvents extends AppCompatActivity {


    ViewPager viewPager;
    TabLayout tabLayout;
    ImageButton profile;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);


        //Navigation View Bottom
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        profile = (ImageButton)findViewById(R.id.accountButton);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start = new Intent(MyEvents.this,MyProfile.class);
                startActivity(start);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.ic_home) {
                    Intent intent3 = new Intent(MyEvents.this, MainFeed.class);
                    startActivity(intent3);
                    finish();
                }

                if (id == R.id.ic_search){
                    startActivity(new Intent(MyEvents.this, SearchActivity.class));
                }

                return true;
            }
        });

        //TabLayout
        tabLayout = findViewById(R.id.MyEventsTabLayout);
        viewPager = findViewById(R.id.MyEventsViewPager);

        //Initialize array list
        ArrayList<String> arrayList = new ArrayList<>();

        //Add title in array list
        arrayList.add("Attending");
        arrayList.add("Bookmarked");
        arrayList.add("Created");

        prepareViewPager(viewPager,arrayList);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList){
        //Initalize Main Adapter
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        //Initialize main fragment
        MainFragment fragment = new MainFragment();
        //use For loop
        for (int i =0; i<arrayList.size(); i++){
            Bundle bundle = new Bundle();

            bundle.putString("title",arrayList.get(i));

            fragment.setArguments(bundle);

            adapter.addFragment(fragment,arrayList.get(i));

            fragment = new MainFragment();
        }
        viewPager.setAdapter(adapter);
    }

    private class MainAdapter extends FragmentPagerAdapter{
        ArrayList<String> arrayList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title){
            arrayList.add(title);
            fragmentList.add(fragment);
        }
        public MainAdapter(@NonNull FragmentManager fm){
            super(fm);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            //Return fragment position
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            //Return fragment list size
            return fragmentList.size();
        }

        public CharSequence getPageTitle(int position){
            return arrayList.get(position);
        }
    }


}