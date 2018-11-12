package com.example.kimea.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

public class FriendPopAdapter extends RecyclerView.Adapter<FriendPopAdapter.RecyclerViewHolder> {
    private ArrayList<GetFriendPopItem> fpItem;
    private OnSendPop mCallback;
    public interface OnSendPop {
        void listdel(String email);
        void listAdd(String email);
    }

    public FriendPopAdapter (ArrayList<GetFriendPopItem> list, OnSendPop listner){
        this.fpItem = list;
        this.mCallback = listner;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friendpop, parent, false);
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
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        final GetFriendPopItem gm  = fpItem.get(position);
        holder.checkResult.setText("Checkbox"+position);
        holder.checkResult.setChecked(gm.getSelected());

        holder.friendNickName.setText(fpItem.get(position).getFriendNickname());
        holder.friendImg.setImageBitmap(byteArrayToBitmap(fpItem.get(position).getFriendImg()));
        holder.friendEmail.setText(fpItem.get(position).getFriendEmail());

        holder.checkResult.setTag(position);
        holder.checkResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer pos = (Integer) holder.checkResult.getTag();
                if (fpItem.get(pos).getSelected()) {
                    fpItem.get(pos).setSelected(false);
                    mCallback.listdel(fpItem.get(pos).getFriendEmail());
                } else {
                    Log.i("check","chekcEmail : "+fpItem.get(pos).getFriendEmail());
                    fpItem.get(pos).setSelected(true);
                    mCallback.listAdd(fpItem.get(pos).getFriendEmail());
                }
            }
        });

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        protected ImageView friendImg;
        protected TextView friendNickName;
        protected TextView friendEmail;
        private CheckBox checkResult;

        public RecyclerViewHolder(View view) {
            super(view);
            checkResult = (CheckBox) view.findViewById(R.id.inviteCheck);
            friendImg = view.findViewById(R.id.fpImg);
            friendNickName = view.findViewById(R.id.fpNickName);
            friendEmail = view.findViewById(R.id.fpEmail);
        }

    }
    @Override
    public int getItemCount() {
        return (null != fpItem ? fpItem.size() : 0);
    }

}
