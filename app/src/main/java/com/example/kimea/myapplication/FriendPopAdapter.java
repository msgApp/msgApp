package com.example.kimea.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FriendPopAdapter extends RecyclerView.Adapter<FriendPopAdapter.RecyclerViewHolder> {
    private ArrayList<GetFriendPopItem> fpItem;
    private ArrayList<GetFriendPopItem2> sfpItem;
    private OnSendPop mCallback;
    public interface OnSendPop {
        void listdel(String email);
        void listAdd(String email);
        void addSelectList(String img, String email, String nick);
        void refresh();
        void setVisibility();
    }

    public FriendPopAdapter (ArrayList<GetFriendPopItem> list, ArrayList<GetFriendPopItem2> list2, OnSendPop listner){
        this.fpItem = list;
        this.sfpItem = list2;
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

                    int size = sfpItem.size();
                    for (int i=0;i<size;i++){
                        if(sfpItem.get(i).sFriendEmail.equals(holder.friendEmail.getText().toString())){
                            sfpItem.remove(i);
                            mCallback.refresh();
                            break;
                        }
                    }
                    if(sfpItem.size()==0){
                        mCallback.setVisibility();
                    }

                } else {
                    fpItem.get(pos).setSelected(true);
                    mCallback.listAdd(fpItem.get(pos).getFriendEmail());

                    Drawable d = holder.friendImg.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bs);
                    byte[] photo = bs.toByteArray();
                    String encodeImg = Base64.encodeToString(photo, Base64.DEFAULT);

                    mCallback.addSelectList(encodeImg,holder.friendEmail.getText().toString(),holder.friendNickName.getText().toString());
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
            friendImg = view.findViewById(R.id.sFpImg);
            friendNickName = view.findViewById(R.id.sFpNickName);
            friendEmail = view.findViewById(R.id.fpEmail);
        }

    }
    @Override
    public int getItemCount() {
        return (null != fpItem ? fpItem.size() : 0);
    }

}
