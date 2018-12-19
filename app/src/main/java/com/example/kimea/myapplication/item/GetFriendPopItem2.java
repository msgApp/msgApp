package com.example.kimea.myapplication.item;

public class GetFriendPopItem2 {
    String sFriendImg;
    String sFriendNickname;
    String sFriendEmail;
    public String getSFriendImg(){
        return sFriendImg;
    }

    public String getSFriendNickname() {
        return sFriendNickname;
    }

    public String getSFriendEmail(){return  sFriendEmail;}

    public GetFriendPopItem2(String friendImg, String friendNickname, String friendEmail){
        this.sFriendImg = friendImg;
        this.sFriendNickname = friendNickname;
        this.sFriendEmail = friendEmail;
    }
}
