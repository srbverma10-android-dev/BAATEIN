package com.sourabh.baatein;

public class Notification {

    public Notification(String num, String image) {
        this.num = num;
        this.image = image;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String num;
    private String image;

}
