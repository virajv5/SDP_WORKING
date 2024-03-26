package com.ddit.project.supermarketcheckouter.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ddit.project.supermarketcheckouter.CartDbHandler;
import com.ddit.project.supermarketcheckouter.Constant;
import com.ddit.project.supermarketcheckouter.Models.CartItem_GetSet;
import com.ddit.project.supermarketcheckouter.R;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private ArrayList<CartItem_GetSet> mItems = new ArrayList<>();

    Context mContext;
    clickcallback mCallback;
    CartDbHandler db_cart;

    public CartItemAdapter(Context context, clickcallback mback) {
        mContext = context;
        mItems = new ArrayList<>();
        db_cart = new CartDbHandler(context);
        mItems.addAll(db_cart.getAllCartLists());
        mCallback = mback;
    }

    public void removeitem(int pos) {
        if (mItems != null && mItems.size() > 0) {
            mItems.remove(pos);
            mCallback.clickTotalAmount(mItems);
            notifyDataSetChanged();
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false));
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

        TextView item_minus;
        TextView item_count;
        TextView item_plus;
        ImageView remove_item_list;
        ImageView product_image;
        TextView product_price;
        TextView product_name;

        private ViewHolder(View itemView) {
            super(itemView);
            item_minus = itemView.findViewById(R.id.item_minus);
            item_count = itemView.findViewById(R.id.item_count);
            item_plus = itemView.findViewById(R.id.item_plus);
            remove_item_list = itemView.findViewById(R.id.remove_item_list);
            product_image = itemView.findViewById(R.id.product_image);
            product_price = itemView.findViewById(R.id.product_price);
            product_name = itemView.findViewById(R.id.product_name);
        }


        void setData(CartItem_GetSet item) {
            int total_item = Integer.parseInt(item.getProduct_items());
            product_name.setText(item.getName());
            product_price.setText(item.getPrice());
            item_count.setText(item.getProduct_items());
            Glide.with(mContext).load(item.getImage()).error(R.drawable.qr_code_scan).into(product_image);

            item_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int total_item = Integer.parseInt(item.getProduct_items());
                    if (total_item > 1) {
                        total_item--;
                        item.setProduct_items(String.valueOf(total_item));
                        mItems.get(getAdapterPosition()).setProduct_items(String.valueOf(total_item));
                        db_cart.updateCartItem(item);
                        item_count.setText(total_item + "");
                        mCallback.clickTotalAmount(mItems);
                    } else {
                        Toast.makeText(mContext, "Please Click on Trash to Remove Items", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            item_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int total_item = Integer.parseInt(item.getProduct_items());
                    total_item++;
                    item.setProduct_items(String.valueOf(total_item));
                    mItems.get(getAdapterPosition()).setProduct_items(String.valueOf(total_item));
                    db_cart.updateCartItem(item);
                    item_count.setText(total_item + "");
                    mCallback.clickTotalAmount(mItems);
                }
            });

            remove_item_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Constant.show_dialog_common(mContext, "Remove",
                            "You Want to Remove?", "Yes", "0",
                            "error", "0", "", new Constant.calling_dialogaction() {
                                @Override
                                public void call_action() {
                                    db_cart.deleteCartItem(item);
                                    removeitem(getAdapterPosition());
                                }
                            });
                }
            });
        }
    }

    public interface clickcallback {
        public void clickTotalAmount(ArrayList<CartItem_GetSet> list);

    }
}