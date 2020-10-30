package com.chat.pojo.VO;

/**
 *    我的好友
 */
public class MyFriendsVO {

    private String friendsUserId;
    private String friendsUsername;
    private String friendsFaceImage;
    private String friendsNickname;

    public String getFriendsNickname() {
        return friendsNickname;
    }

    public void setFriendsNickname(String friendsNickname) {
        this.friendsNickname = friendsNickname;
    }

    public String getFriendsUserId() {
        return friendsUserId;
    }

    public void setFriendsUserId(String friendsUserId) {
        this.friendsUserId = friendsUserId;
    }

    public String getFriendsUsername() {
        return friendsUsername;
    }

    public void setFriendsUsername(String friendsUsername) {
        this.friendsUsername = friendsUsername;
    }

    public String getFriendsFaceImage() {
        return friendsFaceImage;
    }

    public void setFriendsFaceImage(String friendsFaceImage) {
        this.friendsFaceImage = friendsFaceImage;
    }

}
