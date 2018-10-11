package com.example.kimea.myapplication;

public class GetFriendListItem {
    String userImg;
    String userNickname;
    String profileText;
    String email;

    public String getUserImgI(){
        return userImg;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public String getProfileText(){
        return profileText;
    }

    public String getEmail() {
        return email;
    }



    public GetFriendListItem(String userImg, String userNickname, String profileText, String email){
        this.userImg = userImg;
        this.userNickname = userNickname;
        this.profileText = profileText;
        this.email = email;
    }
}
