package com.ddit.project.supermarketcheckouter.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ddit.project.supermarketcheckouter.Models.Category_GetSet;
import com.ddit.project.supermarketcheckouter.R;

import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private ArrayList<Category_GetSet> mItems = new ArrayList<>();
    private ItemListener mListener;
    Context mContext;

    public CategoryListAdapter(Context context, ItemListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void additem(ArrayList<Category_GetSet> items){
        mItems = new ArrayList<>();
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_categorylist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setData(mItems.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(mItems.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView branch_name;

        private ViewHolder(View itemView) {
            super(itemView);
            branch_name = (TextView) itemView.findViewById(R.id.branch_name);
        }

        void setData(Category_GetSet item) {
            branch_name.setText(item.getCat_name());
        }
    }

    public interface ItemListener {
        void onItemClick(Category_GetSet item);
    }
}