package com.example.greetmeet_v1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.MyViewHolder>{

    RecyclerViewClickListener listener;
    ArrayList<Group> list;
    Context mcontext;
    private ArrayList<Group> mData;
    private ArrayList<Group> mDataFull;
    public AdapterClass(ArrayList<Group> list,RecyclerViewClickListener listener){
        this.mData = list;
        mDataFull = new ArrayList<Group>(mData);
        this.list = list;
        this.listener = listener;
        //mcontext = context();
    }

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        mcontext = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row,viewGroup,false);
        return new MyViewHolder(view);
    }

    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        FirebaseUser fuser;
        Group group = list.get(i);
        myViewHolder.gName.setText(group.getGroupName());
        myViewHolder.gLoc.setText(group.getGroupLoc());
        myViewHolder.gDesc.setText(group.getGroupDesc());
        myViewHolder.gPic.setVisibility(View.INVISIBLE);
        Picasso.with(mcontext)
                .load(group.getGroupImgURL())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(myViewHolder.gPic, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                myViewHolder.gPic.setVisibility(View.VISIBLE);
                                Animations.fadeInAndShowImage(myViewHolder.gPic);
                            }

                            @Override
                            public void onError() {
                                myViewHolder.gPic.setVisibility(View.VISIBLE);
                            }
                        }
                );


        Users host = group.getHost();
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User").child(host.getId());
        myViewHolder.host_image.setVisibility(View.INVISIBLE);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (((Boolean) snapshot.child("profilePic").getValue()) && snapshot.child("imageURL").getValue() != null){
                    String imgUrl = snapshot.child("imageURL").getValue().toString();
                    Picasso.with(mcontext)
                            .load(imgUrl)
                            .placeholder(R.mipmap.ic_launcher)
                            .fit()
                            .centerCrop()
                            .into(myViewHolder.host_image, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            myViewHolder.host_image.setVisibility(View.VISIBLE);
                                            Animations.fadeInAndShowImage(myViewHolder.host_image);
                                        }

                                        @Override
                                        public void onError() {
                                            Toast.makeText(mcontext, "Image cannot be displayed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                }
                else{
                    myViewHolder.host_image.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


        public int getItemCount(){
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView gName, gCategory, gLoc,gDate,gDesc;
        ImageView gPic,host_image;
        LinearLayout parentLayout;
        //OnNoteListener onNoteListener;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            gName = itemView.findViewById(R.id.gName);
            gPic = itemView.findViewById(R.id.gPic);
            gLoc = itemView.findViewById(R.id.gLoc);
            gDesc = itemView.findViewById(R.id.gDesc);
            host_image = itemView.findViewById(R.id.host_image);

        }
        @Override
        public void onClick(View view){
            listener.onClick(view, getAdapterPosition());
        }
    }

    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Group> filteredList = new ArrayList<Group>();
            if (charSequence == null || charSequence.length() == 0){
                filteredList.addAll(mDataFull);
                Log.d("SearchAdapter", "No Search, Display Full List");
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Group item : mDataFull){
                    if (item.getGroupName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mData.clear();
            mData.addAll((ArrayList)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public interface RecyclerViewClickListener{
        void onClick(View v, int pos);
    }
}
