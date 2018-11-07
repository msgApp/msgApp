package com.example.kimea.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.RecyclerViewHolder>{
    private ArrayList<GetChatRoomItem> Item;
    private  OnSendItem mCallback;
    public interface OnSendItem {
        void sendIntent(String email);
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        protected TextView chatRoomId;
        protected ImageView chatUserImg;
        protected TextView chatLastText;
        protected TextView bagdeCount;

        public RecyclerViewHolder(View view) {
            super(view);
            this.chatUserImg = view.findViewById(R.id.chatUserImg);
            this.chatRoomId = view.findViewById(R.id.chatRoomId);
            this.chatLastText = view.findViewById(R.id.chatLastText);
            this.bagdeCount = view.findViewById(R.id.badge_notification);
        }
    }
    public ChatRoomAdapter(ArrayList<GetChatRoomItem> list,OnSendItem listner){
        this.Item = list;
        this.mCallback = listner;
    }

    @Override
    public ChatRoomAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chatroom, parent, false);

        ChatRoomAdapter.RecyclerViewHolder viewHolder = new ChatRoomAdapter.RecyclerViewHolder(view);
        return viewHolder;
    }

    public Bitmap byteArrayToBitmap(String jsonString) {
        Bitmap bitmap = null;
        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return bitmap;
    }

    @Override
    public void onBindViewHolder(final ChatRoomAdapter.RecyclerViewHolder holder, int position) {
        holder.bagdeCount.setText(Item.get(position).getBadge());
        if (Item.get(position).getBadge().equals("0")||Item.get(position).getBadge().equals("")) {
            holder.bagdeCount.setVisibility(View.GONE);
        }else{
            holder.bagdeCount.setVisibility(View.VISIBLE);
        }
        //holder.chatUserImg.setImageBitmap(byteArrayToBitmap(Item.get(position).getChatImg()));
        holder.chatRoomId.setText(Item.get(position).getUserId());
        holder.chatLastText.setText(Item.get(position).getLastChat());
        holder.chatRoomId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.sendIntent(holder.chatRoomId.getText().toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != Item ? Item.size() : 0);
    }

}
