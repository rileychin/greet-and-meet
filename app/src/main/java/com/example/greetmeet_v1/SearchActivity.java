package com.example.greetmeet_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private SearchAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseReference reff;
    private ArrayList<Group> list;
    SearchAdapter.RecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        reff = FirebaseDatabase.getInstance().getReference().child("Group");

        list = new ArrayList<Group>();

        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(MainFeed.this,list.get(position).getGroupName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchActivity.this,EventDetails.class);
                intent.putExtra("groupId",list.get(position).getGroupId());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

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

        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    protected void onStart() {
        if (list != null){
            list.clear();
        }
        super.onStart();
        if (reff != null) {
            reff.addValueEventListener(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //check for deleted item

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (!ds.getValue(Group.class).getDeleted()) {
                                list.add(0, ds.getValue(Group.class));
                            }
                        }
                        adapter = new SearchAdapter(list, listener);
                        recyclerView.setAdapter(adapter);
                    }

                }

                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SearchActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}