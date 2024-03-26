package com.ddit.project.supermarketcheckouter.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ddit.project.supermarketcheckouter.Models.Product_GetSet;
import com.ddit.project.supermarketcheckouter.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private ArrayList<Product_GetSet> mItems = new ArrayList<>();

    Context mContext;
    clickcallback mCallback;

    public ProductAdapter(Context context, clickcallback mback) {
        mContext = context;
        mCallback = mback;
    }

    public void additem(ArrayList<Product_GetSet> items) {
        mItems = new ArrayList<>();
        mItems = items;
        notifyDataSetChanged();
    }

    public void removeitem(int pos) {
        if (mItems != null && mItems.size() > 0) {
            mItems.remove(pos);
            notifyDataSetChanged();
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_productlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setData(mItems.get(position));
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView edit_item_list;
        ImageView remove_item_list;
        ImageView product_image;
        TextView product_price;
        TextView product_name;

        private ViewHolder(View itemView) {
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

            edit_item_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.clickeditProduct(item, getAdapterPosition());
                }
            });

            remove_item_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.clickdeleteProduct(item, getAdapterPosition());
                }
            });
        }
    }

    public interface clickcallback {
        public void clickeditProduct(Product_GetSet item, int pos);

        public void clickdeleteProduct(Product_GetSet item, int pos);
    }

    public void DeleteItem(int pos) {


    }
}