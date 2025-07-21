package com.example.myapplication.adapter;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ChatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.ChatroomModel;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.reflect.Type;
import java.util.Objects;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {
    Context context;
    ChatroomModel models;
    String currentUserID = FirebaseUtil.currentUserID();
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {


        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserID());

                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl().
                                addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        Uri uri = task1.getResult();
                                        AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                                    }

                                });
                        holder.usernameText.setText(otherUserModel.getUsername());
                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));
                        if (lastMessageSentByMe){
                            if (model.getLastMessage().contains("###sendImage%&*!")) {
                                holder.lastMessageText.setText("Bạn : Đã gửi một hình ảnh");
                            }else if (model.getLastMessage().contains("###sendVideo%&*!")){
                                holder.lastMessageText.setText("Bạn : Đã gửi một video");
                            }else if (model.getLastMessage().contains("###sendDocument%&*!")){
                                holder.lastMessageText.setText("Bạn : Đã gửi một tệp");
                            }else {
                                holder.lastMessageText.setText("Bạn : "+model.getLastMessage());
                            }
                        }else {
                            if (model.getLastMessage().contains("###sendImage%&*!")) {
                                holder.lastMessageText.setText("Đã gửi một hình ảnh");
                            }else if (model.getLastMessage().contains("###sendVideo%&*!")){
                                holder.lastMessageText.setText("Đã gửi một video");
                            }else if (model.getLastMessage().contains("###sendDocument%&*!")){
                                holder.lastMessageText.setText("Đã gửi một tệp");
                            }else {
                                holder.lastMessageText.setText(model.getLastMessage());
                            }
                        }

                        if (model.getStatusRead() != null)
                        {
                            if (model.getStatusRead().equals("0") && !lastMessageSentByMe)
                            {
                                holder.usernameText.setTypeface(null, Typeface.BOLD);
                                holder.usernameText.setTextColor(Color.BLACK);
                                holder.lastMessageText.setTextColor(Color.rgb(40,167,241));
                                holder.lastMessageTime.setTextColor(Color.rgb(40,167,241));

                            }else {
                                holder.usernameText.setTypeface(null, Typeface.BOLD);
                                holder.usernameText.setTextColor(Color.rgb(117, 117, 117));
                                holder.lastMessageTime.setTextColor(Color.rgb(117, 117, 117));
                            }
                        }
                        FirebaseUtil.status(otherUserModel.getUserId()).addSnapshotListener(((value, error) -> {
                            if (value != null) {
                                String statusStr = value.getString("status");
                                if (statusStr != null && !statusStr.isEmpty()) {
                                    int availability = Integer.parseInt(statusStr);
                                    if (availability == 0) {
                                        holder.statusOnline.setVisibility(View.GONE);
                                    } else {
                                        holder.statusOnline.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }));
                        holder.itemView.setOnClickListener(view -> {

                            if (lastMessageSentByMe){
                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                AndroidUtil.passUserModelAsIntent(intent,otherUserModel);
                                context.startActivity(intent);
                            }else {
                                FirebaseUtil.updateLastRead(model.getChatroomId()).update("statusRead", "1");
                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                AndroidUtil.passUserModelAsIntent(intent,otherUserModel);
                                context.startActivity(intent);
                            }

//                            Intent intent = new Intent(context, ChatActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            AndroidUtil.passUserModelAsIntent(intent,otherUserModel);
//                            context.startActivity(intent);

                        });

                    }

                });


    }
    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_recent_chat_row,parent,false);

        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;
        RoundedImageView statusOnline;



        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            statusOnline = itemView.findViewById(R.id.onlineStatus);


        }
    }


}
