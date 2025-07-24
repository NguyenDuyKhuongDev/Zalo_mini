package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.model.ChatMessageModel;
import com.example.myapplication.test.AudioMessageListener;
import com.example.myapplication.test.OnButtonClickListener;
import com.example.myapplication.test.VideoClickListener;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    ArrayList<TextView> DataSet;
    String chatroomId;
    FragmentManager fragmentManager;
    String textMessage;
    private final OnButtonClickListener listener;
    private final VideoClickListener videoClickListener;
    private final AudioMessageListener audioMessageListener;
    private boolean isMuted = false;
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,  FragmentManager fragmentManager
            ,String chatroomId, Context context
            , OnButtonClickListener listener, VideoClickListener videoClickListener, AudioMessageListener audioMessageListener) {
        super(options);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.chatroomId = chatroomId;
        this.DataSet = new ArrayList<>();
        this.listener = listener;
        this.videoClickListener = videoClickListener;
        this.audioMessageListener = audioMessageListener;



    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        holder.calendarUser.setOnClickListener(v -> {
            if (listener != null) {
                listener.onButtonClick(position, textMessage);
            }
        });



        if (model.getMessage() != null && model.getFiles().equals("1")){
            if (FirebaseUtil.currentUserID().equals(model.getSenderId())){
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.leftMediaLayout.setVisibility(View.GONE);
                holder.cardImageRight.setVisibility(View.GONE);
                holder.cardVideoRight.setVisibility(View.GONE);

                holder.rightMediaLayout.setVisibility(View.VISIBLE);
                holder.documentLayoutRight.setVisibility(View.VISIBLE);
                holder.fileNameTxtRight.setText(model.getFileName());
                holder.sizeFileTxtRight.setText(model.getSizeFile());
                holder.downLoadFileTxtRight.setOnClickListener(v -> {
                    Intent browser= new Intent(Intent.ACTION_VIEW, Uri.parse(model.getMessage()));
                    context.startActivity(browser);

                });

            }else {
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.rightMediaLayout.setVisibility(View.GONE);
                holder.cardImageLeft.setVisibility(View.GONE);
                holder.cardVideoLeft.setVisibility(View.GONE);

                holder.leftMediaLayout.setVisibility(View.VISIBLE);
                holder.documentLayoutLeft.setVisibility(View.VISIBLE);
                holder.fileNameTxtLeft.setText(model.getFileName());
                holder.sizeFileTxtLeft.setText(model.getSizeFile());
                holder.downLoadFileTxtLeft.setOnClickListener(v -> {
                    Intent browser= new Intent(Intent.ACTION_VIEW, Uri.parse(model.getMessage()));
                    context.startActivity(browser);

                });
            }
        }
        else if (model.getMessage() != null && model.getVideos().equals("1")){

            Uri videoUri = Uri.parse(model.getMessage());
            if (FirebaseUtil.currentUserID().equals(model.getSenderId())){
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.leftMediaLayout.setVisibility(View.GONE);
                holder.cardImageRight.setVisibility(View.GONE);

                holder.rightMediaLayout.setVisibility(View.VISIBLE);
                holder.cardVideoRight.setVisibility(View.VISIBLE);
                holder.videoView_right.setVisibility(View.VISIBLE);
                holder.videoView_right.setVideoURI(videoUri);
                holder.videoView_right.setOnPreparedListener(mp -> {
                    mp.setVolume(0f, 0f);
                    holder.videoView_right.start();
                    holder.volumeBtn.setOnClickListener(v -> {
                        if (isMuted){
                            mp.setVolume(1f, 1f);
                            holder.volumeBtn.setImageResource(R.drawable.ic_volume);
                        }else {
                            mp.setVolume(0f, 0f);
                            holder.volumeBtn.setImageResource(R.drawable.ic_volume_off);
                        }
                        isMuted = !isMuted;
                    });

                });
            }else {
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.rightMediaLayout.setVisibility(View.GONE);
                holder.cardImageLeft.setVisibility(View.GONE);

                holder.leftMediaLayout.setVisibility(View.VISIBLE);
                holder.cardVideoLeft.setVisibility(View.VISIBLE);
                holder.videoView_left.setVisibility(View.VISIBLE);
                holder.videoView_left.setVideoURI(videoUri);
                holder.videoView_left.setOnPreparedListener(mp -> {
                    mp.setVolume(0f, 0f);
                    holder.videoView_left.start();
                    holder.volumeLeftBtn.setOnClickListener(v -> {
                        if (isMuted){
                            mp.setVolume(1f, 1f);
                            holder.volumeLeftBtn.setImageResource(R.drawable.ic_volume);
                        }else {
                            mp.setVolume(0f, 0f);
                            holder.volumeLeftBtn.setImageResource(R.drawable.ic_volume_off);
                        }
                        isMuted = !isMuted;
                    });

                });
            }
            holder.videoView_right.setOnClickListener(v -> {
                if (videoClickListener != null){
                    videoClickListener.showDialogMedia(position,"video", videoUri);
                }
            });
            holder.videoView_left.setOnClickListener(v -> {
                if (videoClickListener != null){
                    videoClickListener.showDialogMedia(position,"video", videoUri);
                }
            });

        }else if (model.getMessage() != null && model.getImages().equals("1")) {
                Uri imageUri = Uri.parse(model.getMessage());

                if (FirebaseUtil.currentUserID().equals(model.getSenderId())) {
                    holder.leftChatLayout.setVisibility(View.GONE);
                    holder.rightChatLayout.setVisibility(View.GONE);
                    holder.leftMediaLayout.setVisibility(View.GONE);
                    holder.cardVideoRight.setVisibility(View.GONE);

                    holder.rightMediaLayout.setVisibility(View.VISIBLE);
                    holder.cardImageRight.setVisibility(View.VISIBLE);
                    AndroidUtil.setImagePic(context, imageUri, holder.imageRight);

                } else {
                    holder.rightChatLayout.setVisibility(View.GONE);
                    holder.leftChatLayout.setVisibility(View.GONE);
                    holder.rightMediaLayout.setVisibility(View.GONE);
                    holder.cardVideoLeft.setVisibility(View.GONE);

                    holder.leftMediaLayout.setVisibility(View.VISIBLE);
                    holder.cardImageLeft.setVisibility(View.VISIBLE);
                    AndroidUtil.setImagePic(context, imageUri, holder.imageLeft);

                }
                holder.imageRight.setOnClickListener(v -> {
                    if (videoClickListener != null){
                        videoClickListener.showDialogMedia(position,"image", imageUri);
                    }
                });
                holder.imageLeft.setOnClickListener(v -> {
                    if (videoClickListener != null){
                        videoClickListener.showDialogMedia(position,"image", imageUri);
                    }
                });

        } else if (model.getMessage() != null && "1".equals(model.getAudios())) {
                Uri AudioUri = Uri.parse(model.getMessage());

                if (FirebaseUtil.currentUserID().equals(model.getSenderId())) {
                    holder.leftChatLayout.setVisibility(View.GONE);
                    holder.rightChatLayout.setVisibility(View.GONE);
                    holder.leftMediaLayout.setVisibility(View.GONE);
                    holder.cardVideoRight.setVisibility(View.GONE);
                    holder.cardImageRight.setVisibility(View.GONE);

                    holder.rightMediaLayout.setVisibility(View.VISIBLE);
                    holder.layoutAudioRight.setVisibility(View.VISIBLE);


                } else {
                    holder.rightChatLayout.setVisibility(View.GONE);
                    holder.leftChatLayout.setVisibility(View.GONE);
                    holder.rightMediaLayout.setVisibility(View.GONE);
                    holder.cardVideoLeft.setVisibility(View.GONE);
                    holder.cardImageLeft.setVisibility(View.GONE);
                    holder.leftMediaLayout.setVisibility(View.VISIBLE);
                    holder.layoutAudioLeft.setVisibility(View.VISIBLE);

                }
                holder.playAudioRight.setOnClickListener(v -> audioMessageListener.OnItemAudioClick(AudioUri, holder.timerAudioRight, holder.playAudioRight, holder.animationAudio, position));
                holder.playAudioLeft.setOnClickListener(v -> audioMessageListener.OnItemAudioClick(AudioUri, holder.timerAudioRightLeft, holder.playAudioLeft, holder.animationAudioLeft, position));
        }else {
            if (FirebaseUtil.currentUserID().equals(model.getSenderId())) {
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.rightMediaLayout.setVisibility(View.GONE);
                holder.leftMediaLayout.setVisibility(View.GONE);

                holder.rightChatLayout.setVisibility(View.VISIBLE);
                holder.rightChatTextview.setText(model.getMessage());

            } else {
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.leftMediaLayout.setVisibility(View.GONE);
                holder.rightMediaLayout.setVisibility(View.GONE);

                holder.leftChatLayout.setVisibility(View.VISIBLE);
                holder.leftChatTextview.setText(model.getMessage());
            }
        }



        String current = model.getMessage();
        String mess = holder.rightChatTextview.getText().toString();
        if (current.equals(mess)){
            for (TextView calendarUser :DataSet){
                calendarUser.setVisibility(View.GONE);
            }
            DataSet.clear();
            String[] keywords = {"ngày mai đi", "ngày kia đi", "mai đi", "thứ hai", "thứ ba", "chủ nhật","hôm nào đi"};
            boolean containsKeyword = false;
            for (String keyword : keywords) {
                if (holder.rightChatTextview.getText().toString().contains(keyword)) {
                    containsKeyword = true;
                    textMessage = holder.rightChatTextview.getText().toString();
                    break;
                }
            }
            if (containsKeyword) {

                holder.calendarUser.setVisibility(View.VISIBLE);
                holder.viewCalendarUser.setVisibility(View.VISIBLE);
                DataSet.add(holder.calendarUser);
                DataSet.add(holder.viewCalendarUser);

            } else {
                holder.calendarUser.setVisibility(View.GONE);
                holder.viewCalendarUser.setVisibility(View.GONE);
            }
        }else {
            holder.calendarUser.setVisibility(View.GONE);
            holder.viewCalendarUser.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_chat_message_row, parent, false);

        return new ChatModelViewHolder(view);
    }



    static class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout, leftMediaLayout, rightMediaLayout, documentLayoutRight, documentLayoutLeft, layoutAudioRight, layoutAudioLeft;
        TextView leftChatTextview,rightChatTextview, calendarUser, calendarOther, fileNameTxtRight,sizeFileTxtRight, downLoadFileTxtRight ;
        ImageView imageLeft, imageRight;
        CardView cardImageLeft, cardImageRight, cardVideoRight, cardVideoLeft;
        TextView viewCalendarUser,viewCalendarOther, fileNameTxtLeft, sizeFileTxtLeft, downLoadFileTxtLeft, timerAudioRight, timerAudioRightLeft;
        VideoView videoView_left, videoView_right;
        ProgressBar progressVideoRight, progressVideoLeft;
        ImageButton playAudioRight, playAudioLeft, volumeBtn, volumeLeftBtn;
        LottieAnimationView animationAudio, animationAudioLeft;


        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);

            leftMediaLayout = itemView.findViewById(R.id.left_image_layout);
            rightMediaLayout = itemView.findViewById(R.id.right_image_layout);
            imageLeft = itemView.findViewById(R.id.image_left);
            imageRight = itemView.findViewById(R.id.image_right);
            cardImageLeft = itemView.findViewById(R.id.cardImageLeft);
            cardImageRight = itemView.findViewById(R.id.cardImageRight);

            calendarUser = itemView.findViewById(R.id.calendarUser);
            viewCalendarUser = itemView.findViewById(R.id.viewCalendarUser);

            viewCalendarOther = itemView.findViewById(R.id.viewCalendarOther);
            calendarOther = itemView.findViewById(R.id.calendarOther);

            videoView_left = itemView.findViewById(R.id.videoView_left);
            videoView_right = itemView.findViewById(R.id.videoView_right);
            cardVideoRight = itemView.findViewById(R.id.cardVideoRight);
            cardVideoLeft = itemView.findViewById(R.id.cardVideoLeft);
            volumeBtn = itemView.findViewById(R.id.volumeBtn);
            volumeLeftBtn = itemView.findViewById(R.id.volumeLeftBtn);

            progressVideoRight = itemView.findViewById(R.id.progressVideoRight);
            progressVideoLeft = itemView.findViewById(R.id.progressVideoLeft);

            documentLayoutRight = itemView.findViewById(R.id.documentLayoutRight);
            fileNameTxtRight = itemView.findViewById(R.id.fileNameTxtRight);
            sizeFileTxtRight = itemView.findViewById(R.id.sizeFileTxtRight);
            downLoadFileTxtRight = itemView.findViewById(R.id.downLoadFileTxtRight);

            documentLayoutLeft = itemView.findViewById(R.id.documentLeftLayout);
            fileNameTxtLeft = itemView.findViewById(R.id.fileNameTxtLeft);
            sizeFileTxtLeft = itemView.findViewById(R.id.sizeFileTxtLeft);
            downLoadFileTxtLeft = itemView.findViewById(R.id.downLoadFileTxtLeft);

            layoutAudioRight = itemView.findViewById(R.id.layoutAudioRight);
            playAudioRight = itemView.findViewById(R.id.playAudioRight);
            animationAudio = itemView.findViewById(R.id.animationAudio);
            timerAudioRight = itemView.findViewById(R.id.timerAudioRight);

            layoutAudioLeft = itemView.findViewById(R.id.layoutAudioLeft);
            playAudioLeft = itemView.findViewById(R.id.playAudioLeft);
            animationAudioLeft = itemView.findViewById(R.id.animationAudioLeft);
            timerAudioRightLeft = itemView.findViewById(R.id.timerAudioRightLeft);

        }
    }
}
