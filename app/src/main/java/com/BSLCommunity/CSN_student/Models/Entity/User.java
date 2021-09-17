package com.BSLCommunity.CSN_student.Models.Entity;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("NickName")
    private String nickName;
    @SerializedName("Password")
    private String password;
    @SerializedName("group_id")
    private int groupId;
    @SerializedName("GroupName")
    private String groupName;
    @SerializedName("Course")
    private int course;
    @SerializedName("token")
    private String token;

    public User() {
        this.nickName = "";
        this.password = "";
        this.groupId = -1;
        this.groupName = "";
        this.course = -1;
        this.token = "";
    }

    public String getNickName() {
        return nickName;
    }

    public String getPassword() {
        return password;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getCourse() {
        return course;
    }

    public String getToken() {
        return token;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
