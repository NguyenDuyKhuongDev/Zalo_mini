package com.example.myapplication.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;

public class GroupAddUserSuRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, GroupAddUserSuRecyclerAdapter.GroupSearchModelViewHolder> {
    private PreferenceManager preferenceManager;
    Context context;

    String documentId;

    public GroupAddUserSuRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, String documentId, Context context) {
        super(options);
        this.context = context;
        this.documentId = documentId;
    }



    @Override
    protected void onBindViewHolder(@NonNull GroupSearchModelViewHolder holder, int position, @NonNull UserModel model) {

        preferenceManager =new PreferenceManager(context);
        Toast.makeText(context, documentId, Toast.LENGTH_SHORT).show();
        holder.usernameTxt.setText(model.getUsername());
        holder.phoneTxt.setText(model.getPhone());
        FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl().
                addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Uri uri = task1.getResult();
                        AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                    }

                });
        holder.successAddMemberInGroup.setOnClickListener(v -> {
            showToast("Người này đã ở trong nhóm rồi");
        });
        holder.addUserGroupBtn.setOnClickListener(v -> {
            HashMap<String, Object> otherUser = new HashMap<>();
            otherUser.put(FirebaseUtil.KEY_USER_ID, model.getUserId());
            otherUser.put(FirebaseUtil.KEY_USER_NAME, model.getUsername());
            otherUser.put(FirebaseUtil.KEY_PHONE, model.getPhone());
            otherUser.put(FirebaseUtil.KEY_TOKEN, model.getFcmToken());
            FirebaseUtil.groups().document(documentId).collection("members").document(model.getUserId()).set(otherUser).addOnSuccessListener(documentReference1 -> {

                HashMap<String, Object> message = new HashMap<>();
                message.put("senderId", "##########################~~");
                message.put("senderName", "Admin");
                message.put("senderImage", "Admin");
                message.put("message", model.getUsername() + " vừa được thêm vào nhóm.");
                message.put("dataTime", Timestamp.now());
                message.put("files", "0");
                message.put("images", "0");
                message.put("videos", "0");
                FirebaseUtil.group_chats(documentId).add(message);


                HashMap<String, Object> messUpdate = new HashMap<>();
                messUpdate.put("lastMessage", model.getUsername()+" vừa được thêm vào nhóm.");
                messUpdate.put("timestamp", Timestamp.now());
                messUpdate.put("userIds", FieldValue.arrayUnion(model.getUserId()));
                FirebaseUtil.groups().document(documentId).update(messUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(context, "Thêm vào nhóm thành công", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Thêm thất bại"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

    }

    @NonNull
    @Override
    public GroupSearchModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_group_search_row, parent, false);
        return new GroupAddUserSuRecyclerAdapter.GroupSearchModelViewHolder(view);
    }
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    static class GroupSearchModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTxt, phoneTxt, addUserGroupBtn, successAddMemberInGroup;
        ImageView profilePic;
        public GroupSearchModelViewHolder(@NonNull View itemView) {
            super(itemView);
            successAddMemberInGroup = itemView.findViewById(R.id.successAddMemberInGroup);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            usernameTxt = itemView.findViewById(R.id.user_name_textFf);
            phoneTxt = itemView.findViewById(R.id.phone_textFf);
            addUserGroupBtn = itemView.findViewById(R.id.add_user_groupBtn);

        }
    }
}
