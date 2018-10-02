package com.example.kimea.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.RecyclerViewHolder>{
    private ArrayList<GetFriendListItem> Item;

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        protected TextView userNickname;
        protected ImageView userImg;

        public RecyclerViewHolder(View view) {
            super(view);
            this.userImg = view.findViewById(R.id.userImg);
            this.userNickname = view.findViewById(R.id.userNickname);
        }
    }
    public FriendListAdapter(ArrayList<GetFriendListItem> list){
        this.Item = list;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friends, parent, false);

        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);
        return viewHolder;
    }
    public Bitmap byteArrayToBitmap(String jsonString) {
        Bitmap bitmap = null;
        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return bitmap;

    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        holder.userImg.setImageBitmap(byteArrayToBitmap(Item.get(position).getUserImgI()));
        holder.userNickname.setText(Item.get(position).getUserNickname());

    }

    @Override
    public int getItemCount() {
        return (null != Item ? Item.size() : 0);
    }
}
