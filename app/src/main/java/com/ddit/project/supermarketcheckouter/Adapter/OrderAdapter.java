package com.ddit.project.supermarketcheckouter.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ddit.project.supermarketcheckouter.Models.Order_GetSet;
import com.ddit.project.supermarketcheckouter.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private ArrayList<Order_GetSet> mItems = new ArrayList<>();

    Context mContext;
    clickcallback mCallback;

    public OrderAdapter(Context context, clickcallback mback) {
        mContext = context;
        mCallback = mback;
    }

    public void additem(ArrayList<Order_GetSet> items) {
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_item, parent, false));
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


        TextView approve_tv;
        TextView ondate_tv;
        TextView ref_id;
        TextView order_status;
        TextView amount;
        TextView order_id;

        private ViewHolder(View itemView) {
            super(itemView);
            approve_tv = itemView.findViewById(R.id.approve_tv);
            ondate_tv = itemView.findViewById(R.id.ondate_tv);
            ref_id = itemView.findViewById(R.id.ref_id);
            order_status = itemView.findViewById(R.id.order_status);
            amount = itemView.findViewById(R.id.amount);
            order_id = itemView.findViewById(R.id.order_id);
        }

        void setData(Order_GetSet item) {
            ondate_tv.setText(item.getOndate() + "");
            ref_id.setText(item.getPayment_refid() + "");
            amount.setText(item.getTotal_amount());
            order_id.setText(item.getOrder_id());
            order_status.setText(item.getPayment_status());
            if (item.getPayment_status().equals("success")) {
                order_status.setTextColor(mContext.getResources().getColor(R.color.colorSuccess));
            } else {
                order_status.setTextColor(mContext.getResources().getColor(R.color.colorError));
            }

        }
    }

    public interface clickcallback {
        public void clickDetailsOrder(Order_GetSet item, int pos);

    }

    public void DeleteItem(int pos) {


    }
}