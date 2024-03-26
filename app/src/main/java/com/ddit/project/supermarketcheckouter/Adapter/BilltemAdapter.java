package com.ddit.project.supermarketcheckouter.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ddit.project.supermarketcheckouter.CartDbHandler;
import com.ddit.project.supermarketcheckouter.Models.CartItem_GetSet;
import com.ddit.project.supermarketcheckouter.R;

import java.util.ArrayList;

public class BilltemAdapter extends RecyclerView.Adapter<BilltemAdapter.ViewHolder> {

    private ArrayList<CartItem_GetSet> mItems = new ArrayList<>();

    Context mContext;
    CartDbHandler db_cart;

    public BilltemAdapter(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
        db_cart = new CartDbHandler(context);
        mItems.addAll(db_cart.getAllCartLists());
    }

    public void removeitem(int pos) {
        if (mItems != null && mItems.size() > 0) {
            mItems.remove(pos);
            notifyDataSetChanged();
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_product, parent, false));
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

        TextView bill_pr_name;
        TextView bill_pr_price;
        TextView bill_pr_qty;
        TextView bill_pr_total;

        private ViewHolder(View itemView) {
            super(itemView);
            bill_pr_name = itemView.findViewById(R.id.bill_pr_name);
            bill_pr_price = itemView.findViewById(R.id.bill_pr_price);
            bill_pr_qty = itemView.findViewById(R.id.bill_pr_qty);
            bill_pr_total = itemView.findViewById(R.id.bill_pr_total);
        }


        void setData(CartItem_GetSet item) {
            int total_item = Integer.parseInt(item.getProduct_items());
            bill_pr_name.setText(item.getName());
            bill_pr_price.setText(item.getPrice());
            bill_pr_qty.setText(item.getProduct_items());
            float tot_price = (Float.parseFloat(item.getProduct_items()) * Float.parseFloat(item.getPrice()));
            bill_pr_total.setText(String.valueOf(tot_price));
        }
    }

    public interface clickcallback {
        public void clickTotalAmount(ArrayList<CartItem_GetSet> list);

    }
}