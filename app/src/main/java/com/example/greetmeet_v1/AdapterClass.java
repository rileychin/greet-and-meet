package com.example.greetmeet_v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.MyViewHolder>{

    RecyclerViewClickListener listener;
    ArrayList<Group> list;
    Context mcontext;
    public AdapterClass(ArrayList<Group> list,RecyclerViewClickListener listener){
        this.list = list;
        this.listener = listener;
        //mcontext = context();
    }

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        mcontext = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row,viewGroup,false);
        return new MyViewHolder(view);
    }

    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i){
        Group group = list.get(i);
        myViewHolder.gName.setText(group.getGroupName());
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
    }
    public int getItemCount(){
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView gName, gCategory, gLoc;
        ImageView gPic;
        LinearLayout parentLayout;
        //OnNoteListener onNoteListener;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            gName = itemView.findViewById(R.id.gName);
            gPic = itemView.findViewById(R.id.gPic);

        }
        @Override
        public void onClick(View view){
            listener.onClick(view, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int pos);
    }
}
