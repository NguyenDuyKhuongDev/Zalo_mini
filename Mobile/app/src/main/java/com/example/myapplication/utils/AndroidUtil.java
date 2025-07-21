package com.example.myapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class AndroidUtil {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("fcmToken", model.getFcmToken());
        intent.putExtra("address", model.getAddress());
        intent.putExtra("birthDay", model.getBirthDay());
        intent.putExtra("gender", model.getGender());
        intent.putExtra("school", model.getSchool());
        intent.putExtra("relationship", model.getRelationship());

    }

    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));

        userModel.setAddress(intent.getStringExtra("address"));
        userModel.setBirthDay(intent.getStringExtra("birthDay"));
        userModel.setGender(intent.getStringExtra("gender"));
        userModel.setSchool(intent.getStringExtra("school"));
        userModel.setRelationship(intent.getStringExtra("relationship"));
        return userModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
    public static void setImagePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).placeholder(R.drawable.anhbia1).into(imageView);
    }

}