package com.example.kimea.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.RecyclerViewHolder>{
    private ArrayList<GetFriendListItem> Item;

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        protected TextView userNickname;
        protected ImageView userImg;
        protected TextView email;
        protected TextView profileText;

        public RecyclerViewHolder(View view) {
            super(view);
            this.userImg = view.findViewById(R.id.userImg);
            this.userNickname = view.findViewById(R.id.userNickname);
            this.profileText = view.findViewById(R.id.profileText);
            this.email = view.findViewById(R.id.email);
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
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {

        holder.userImg.setImageBitmap(byteArrayToBitmap(Item.get(position).getUserImgI()));
        holder.userNickname.setText(Item.get(position).getUserNickname());
        holder.profileText.setText(Item.get(position).getProfileText());
        holder.email.setText(Item.get(position).getEmail());

        holder.userNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Drawable d = holder.userImg.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);

                Intent intent = new Intent(v.getContext(), FriendProfile.class);
                intent.putExtra("email", holder.email.getText());
                intent.putExtra("nickname", holder.userNickname.getText());
                intent.putExtra("profileText", holder.profileText.getText());
                intent.putExtra("img", bs.toByteArray());
                v.getContext().startActivity(intent);

            }
        });
        holder.userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable d = holder.userImg.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                Intent intent = new Intent(v.getContext(), FriendProfile.class);

                intent.putExtra("email", holder.email.getText());
                intent.putExtra("nickname", holder.userNickname.getText());
                intent.putExtra("profileText", holder.profileText.getText());
                intent.putExtra("img", bs.toByteArray());
                v.getContext().startActivity(intent);
            }
        });

        holder.profileText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable d = holder.userImg.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                Intent intent = new Intent(v.getContext(), FriendProfile.class);

                intent.putExtra("email", holder.email.getText());
                intent.putExtra("nickname", holder.userNickname.getText());
                intent.putExtra("profileText", holder.profileText.getText());
                intent.putExtra("img", bs.toByteArray());
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != Item ? Item.size() : 0);
    }
}
