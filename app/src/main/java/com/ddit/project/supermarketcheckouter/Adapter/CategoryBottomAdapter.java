package com.ddit.project.supermarketcheckouter.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ddit.project.supermarketcheckouter.Models.Category_GetSet;
import com.ddit.project.supermarketcheckouter.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryBottomAdapter extends RecyclerView.Adapter<CategoryBottomAdapter.ViewHolder> implements Filterable {

    private ArrayList<Category_GetSet> mItems = new ArrayList<>();
    private List<Category_GetSet> branchListFiltered = new ArrayList<>();
    private ItemListener mListener;
    Context mContext;

    public CategoryBottomAdapter(Context context, ItemListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void additem(ArrayList<Category_GetSet> items){
        mItems = new ArrayList<>();
        branchListFiltered = new ArrayList<>();
        mItems = items;
        branchListFiltered = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setData(branchListFiltered.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(branchListFiltered.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return branchListFiltered.size();
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                Log.e("callingbranchfilter","  ::  "+ charString);
                if (charString.isEmpty()) {
                    Log.e("callingbranchfilter","  ::  isempty");
                    branchListFiltered = mItems;
                } else {
                    Log.e("callingbranchfilter","  ::  non empty");
                    List<Category_GetSet> filteredList = new ArrayList<>();
                    for (Category_GetSet row : mItems) {
                        if (row.getCat_name().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    branchListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = branchListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                branchListFiltered = (ArrayList<Category_GetSet>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }
}