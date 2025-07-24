package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ChatGroupMessage;

import com.example.myapplication.test.GroupMediaListener;

import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class GroupChatAdapter extends FirestoreRecyclerAdapter<ChatGroupMessage, GroupChatAdapter.GroupChatModelViewHolder> {

    Context context;
    ArrayList<TextView> DataSet;
    String chatgroupId;
    String textMessage;
    String currentUserID = FirebaseUtil.currentUserID();
    private final GroupMediaListener groupMediaListener;

    public GroupChatAdapter(@NonNull FirestoreRecyclerOptions<ChatGroupMessage> options,String chatgroupId, Context context, GroupMediaListener groupMediaListener) {
        super(options);
        this.context = context;
        this.chatgroupId = chatgroupId;
        this.DataSet = new ArrayList<>();
        this.groupMediaListener = groupMediaListener;

    }

    @Override
    protected void onBindViewHolder(@NonNull GroupChatModelViewHolder holder, int position, @NonNull ChatGroupMessage model) {

       if (model.getMessage() != null && model.getFiles().equals("1")){
            if (model.getSenderId().equals(currentUserID)){
                holder.containerLeft.setVisibility(View.GONE);
                holder.containerRight.setVisibility(View.VISIBLE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.rightMediaLayout.setVisibility(View.VISIBLE);
                holder.cardVideoRight_group.setVisibility(View.GONE);
                holder.cardImageRight.setVisibility(View.GONE);
                holder.documentLayoutRight.setVisibility(View.VISIBLE);
                holder.fileNameTxtRight.setText(model.getFileName());
                holder.sizeFileTxtRight.setText(model.getSizeFile());
                holder.downLoadFileTxtRight.setOnClickListener(v -> {
                    Intent browser= new Intent(Intent.ACTION_VIEW, Uri.parse(model.getMessage()));
                    context.startActivity(browser);
                });

            }else {
                setData(holder.imageProfile, model.getSenderId(), holder.nameOtherUser, model.senderName);
                holder.containerLeft.setVisibility(View.VISIBLE);
                holder.containerRight.setVisibility(View.GONE);
                holder.cardImageLeft.setVisibility(View.GONE);
                holder.cardVideoLeft.setVisibility(View.GONE);
                holder.documentLeftLayout.setVisibility(View.VISIBLE);
                holder.leftMediaLayout.setVisibility(View.VISIBLE);
                holder.fileNameTxtLeft.setText(model.getFileName());
                holder.sizeFileTxtLeft.setText(model.getSizeFile());
                holder.downLoadFileTxtLeft.setOnClickListener(v -> {
                    Intent browser= new Intent(Intent.ACTION_VIEW, Uri.parse(model.getMessage()));
                    context.startActivity(browser);
                });
            }
        }else if (model.getMessage() != null && model.getVideos().equals("1")){
            Uri videoUri = Uri.parse(model.getMessage());
            if (model.getSenderId().equals(currentUserID)){
                holder.containerLeft.setVisibility(View.GONE);
                holder.containerRight.setVisibility(View.VISIBLE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.rightMediaLayout.setVisibility(View.VISIBLE);
                holder.cardImageRight.setVisibility(View.GONE);
                holder.documentLayoutRight.setVisibility(View.GONE);
                holder.cardVideoRight_group.setVisibility(View.VISIBLE);
                holder.video_right_group.setVideoURI(videoUri);
                holder.video_right_group.start();
            }else {
                setData(holder.imageProfile, model.getSenderId(), holder.nameOtherUser, model.senderName);
                holder.containerRight.setVisibility(View.GONE);
                holder.containerLeft.setVisibility(View.VISIBLE);
                holder.leftMediaLayout.setVisibility(View.VISIBLE);
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.cardImageLeft.setVisibility(View.GONE);
                holder.documentLeftLayout.setVisibility(View.GONE);
                holder.cardVideoLeft.setVisibility(View.VISIBLE);
                holder.videoView_left.setVideoURI(videoUri);
                holder.videoView_left.start();

            }
           holder.video_right_group.setOnClickListener(v -> {
               if (groupMediaListener != null){
                   groupMediaListener.showDialogMedia(position,"video", videoUri);
               }
           });
           holder.videoView_left.setOnClickListener(v -> {
               if (groupMediaListener != null){
                   groupMediaListener.showDialogMedia(position,"video", videoUri);
               }
           });

        } else if (model.getMessage() != null && model.getImages().equals("1")) {
            Uri imageUri = Uri.parse(model.getMessage());

            if (model.getSenderId().equals(currentUserID)){
                holder.containerLeft.setVisibility(View.GONE);
                holder.containerRight.setVisibility(View.VISIBLE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.rightMediaLayout.setVisibility(View.VISIBLE);
                holder.cardImageRight.setVisibility(View.VISIBLE);
                AndroidUtil.setImagePic(context, imageUri, holder.imageRight);

            }else {
                setData(holder.imageProfile, model.getSenderId(), holder.nameOtherUser, model.senderName);
                holder.containerRight.setVisibility(View.GONE);
                holder.containerLeft.setVisibility(View.VISIBLE);
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.leftMediaLayout.setVisibility(View.VISIBLE);
                holder.cardImageLeft.setVisibility(View.VISIBLE);
                AndroidUtil.setImagePic(context, imageUri, holder.image_left);

            }
           holder.imageRight.setOnClickListener(v -> {
               if (groupMediaListener != null){
                   groupMediaListener.showDialogMedia(position,"image", imageUri);
               }
           });
           holder.image_left.setOnClickListener(v -> {
               if (groupMediaListener != null){
                   groupMediaListener.showDialogMedia(position,"image", imageUri);
               }
           });
        }else {
           if (!model.getSenderId().equals("##########################~~")) {
               if (model.getSenderId().equals(FirebaseUtil.currentUserID())) {
                   holder.containerLeft.setVisibility(View.GONE);
                   holder.containerRight.setVisibility(View.VISIBLE);
                   holder.rightMediaLayout.setVisibility(View.GONE);
                   holder.rightChatLayout.setVisibility(View.VISIBLE);
                   holder.rightChatTextview.setText(model.getMessage());
               } else {
                   setData(holder.imageProfile, model.getSenderId(), holder.nameOtherUser, model.senderName);
                   holder.containerRight.setVisibility(View.GONE);
                   holder.containerLeft.setVisibility(View.VISIBLE);
                   holder.leftMediaLayout.setVisibility(View.GONE);
                   holder.leftChatLayout.setVisibility(View.VISIBLE);
                   holder.left_chat_textview.setText(model.getMessage());

               }
           }
        }

        if (model.getSenderId().equals("##########################~~")){
                holder.containerLeft.setVisibility(View.GONE);
                holder.containerRight.setVisibility(View.GONE);
                holder.item_textNotification.setVisibility(View.VISIBLE);
                holder.item_textNotification.setText(model.getMessage());
            }else {
                holder.item_textNotification.setVisibility(View.GONE);
            }
        }


    @NonNull
    @Override
    public GroupChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_chat_group_row, parent, false);
        return new GroupChatAdapter.GroupChatModelViewHolder(view);
    }

    void setData(RoundedImageView imageView, String sendId, TextView nameTxt, String name){
        nameTxt.setText(name);
        FirebaseUtil.getOtherProfilePicStorageRef(sendId).getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri uri = task.getResult();
                            AndroidUtil.setProfilePic(context, uri, imageView);
                        }
                    }
                });


    }





    class GroupChatModelViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftChatLayout,rightChatLayout, leftMediaLayout, rightMediaLayout, documentLayoutRight, documentLeftLayout, containerLeft, containerRight;
        TextView left_chat_textview,rightChatTextview, calendarUser, calendarOther, item_textNotification, nameOtherUser, fileNameTxtLeft, sizeFileTxtLeft, downLoadFileTxtLeft;
        ImageView image_left, imageRight;
        CardView cardImageLeft, cardImageRight, cardVideoRight_group, cardVideoLeft;
        TextView viewCalendarUser,viewCalendarOther, fileNameTxtRight, sizeFileTxtRight, downLoadFileTxtRight;
        VideoView videoView_left, video_right_group;
        RoundedImageView imageProfile;

        public GroupChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            left_chat_textview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            containerLeft = itemView.findViewById(R.id.containerOther);
             containerRight = itemView.findViewById(R.id.containerCurrent);


            leftMediaLayout = itemView.findViewById(R.id.left_media_layout);
            rightMediaLayout = itemView.findViewById(R.id.right_media_layout);
            image_left = itemView.findViewById(R.id.image_left);
            imageRight = itemView.findViewById(R.id.image_right);
            cardImageLeft = itemView.findViewById(R.id.cardImageLeft);
            cardImageRight = itemView.findViewById(R.id.cardImageRight);
            item_textNotification = itemView.findViewById(R.id.item_textNotification);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            nameOtherUser = itemView.findViewById(R.id.nameOtherUser);

            cardVideoRight_group = itemView.findViewById(R.id.cardVideoRight);
            cardVideoLeft = itemView.findViewById(R.id.cardVideoLeft);
            videoView_left = itemView.findViewById(R.id.videoView_left);
            video_right_group = itemView.findViewById(R.id.videoView_right);

            documentLayoutRight = itemView.findViewById(R.id.documentLayoutRight);
            documentLeftLayout = itemView.findViewById(R.id.documentLeftLayout);
            fileNameTxtLeft = itemView.findViewById(R.id.fileNameTxtLeft);
            sizeFileTxtLeft = itemView.findViewById(R.id.sizeFileTxtLeft);
            downLoadFileTxtLeft = itemView.findViewById(R.id.downLoadFileTxtLeft);

            fileNameTxtRight = itemView.findViewById(R.id.fileNameTxtRight);
            sizeFileTxtRight = itemView.findViewById(R.id.sizeFileTxtRight);
            downLoadFileTxtRight = itemView.findViewById(R.id.downLoadFileTxtRight);
        }
    }
}
