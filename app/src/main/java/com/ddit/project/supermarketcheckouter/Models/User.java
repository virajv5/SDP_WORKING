package com.ddit.project.supermarketcheckouter.Models;

public class User {

    private String userid;
    private String name;
    private String email;
    private String photourl;
    private String serverauthcode;
    private String reward;
    private String type;
    private String createdate;
    private String active;


    public User() {
    }

    public User(String userid, String name, String email, String photourl, String serverauthcode, String reward, String type, String createdate, String active) {
        this.userid = userid;
        this.name = name;
        this.email = email;
        this.photourl = photourl;
        this.serverauthcode = serverauthcode;
        this.reward = reward;
        this.reward = reward;
        this.type = type;
        this.createdate = createdate;
        this.active = active;

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getServerauthcode() {
        return serverauthcode;
    }

    public void setServerauthcode(String serverauthcode) {
        this.serverauthcode = serverauthcode;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
