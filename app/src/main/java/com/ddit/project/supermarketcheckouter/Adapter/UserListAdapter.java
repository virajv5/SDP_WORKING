package com.ddit.project.supermarketcheckouter.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ddit.project.supermarketcheckouter.Models.User;
import com.ddit.project.supermarketcheckouter.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private ArrayList<User> mItems = new ArrayList<>();
    Context mContext;

    public UserListAdapter(Context context) {
        mContext = context;
    }

    public void additem(ArrayList<User> items) {
        mItems = new ArrayList<>();
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_userlist_item, parent, false));
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

        CircleImageView user_image;
        TextView user_name;
        TextView user_email;

        private ViewHolder(View itemView) {
            super(itemView);
            user_image =  itemView.findViewById(R.id.user_image);
            user_name =  itemView.findViewById(R.id.user_name);
            user_email =  itemView.findViewById(R.id.user_email);
        }

        void setData(User item) {
            user_name.setText(item.getName());
            user_email.setText(item.getEmail());
            Glide.with(mContext).load(item.getPhotourl()).error(R.drawable.user_default).into(user_image);
        }
    }
}