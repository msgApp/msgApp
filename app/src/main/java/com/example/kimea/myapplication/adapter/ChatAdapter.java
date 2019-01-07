package com.example.kimea.myapplication.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kimea.myapplication.item.GetMessageItem;
import com.example.kimea.myapplication.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.RecyclerViewHolder> {

    private ArrayList<GetMessageItem> Item;

    public ChatAdapter(ArrayList<GetMessageItem> list) {
        this.Item = list;
    }

    // RecylerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, null);
        int layout = -1;
        switch (viewType){
            case GetMessageItem.TYPE_MESSAGE:
                layout = R.layout.item_message;
                break;
            case GetMessageItem.TYPE_MYMSG:
                layout = R.layout.itme_mychat;
                break;
            case GetMessageItem.TYPE_MYIMG:
                layout = R.layout.itme_mychatimg;
                break;
            case GetMessageItem.TYPE_IMG:
                layout = R.layout.item_messageimg;
                break;
        }
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);

        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);

        return viewHolder;
    }


    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder viewholder, int position) {
        GetMessageItem gm = Item.get(position);
        viewholder.name.setText(Item.get(position).getName());
        if (Item.get(position).getmType()<=1) {
            viewholder.message.setText(Item.get(position).getMessage());
        }
        if (Item.get(position).getmType()>1){
            viewholder.chatImg.setImageBitmap(Item.get(position).getBitmap());
        }
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        protected TextView message;
        protected TextView name;
        protected ImageView chatImg;
        public RecyclerViewHolder(View view) {
            super(view);
            this.chatImg = view.findViewById(R.id.chat_img);
            this.message = view.findViewById(R.id.setMsg);
            this.name = view.findViewById(R.id.username);
        }
    }

    @Override
    public int getItemCount() {
        return (null != Item ? Item.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return Item.get(position).getmType();
    }

}