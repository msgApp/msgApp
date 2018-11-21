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

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FriendPopAdapter2 extends RecyclerView.Adapter<FriendPopAdapter2.RecyclerViewHolder> {
    private ArrayList<GetFriendPopItem2> sfpItem;
    private ArrayList<GetFriendPopItem> fpItem;
    private static final String TAG = "FriendPopAdapter2";
    private OnSendPop mCallback;
    public interface OnSendPop {
        void refresh();
        void setVisibility();
        void listdel(String email);
    }

    public FriendPopAdapter2 (ArrayList<GetFriendPopItem2> list, ArrayList<GetFriendPopItem> list2, OnSendPop listner){
        this.sfpItem = list;
        this.fpItem = list2;
        this.mCallback = listner;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_pop_select, parent, false);
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
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.pfImg.setImageBitmap(byteArrayToBitmap(sfpItem.get(position).sFriendImg));
        holder.email.setText(sfpItem.get(position).sFriendEmail);
        holder.nick.setText(sfpItem.get(position).sFriendNickname);
        holder.xButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    sfpItem.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, sfpItem.size());
                    int size = fpItem.size();
                    for (int i=0;i<size;i++){
                        if(fpItem.get(i).friendEmail.equals(holder.email.getText().toString())){
                            fpItem.get(i).setSelected(false);
                            mCallback.listdel(fpItem.get(i).friendEmail);
                            mCallback.refresh();
                        }
                    }
                    if (sfpItem.size()==0){
                       mCallback.setVisibility();
                    }


            }
        });

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private ImageView xButton;
        private ImageView pfImg;
        private TextView nick;
        private TextView email;
        public RecyclerViewHolder(View view) {
            super(view);
            xButton = view.findViewById(R.id.xButton);
            pfImg = view.findViewById(R.id.sFpImg);
            nick = view.findViewById(R.id.sFpNickName);
            email = view.findViewById(R.id.sFpEmail);
        }

    }
    @Override
    public int getItemCount() {
        return (null != sfpItem ? sfpItem.size() : 0);
    }

}
