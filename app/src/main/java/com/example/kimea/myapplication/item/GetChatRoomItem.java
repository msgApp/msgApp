package com.example.kimea.myapplication.item;

public class GetChatRoomItem {
    String userId;
    String lastChat;
    String chatImg;
    String badge;
    String roomname;
    String email;
    String roomNickName;
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
    public String getEmail(){
        return email;
    }
    public String getRoomNickName() {
        return roomNickName;
    }
    public GetChatRoomItem(String userId, String lastChat,String chatImg,String badge,String roomname,String email,String roomNickName){
        this.userId = userId;
        this.lastChat = lastChat;
        this.chatImg = chatImg;
        this.badge = badge;
        this.roomname = roomname;
        this.email = email;
        this.roomNickName = roomNickName;
    }
}
