package com.example.greetmeet_v1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<Group> mData;
    private ArrayList<Group> mDataFull;
    RecyclerViewClickListener listener;

    public SearchAdapter(ArrayList<Group> mData, RecyclerViewClickListener listener){
        this.mData = mData;
        this.listener = listener;
        mDataFull = new ArrayList<Group>(mData);
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Group group = mData.get(position);
        holder.gName.setText(group.getGroupName());
        holder.gPic.setVisibility(View.INVISIBLE);
        Picasso.with(mContext)
                .load(group.getGroupImgURL())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.gPic, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                holder.gPic.setVisibility(View.VISIBLE);
                                Animations.fadeInAndShowImage(holder.gPic);
                            }

                            @Override
                            public void onError() {
                                holder.gPic.setVisibility(View.VISIBLE);
                            }
                        }
                );
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
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

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView gName;
        ImageView gPic;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            gName = itemView.findViewById(R.id.gName);
            gPic = itemView.findViewById(R.id.gPic);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int pos);
    }
}
