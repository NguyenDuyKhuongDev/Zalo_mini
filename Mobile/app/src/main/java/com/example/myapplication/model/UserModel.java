package com.example.myapplication.model;

import com.example.myapplication.utils.FirebaseUtil;
import com.google.firebase.Timestamp;

import java.util.HashMap;

public class UserModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;
    private String status;
    private String birthDay;
    private String address;
    private String school;
    private String gender;
    private String relationship;
    private String information;

    public UserModel(String phone, String username, Timestamp createdTimestamp, String userId, String fcmToken, String status, String birthDay, String address, String school, String gender, String relationship, String information) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.status = status;
        this.birthDay = birthDay;
        this.address = address;
        this.school = school;
        this.gender = gender;
        this.relationship = relationship;
        this.information = information;
    }

    public UserModel() {

    }

    public UserModel(HashMap<String, Object> userData) {
        if (userData != null) {
            this.userId = (String) userData.get(FirebaseUtil.KEY_USER_ID);
            this.username = (String) userData.get(FirebaseUtil.KEY_USER_NAME);
            this.phone = (String) userData.get(FirebaseUtil.KEY_PHONE);
            this.fcmToken = (String) userData.get(FirebaseUtil.KEY_TOKEN);
        }
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
