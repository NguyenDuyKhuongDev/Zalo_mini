package com.example.myapplication.model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class ChatGroupMessage {
    public String senderId;
    public String message;
    public String senderName;
    private String images;
    private String videos;
    public Timestamp dataTime;
    private String files, fileName, sizeFile;
    public ChatGroupMessage(String senderId, String message, String senderName, String images, String videos, Timestamp dataTime, String files, String fileName, String sizeFile) {
        this.senderId = senderId;
        this.message = message;
        this.senderName = senderName;
        this.images = images;
        this.dataTime = dataTime;
        this.files = files;
        this.sizeFile = sizeFile;
        this.fileName = fileName;
        this.videos = videos;
    }
    public ChatGroupMessage() {

    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getVideos() {
        return videos;
    }

    public void setVideos(String videos) {
        this.videos = videos;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSizeFile() {
        return sizeFile;
    }

    public void setSizeFile(String sizeFile) {
        this.sizeFile = sizeFile;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }



    public Timestamp getDataTime() {
        return dataTime;
    }

    public void setDataTime(Timestamp dataTime) {
        this.dataTime = dataTime;
    }


}
