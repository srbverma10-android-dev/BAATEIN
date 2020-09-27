package com.sourabh.baatein;

public class Post {

    public Post(String idOfOtherUser, String postOfOtherUser, String postId) {
        this.idOfOtherUser = idOfOtherUser;
        this.postOfOtherUser = postOfOtherUser;
        this.postId = postId;
    }

    public Post() {
    }

    public String getIdOfOtherUser() {
        return idOfOtherUser;
    }

    public void setIdOfOtherUser(String idOfOtherUser) {
        this.idOfOtherUser = idOfOtherUser;
    }

    public String getPostOfOtherUser() {
        return postOfOtherUser;
    }

    public void setPostOfOtherUser(String postOfOtherUser) {
        this.postOfOtherUser = postOfOtherUser;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    private String idOfOtherUser;
    private String postOfOtherUser;
    private String postId;

}
