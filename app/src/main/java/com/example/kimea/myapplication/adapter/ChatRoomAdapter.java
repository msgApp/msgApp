package com.example.kimea.myapplication.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kimea.myapplication.ChatRoomActivity;
import com.example.kimea.myapplication.item.GetChatRoomItem;
import com.example.kimea.myapplication.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.RecyclerViewHolder>{
    private ArrayList<GetChatRoomItem> Item;
    private  OnSendItem mCallback;

    public interface OnSendItem {
        void sendIntent(String email, String room, String roomNick);
        void deleteRoom(JSONObject jsonObject);
    }
    public ChatRoomAdapter(ArrayList<GetChatRoomItem> list,OnSendItem listner){
        this.Item = list;
        this.mCallback = listner;
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        protected TextView chatRoomId;
        protected ImageView chatUserImg;
        protected TextView chatLastText;
        protected TextView bagdeCount;
        protected TextView roomname;
        protected TextView roomNickName;



        public RecyclerViewHolder(View view) {
            super(view);
            this.chatUserImg = view.findViewById(R.id.chatUserImg);
            this.chatRoomId = view.findViewById(R.id.chatRoomId);
            this.chatLastText = view.findViewById(R.id.chatLastText);
            this.bagdeCount = view.findViewById(R.id.badge_notification);
            this.roomname = view.findViewById(R.id.roomname);
            this.roomNickName = view.findViewById(R.id.roomNickName);

            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            MenuItem delete = contextMenu.add(Menu.NONE, 1001, 1,"방 나가기");
            delete.setOnMenuItemClickListener(onMenu);
        }

        private final MenuItem.OnMenuItemClickListener onMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case 1001:
                        String room = Item.get(getAdapterPosition()).getRoomname();
                        String userEmail = Item.get(getAdapterPosition()).getEmail();


                        try{
                            ChatRoomActivity chatRoomActivity = new ChatRoomActivity();
                            JSONObject delete = new JSONObject();
                            delete.put("u_email", userEmail);
                            delete.put("room", room);

                            mCallback.deleteRoom(delete);

                            Item.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            notifyItemRangeChanged(getAdapterPosition(), Item.size());
                            //((ViewPagerActivity)ViewPagerActivity.CONTEXT).reset();

                        }catch (Exception e){
                        }
                        break;


                }
                return true;
            }
        };
    }


    @Override
    public ChatRoomAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chatroom, parent, false);

        ChatRoomAdapter.RecyclerViewHolder viewHolder = new ChatRoomAdapter.RecyclerViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ChatRoomAdapter.RecyclerViewHolder holder, final int position) {
        holder.bagdeCount.setText(Item.get(position).getBadge());
        if (Item.get(position).getBadge().equals("0")||Item.get(position).getBadge().equals("")) {
            holder.bagdeCount.setVisibility(View.GONE);
        }else{
            holder.bagdeCount.setVisibility(View.VISIBLE);
        }
        //holder.chatUserImg.setImageBitmap(byteArrayToBitmap(Item.get(position).getChatImg()));
        holder.chatRoomId.setText(Item.get(position).getRoomNickName());
        holder.roomNickName.setText(Item.get(position).getRoomname());
        holder.chatLastText.setText(Item.get(position).getLastChat());
        holder.roomname.setText(Item.get(position).getUserId());
        holder.chatRoomId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.sendIntent(holder.roomname.getText().toString(), holder.roomNickName.getText().toString(), holder.chatRoomId.getText().toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != Item ? Item.size() : 0);
    }

}