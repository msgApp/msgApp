package com.example.kimea.myapplication;

public class GetFriendPopItem {
    String friendImg;
    String friendNickname;
    String friendEmail;
    Boolean isSelected = false;
    public String getFriendImg(){
        return friendImg;
    }

    public String getFriendNickname() {
        return friendNickname;
    }

    public String getFriendEmail(){return  friendEmail;}

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public GetFriendPopItem(String friendImg, String friendNickname, String friendEmail){
        this.friendImg = friendImg;
        this.friendNickname = friendNickname;
        this.friendEmail = friendEmail;
    }
}
