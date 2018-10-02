package com.example.kimea.myapplication;

public class GetFriendListItem {
    String userImg;
    String userNickname;

    public String getUserImgI(){
        return userImg;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public GetFriendListItem(String userImg, String userNickname){
        this.userImg = userImg;
        this.userNickname = userNickname;
    }
}
