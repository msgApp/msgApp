package com.example.kimea.myapplication;

public class GetChatRoomItem {
    String userId;
    String lastChat;
    String chatImg;
    String badge;
    String roomname;
    public String getUserId(){
        return userId;
    }
    public String getLastChat(){
        return lastChat;
    }
    public String getChatImg(){
        return chatImg;
    }
    public String getBadge(){
        return badge;
    }
    public String getRoomname(){
        return roomname;
    }
    public GetChatRoomItem(String userId, String lastChat,String chatImg,String badge,String roomname){
        this.userId = userId;
        this.lastChat = lastChat;
        this.chatImg = chatImg;
        this.badge = badge;
        this.roomname = roomname;
    }
}
