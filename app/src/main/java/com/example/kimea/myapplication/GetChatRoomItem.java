package com.example.kimea.myapplication;

public class GetChatRoomItem {
    String userId;
    String lastChat;
    String chatImg;
    String badge;
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
    public GetChatRoomItem(String userId, String lastChat,String chatImg,String badge){
        this.userId = userId;
        this.lastChat = lastChat;
        this.chatImg = chatImg;
        this.badge = badge;
    }
}
