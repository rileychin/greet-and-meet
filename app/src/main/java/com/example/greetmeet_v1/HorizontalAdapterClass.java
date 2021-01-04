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

public class HorizontalAdapterClass extends RecyclerView.Adapter<HorizontalAdapterClass.MyViewHolder> {

    HorizontalAdapterClass.RecyclerViewClickListener listener;
    ArrayList<Group> list;
    Context mcontext;

    public HorizontalAdapterClass(ArrayList<Group> list, HorizontalAdapterClass.RecyclerViewClickListener listener){
        this.list = list;
        this.listener = listener;
        //mcontext = context();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mcontext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalAdapterClass.MyViewHolder holder, int position) {
        Group group = list.get(position);
        holder.gName2.setText(list.get(position).getGroupName());
        holder.gPic2.setVisibility(View.INVISIBLE);
        Picasso.with(mcontext)
                .load(group.getGroupImgURL())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.gPic2, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                holder.gPic2.setVisibility(View.VISIBLE);
                                Animations.fadeInAndShowImage(holder.gPic2);
                            }

                            @Override
                            public void onError() {
                                holder.gPic2.setVisibility(View.INVISIBLE);
                            }
                        }
                );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView gName2;
        ImageView gPic2;
        LinearLayout parentLayout;
        //OnNoteListener onNoteListener;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            gName2 = itemView.findViewById(R.id.gName2);
            parentLayout = itemView.findViewById(R.id.cardId2);
            gPic2 = itemView.findViewById(R.id.gPic2);
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
