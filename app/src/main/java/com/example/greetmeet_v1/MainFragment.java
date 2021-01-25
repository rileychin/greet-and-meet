package com.example.greetmeet_v1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SwipeRefreshLayout myEventsRefresh;
    RecyclerView myEventsRecyclerView;

    DatabaseReference reff;
    ArrayList<Group> list;
    FirebaseUser fuser;

    AdapterClass adapterClass;
    AdapterClass.RecyclerViewClickListener listener;
    String type;
    DatabaseReference user;

    ArrayList<String> groupsAttendingName,groupsBookmarkedName;




    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialize view
        View view = inflater.inflate(R.layout.fragment_main,container,false);
        type = getArguments().getString("title");


        myEventsRecyclerView = view.findViewById(R.id.MyEventsRecycleView);
        myEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));;

        myEventsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), myEventsRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(MainFeed.this,list.get(position).getGroupName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),EventDetails.class);
                intent.putExtra("groupId",list.get(position).getGroupId());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

        myEventsRefresh = view.findViewById(R.id.MyEventsRefresh);
        myEventsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onStart();
                if(myEventsRefresh.isRefreshing()){
                    myEventsRefresh.setRefreshing(false);
                }
            }
        });

        reff = FirebaseDatabase.getInstance().getReference().child("Group");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        user = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        list = new ArrayList<>();
        groupsBookmarkedName = new ArrayList<>();
        groupsAttendingName = new ArrayList<>();


        return view;

        }

    public void onStart(){
        if (list != null){
            list.clear();
        }

        super.onStart();

        switch(type){
            //case 1
            case "Attending":
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("attending").exists()){
                            for(DataSnapshot ds: snapshot.child("attending").getChildren()){
                                groupsAttendingName.add(ds.getValue().toString());
                            }
                            reff.addListenerForSingleValueEvent(new ValueEventListener() {
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                                    //createGroups list
                                    if (dataSnapshot.exists()){
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            if (!ds.getValue(Group.class).getDeleted()){
                                                try {
                                                    String groupId = ds.getValue(Group.class).getGroupId();
                                                    if (groupsAttendingName.contains(groupId)) {
                                                        list.add(0, ds.getValue(Group.class));
                                                    }
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        AdapterClass adapterClass1 = new AdapterClass(list,listener);
                                        myEventsRecyclerView.setAdapter(adapterClass1);
                                    }

                                }

                                public void onCancelled(@NonNull DatabaseError databaseError){
                                    Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                break;
            //case 2
            case "Bookmarked":
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("bookmarked").exists()){
                            for(DataSnapshot ds : snapshot.child("bookmarked").getChildren()){
                                groupsBookmarkedName.add(ds.getValue().toString());
                            }
                            reff.addListenerForSingleValueEvent(new ValueEventListener() {
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                                    //createGroups list
                                    if (dataSnapshot.exists()){
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            if (!ds.getValue(Group.class).getDeleted()){
                                                try {
                                                    String groupId = ds.getValue(Group.class).getGroupId();
                                                    if (groupsBookmarkedName.contains(groupId)) {
                                                        list.add(0, ds.getValue(Group.class));
                                                    }
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        AdapterClass adapterClass1 = new AdapterClass(list,listener);
                                        myEventsRecyclerView.setAdapter(adapterClass1);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }


                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){
                        Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


                break;
            //case 3
            case "Created":
                if (reff!=null){
                    reff.addValueEventListener(new ValueEventListener(){
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                            //createGroups list
                            if (dataSnapshot.exists()){
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    if (!ds.getValue(Group.class).getDeleted()){
                                        try {
                                            String groupId = ds.getValue(Group.class).getGroupId();
                                            String hostId = dataSnapshot.child(groupId).child("host").child("id").getValue().toString();
                                            if (fuser.getUid().equals(hostId)) {
                                                list.add(0, ds.getValue(Group.class));
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                AdapterClass adapterClass = new AdapterClass(list,listener);
                                myEventsRecyclerView.setAdapter(adapterClass);
                            }

                        }

                        public void onCancelled(@NonNull DatabaseError databaseError){
                            Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            //default branch
            default:
                break;

        }


    }
}