package com.example.myapplication.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.model.PreferenceManager;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FirebaseUtil {

    public static  String currentUserID(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        if(currentUserID()!=null){
            return true;
        }
        return false;
    }
    public static FirebaseFirestore db (){
        return FirebaseFirestore.getInstance();
    }
    public static DocumentReference currentUserDetails(){

        return FirebaseFirestore.getInstance().collection("user").document(currentUserID());
    }
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("user");
    }
    public static DocumentReference otherUserDetails(String userId){
        return FirebaseFirestore.getInstance().collection("user").document(userId);
    }
    @NonNull
    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }
    public static DocumentReference getChatGroupReference(String chatGroupId){
        return FirebaseFirestore.getInstance().collection("groups").document(chatGroupId);
    }
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserID())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }
    public static DocumentReference getOtherUser(String userIds){
        return allUserCollectionReference().document(userIds);

    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }
    public static String DateToString(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference  getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserID());
    }

    public static StorageReference getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
    public static StorageReference putFile(){
        return FirebaseStorage.getInstance().getReference();
    }
    public static StorageReference putImageChat(){
        return putFile().child("images").child("roomImages").child(currentUserID()+"_"+UUID.randomUUID().toString());
    }
    public static StorageReference putImageGroupChat(){
        return putFile().child("images").child("groupImages").child(currentUserID()+"_"+UUID.randomUUID().toString());
    }
    public static StorageReference putVideoChat(){
        return putFile().child("videos").child("roomVideos").child(currentUserID()+"_"+UUID.randomUUID().toString());
    }
    public static StorageReference putVideoGroupChat(){
        return putFile().child("videos").child("groupVideos").child(currentUserID()+"_"+UUID.randomUUID().toString());
    }
    public static StorageReference putDocuments(){
        return putFile().child("documents").child("roomDocuments").child(currentUserID()+"_"+UUID.randomUUID().toString());
    }
    public static StorageReference putDocumentsGroupChat(){
        return putFile().child("documents").child("groupDocuments").child(currentUserID()+"_"+UUID.randomUUID().toString());
    }
    public static StorageReference putMediaImagesStory(){
        return putFile().child("MediaStory").child("Images").child(currentUserID()+"_"+UUID.randomUUID().toString());
    }
    public static StorageReference putMediaVideosStory(){
        return putFile().child("MediaStory").child("Videos").child(currentUserID()+"_"+UUID.randomUUID().toString());
    }
    public static StorageReference putAudios(String pathAudio){
        return putFile().child("audios").child("roomAudios").child(currentUserID()+"_"+pathAudio);
    }
    public static CollectionReference deletechatCollection(){
        return  FirebaseFirestore.getInstance().collection("chatrooms");
    }
    public static CollectionReference deletechat(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId).collection("chats");
    }
    public static CollectionReference waitFriend(String userId){
        return FirebaseFirestore.getInstance().collection("user").document(userId).collection("wait_friends");

    }
    public static CollectionReference requestFriend(String userId){
        return FirebaseFirestore.getInstance().collection("user").document(userId).collection("request_friends");

    }
    public  static CollectionReference friend(String userId){
        return FirebaseFirestore.getInstance().collection("user").document(userId).collection("friends");
    }
    public static CollectionReference checkWaitFriend(String userId){
        return FirebaseFirestore.getInstance().collection("user").document(userId).collection("wait_friends");
    }
    public static CollectionReference checkRequestFriend(String userId){
        return FirebaseFirestore.getInstance().collection("user").document(userId).collection("request_friends");

    }
    public static CollectionReference postStory(){
        return FirebaseFirestore.getInstance().collection("post");
    }
    public static DocumentReference deletePostStory(String documentID){
        return postStory().document(documentID);
    }
    public static DocumentReference status(String id){
        return FirebaseFirestore.getInstance().collection("user").document(id);
    }
    public static CollectionReference addRemind(String chatRoomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId).collection("remind");
    }
    public static CollectionReference groups(){
        return  FirebaseFirestore.getInstance().collection("groups");
    }
    public static CollectionReference group_chats(String chatGroupId){
        return groups().document(chatGroupId).collection("messages");
    }
    public static DocumentReference groupsMember(String id){
        return groups().document(id);
    }
    public  static CollectionReference groupMemberList(String id){
        return groups().document(id).collection("members");
    }

    public  static DocumentReference updateLastRead(String documentId){
        return  FirebaseFirestore.getInstance().collection("chatrooms").document(documentId);

    }
    public static DocumentReference updateLastReadGroup(String documentId){
        return FirebaseFirestore.getInstance().collection("groups").document(documentId);
    }
    public static DocumentReference documentGroup(String documentId){
        return FirebaseFirestore.getInstance().collection("groups").document(documentId);
    }

    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_TOKEN = "fcmToken";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_BIRTHDAY = "birthDay";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_SCHOOL = "school";
    public static final String KEY_RELATIONSHIP = "relationship";


}
