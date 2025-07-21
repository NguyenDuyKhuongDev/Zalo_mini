package com.example.myapplication.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class GroupModel implements Serializable {
    public GroupModel(){

    }
    List<String> userIds;
    String adminID, groupName, lastMessage,lastUserIdSend , statusRead;
    Timestamp timestamp;

    public GroupModel(List<String> userIds, String adminID, String groupName, String lastMessage, String lastUserIdSend, Timestamp timestamp, String statusRead) {
        this.userIds = userIds;
        this.adminID = adminID;
        this.groupName = groupName;
        this.lastMessage = lastMessage;
        this.lastUserIdSend = lastUserIdSend;
        this.timestamp = timestamp;
        this.statusRead = statusRead;
    }

    public String getStatusRead() {
        return statusRead;
    }

    public void setStatusRead(String statusRead) {
        this.statusRead = statusRead;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastUserIdSend() {
        return lastUserIdSend;
    }

    public void setLastUserIdSend(String lastUserIdSend) {
        this.lastUserIdSend = lastUserIdSend;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
