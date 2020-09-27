package com.sourabh.baatein;

import java.io.Serializable;

public class User implements Serializable {

    public User(String name, String id, String email, String imageUrl, String token) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.imageUrl = imageUrl;
        this.token = token;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String name;
    private String id;
    private String email;
    private String imageUrl;
    private String token;

}
