package com.example.kimea.myapplication;

public class GetChatRoomItem {
    String userId;
    String lastChat;
    String chatImg;
    public String getUserId(){
        return userId;
    }
    public String getLastChat(){
        return lastChat;
    }
    public String getChatImg(){
        return chatImg;
    }
    public GetChatRoomItem(String userId, String lastChat,String chatImg){
        this.userId = userId;
        this.lastChat = lastChat;
        this.chatImg = chatImg;
    }
}
