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

    // Lấy ID user hiện tại - cx đơn giản thôi
    public static String currentUserID(){
        return FirebaseAuth.getInstance().getUid();
    }

    // Check xem đã login chưa - ko phức tạp gì đâu
    public static boolean isLoggedIn(){
        String userId = currentUserID();
        return userId != null; // viết gọn hơn thay vì if else dài dòng
    }

    // Get instance của Firestore - dùng mãi cx ko sai
    public static FirebaseFirestore db (){
        return FirebaseFirestore.getInstance();
    }

    // Lấy document của user hiện tại
    public static DocumentReference currentUserDetails(){
        return db().collection("user").document(currentUserID());
    }

    // Collection chứa tất cả user - dùng để query
    public static CollectionReference allUserCollectionReference(){
        return db().collection("user");
    }

    // Lấy thông tin user khác - truyền userId vào
    public static DocumentReference otherUserDetails(String userId){
        return allUserCollectionReference().document(userId);
    }

    @NonNull
    // Reference tới chatroom cụ thể
    public static DocumentReference getChatroomReference(String chatroomId){
        return db().collection("chatrooms").document(chatroomId);
    }

    // Reference tới group chat - tương tự chatroom nhưng khác collection
    public static DocumentReference getChatGroupReference(String chatGroupId){
        return db().collection("groups").document(chatGroupId);
    }

    // Collection tin nhắn trong chatroom - nested collection đó
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    // Tạo ID chatroom từ 2 userID - logic này hay đó, sort theo hashCode
    public static String getChatroomId(String userId1, String userId2){
        // So sánh hashCode để đảm bảo thứ tự consistent
        boolean isUser1First = userId1.hashCode() < userId2.hashCode();
        return isUser1First ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    // Collection tất cả chatroom - để query tìm kiếm
    public static CollectionReference allChatroomCollectionReference(){
        return db().collection("chatrooms");
    }

    // Lấy user còn lại trong chatroom - ko phải mình
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        String currentId = currentUserID();
        // Check user đầu tiên có phải mình ko
        boolean isFirstUserCurrent = userIds.get(0).equals(currentId);
        String otherUserId = isFirstUserCurrent ? userIds.get(1) : userIds.get(0);
        return allUserCollectionReference().document(otherUserId);
    }

    // Get user khác - đơn giản hơn function trên
    public static DocumentReference getOtherUser(String userId){
        return allUserCollectionReference().document(userId);
    }

    // Convert timestamp thành string giờ:phút
    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    // Convert timestamp thành string ngày/tháng/năm
    public static String DateToString(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    // Logout khỏi Firebase Auth
    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    // Storage reference cho ảnh đại diện của mình
    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pic")
                .child(currentUserID());
    }

    // Storage reference cho ảnh đại diện user khác
    public static StorageReference getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pic")
                .child(otherUserId);
    }

    // Upload ảnh trong chat - tạo tên file unique
    public static StorageReference putImageChat(){
        String fileName = currentUserID() + "_" + UUID.randomUUID().toString();
        return FirebaseStorage.getInstance().getReference()
                .child("images")
                .child("roomImages")
                .child(fileName);
    }

    // Upload ảnh trong group chat
    public static StorageReference putImageGroupChat(){
        String fileName = currentUserID() + "_" + UUID.randomUUID().toString();
        return FirebaseStorage.getInstance().getReference()
                .child("images")
                .child("groupImages")
                .child(fileName);
    }

    // Upload video trong chat
    public static StorageReference putVideoChat(){
        String fileName = currentUserID() + "_" + UUID.randomUUID().toString();
        return FirebaseStorage.getInstance().getReference()
                .child("videos")
                .child("roomVideos")
                .child(fileName);
    }

    // Upload video trong group chat
    public static StorageReference putVideoGroupChat(){
        String fileName = currentUserID() + "_" + UUID.randomUUID().toString();
        return FirebaseStorage.getInstance().getReference()
                .child("videos")
                .child("groupVideos")
                .child(fileName);
    }

    // Upload document/file trong chat
    public static StorageReference putDocuments(){
        String fileName = currentUserID() + "_" + UUID.randomUUID().toString();
        return FirebaseStorage.getInstance().getReference()
                .child("documents")
                .child("roomDocuments")
                .child(fileName);
    }

    // Upload document trong group chat
    public static StorageReference putDocumentsGroupChat(){
        String fileName = currentUserID() + "_" + UUID.randomUUID().toString();
        return FirebaseStorage.getInstance().getReference()
                .child("documents")
                .child("groupDocuments")
                .child(fileName);
    }

    // Upload ảnh story
    public static StorageReference putMediaImagesStory(){
        String fileName = currentUserID() + "_" + UUID.randomUUID().toString();
        return FirebaseStorage.getInstance().getReference()
                .child("MediaStory")
                .child("Images")
                .child(fileName);
    }

    // Upload video story
    public static StorageReference putMediaVideosStory(){
        String fileName = currentUserID() + "_" + UUID.randomUUID().toString();
        return FirebaseStorage.getInstance().getReference()
                .child("MediaStory")
                .child("Videos")
                .child(fileName);
    }

    // Collection chatrooms - để xóa chat
    public static CollectionReference deletechatCollection(){
        return db().collection("chatrooms");
    }

    // Collection tin nhắn để xóa - trong chatroom cụ thể
    public static CollectionReference deletechat(String chatroomId){
        return db().collection("chatrooms")
                .document(chatroomId)
                .collection("chats");
    }

    // Collection chờ kết bạn - pending friend requests
    public static CollectionReference waitFriend(String userId){
        return db().collection("user")
                .document(userId)
                .collection("wait_friends");
    }

    // Collection yêu cầu kết bạn - incoming friend requests
    public static CollectionReference requestFriend(String userId){
        return db().collection("user")
                .document(userId)
                .collection("request_friends");
    }

    // Collection bạn bè
    public static CollectionReference friend(String userId){
        return db().collection("user")
                .document(userId)
                .collection("friends");
    }

    // Check xem có đang chờ kết bạn ko
    public static CollectionReference checkWaitFriend(String userId){
        return waitFriend(userId); // dùng lại function trên cho gọn
    }

    // Check yêu cầu kết bạn
    public static CollectionReference checkRequestFriend(String userId){
        return requestFriend(userId); // dùng lại function trên
    }

    // Collection post story
    public static CollectionReference postStory(){
        return db().collection("post");
    }

    // Xóa post story cụ thể
    public static DocumentReference deletePostStory(String documentID){
        return postStory().document(documentID);
    }

    // Status của user
    public static DocumentReference status(String id){
        return db().collection("user").document(id);
    }

    // Collection nhắc nhở trong chatroom
    public static CollectionReference addRemind(String chatRoomId){
        return db().collection("chatrooms")
                .document(chatRoomId)
                .collection("remind");
    }

    // Collection groups
    public static CollectionReference groups(){
        return db().collection("groups");
    }

    // Tin nhắn trong group
    public static CollectionReference group_chats(String chatGroupId){
        return groups().document(chatGroupId).collection("messages");
    }

    // Member của group
    public static DocumentReference groupsMember(String id){
        return groups().document(id);
    }

    // Cập nhật lần đọc cuối trong chatroom
    public static DocumentReference updateLastRead(String documentId){
        return db().collection("chatrooms").document(documentId);
    }

    // Cập nhật lần đọc cuối trong group
    public static DocumentReference updateLastReadGroup(String documentId){
        return db().collection("groups").document(documentId);
    }

    // Document group cụ thể
    public static DocumentReference documentGroup(String documentId){
        return groups().document(documentId);
    }

    // Các key constants - dùng để map data
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