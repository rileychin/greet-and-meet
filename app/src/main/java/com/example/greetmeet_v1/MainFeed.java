package com.example.greetmeet_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.TimeUnit;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;



public class MainFeed extends AppCompatActivity{

    DatabaseReference reff;
    ArrayList<Group> list;
    RecyclerView recyclerView;
    SearchView searchView;
    AdapterClass adapterClass;
    AdapterClass.RecyclerViewClickListener listener;
    Button createGroup;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feed);

        reff = FirebaseDatabase.getInstance().getReference().child("Group");
        list = new ArrayList<>();
        searchView = (SearchView)findViewById(R.id.searchView);
//        createGroup = (Button)findViewById(R.id.createGroup);
//        createGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainFeed.this,CreateGroup.class));
//            }
//        });
        recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //move to EventDetails
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(MainFeed.this,list.get(position).getGroupName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainFeed.this,EventDetails.class);
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
                    Intent intent1 = new Intent(MainFeed.this, MyProfile.class);
                    startActivity(intent1);
                    //finish();
                }

                if (id == R.id.ic_home) {
//                    Intent intent3 = new Intent(MainFeed.this, MainFeed.class);
//                    startActivity(intent3);
                    //finish();
                }

                if (id == R.id.ic_group) {
                    Intent intent2 = new Intent(MainFeed.this, CreateGroup.class);
                    startActivity(intent2);
                    //finish();
                }

                return true;
            }
        });


    }

    protected void onStart(){
        if (list != null){
            list.clear();
        }

        super.onStart();
        if (getIntent().hasExtra("deletedId")){
            Bundle boondle = getIntent().getExtras();
            String deletedId = boondle.getString("deletedId");
            Toast.makeText(MainFeed.this,"" + list.isEmpty(),Toast.LENGTH_SHORT).show();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Group").child(deletedId);
            //boondle.getString("deletedId")

            //ref.removeValue();
            ref.child("groupName").setValue("");
            ref.child("groupDesc").setValue("");
            ref.child("groupId").setValue("");
            ref.child("groupLoc").setValue("");
            ref.child("deleted").setValue(true);
            ref.child("categorySpinner").setValue("");

        }
        if (reff!=null){
            reff.addValueEventListener(new ValueEventListener(){
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                    //check for deleted item

                    if (dataSnapshot.exists()){
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            if (!ds.getValue(Group.class).getDeleted()){
                                list.add(0, ds.getValue(Group.class));
                            }
                        }
                        AdapterClass adapterClass = new AdapterClass(list,listener);
                        recyclerView.setAdapter(adapterClass);
                    }

                }

                public void onCancelled(@NonNull DatabaseError databaseError){
                    Toast.makeText(MainFeed.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);
                    return false;
                }

            });
        }
    }


    private void search(String str){
        ArrayList<Group> myList = new ArrayList<>();
        for(Group object: list){
            if (object.getGroupDesc().toLowerCase().contains(str.toLowerCase()) ||
                    object.getGroupLoc().toLowerCase().contains(str.toLowerCase()) ||
                    object.getGroupName().toLowerCase().contains(str.toLowerCase()) ||
                    object.getCategorySpinner().toLowerCase().contains(str.toLowerCase()))
            {
                myList.add(0,object);
            }
        }
        AdapterClass adapterClass = new AdapterClass(myList,listener);
        recyclerView.setAdapter(adapterClass);
    }

}