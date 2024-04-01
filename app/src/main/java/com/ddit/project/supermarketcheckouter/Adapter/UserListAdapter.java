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
    private Context mContext;
    private OnUserClickListener mListener;

    public UserListAdapter(Context context, OnUserClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void addItems(ArrayList<User> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_userlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView userImage;
        TextView userName;
        TextView userEmail;

        ViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            itemView.setOnClickListener(this);
        }

        void setData(User item) {
            userName.setText(item.getName());
            userEmail.setText(item.getEmail());
            Glide.with(mContext).load(item.getPhotourl()).error(R.drawable.user_default).into(userImage);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mListener.onUserClick(mItems.get(position));
            }
        }
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }
}
