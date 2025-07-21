package com.example.myapplication.model;

import java.io.Serializable;
import java.util.Date;

public class PostStoryModel {
  String idAuthor, nameAuthor,phoneAuthor, postMedia, postText, imageStatus, videoStatus;
  Date postTimestamp;

    public String getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(String idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getNameAuthor() {
        return nameAuthor;
    }

    public void setNameAuthor(String nameAuthor) {
        this.nameAuthor = nameAuthor;
    }

    public String getPhoneAuthor() {
        return phoneAuthor;
    }

    public void setPhoneAuthor(String phoneAuthor) {
        this.phoneAuthor = phoneAuthor;
    }

    public String getPostMedia() {
        return postMedia;
    }

    public void setPostMedia(String postMedia) {
        this.postMedia = postMedia;
    }

    public String getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(String imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(String videoStatus) {
        this.videoStatus = videoStatus;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public Date getPostTimestamp() {
        return postTimestamp;
    }

    public void setPostTimestamp(Date postTimestamp) {
        this.postTimestamp = postTimestamp;
    }

    public PostStoryModel(){

  }

    public PostStoryModel(String idAuthor, String nameAuthor, String phoneAuthor, String postMedia, String postText, String imageStatus, String videoStatus, Date postTimestamp) {
        this.idAuthor = idAuthor;
        this.nameAuthor = nameAuthor;
        this.phoneAuthor = phoneAuthor;
        this.postMedia = postMedia;
        this.postText = postText;
        this.imageStatus = imageStatus;
        this.videoStatus = videoStatus;
        this.postTimestamp = postTimestamp;
    }

}
