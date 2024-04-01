package com.ddit.project.supermarketcheckouter.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ddit.project.supermarketcheckouter.Models.Product_GetSet;
import com.ddit.project.supermarketcheckouter.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {

    private ArrayList<Product_GetSet> mItems = new ArrayList<>();
    private ArrayList<Product_GetSet> mItemsFiltered = new ArrayList<>();

    private Context mContext;
    private clickcallback mCallback;

    public ProductAdapter(Context context, clickcallback callback) {
        mContext = context;
        mCallback = callback;
    }

    public void additem(ArrayList<Product_GetSet> items) {
        mItems = items;
        mItemsFiltered = new ArrayList<>(mItems);
        notifyDataSetChanged();
    }

    public void removeitem(int pos) {
        if (mItems != null && mItems.size() > pos) {
            mItems.remove(pos);
            mItemsFiltered = new ArrayList<>(mItems);
            notifyDataSetChanged();
        }
    }

    public void clearItems() {
        mItems.clear();
        mItemsFiltered.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_productlist_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mItemsFiltered.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (TextUtils.isEmpty(constraint)) {
                    results.values = mItems;
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    List<Product_GetSet> filteredList = new ArrayList<>();
                    for (Product_GetSet item : mItems) {
                        if (item.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                    results.values = filteredList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                mItemsFiltered = (ArrayList<Product_GetSet>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView edit_item_list;
        ImageView remove_item_list;
        ImageView product_image;
        TextView product_price;
        TextView product_name;

        ViewHolder(View itemView) {
            super(itemView);
            edit_item_list = itemView.findViewById(R.id.edit_item_list);
            remove_item_list = itemView.findViewById(R.id.remove_item_list);
            product_image = itemView.findViewById(R.id.product_image);
            product_price = itemView.findViewById(R.id.product_price);
            product_name = itemView.findViewById(R.id.product_name);
        }

        void setData(Product_GetSet item) {
            product_name.setText(item.getName());
            product_price.setText(item.getPrice());
            Glide.with(mContext).load(item.getImage()).error(R.drawable.qr_code_scan).into(product_image);

            edit_item_list.setOnClickListener(view -> mCallback.clickeditProduct(item, getAdapterPosition()));

            remove_item_list.setOnClickListener(view -> mCallback.clickdeleteProduct(item, getAdapterPosition()));
        }
    }

    public interface clickcallback {
        void clickeditProduct(Product_GetSet item, int pos);

        void clickdeleteProduct(Product_GetSet item, int pos);
    }
}
