package com.example.myapplication.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myapplication.ChatActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;


public class Permission {
    Context mcontext;
    public Permission(Context context) {
        mcontext = context;
    }

    Dialog dialog;
    MediaPlayer mediaPlayer;
    public void checkConnectInternet(ImageView internet_status, TextView text_status_internet){
        boolean isConnect = isConnectToInternet();

        if (isConnect){
            internet_status.setVisibility(View.GONE);
            text_status_internet.setVisibility(View.GONE);
        }else {
            internet_status.setVisibility(View.VISIBLE);
            text_status_internet.setVisibility(View.VISIBLE);
        }
    }
    private boolean isConnectToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
    public void checkShowNotificationPermission(Activity activity){
        NotificationManager notificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
        boolean checkNotificationsEnabled = notificationManager.areNotificationsEnabled();
        if (!checkNotificationsEnabled){
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.custom_dialog_notification_permission);
            Drawable customBackground  = ContextCompat.getDrawable(mcontext, R.drawable.dialog_backgroud);
            dialog.getWindow().setBackgroundDrawable(customBackground);
            TextView no = dialog.findViewById(R.id.cancelBtn);
            TextView yes = dialog.findViewById(R.id.successBtn);

            no.setOnClickListener(v -> {
                dialog.dismiss();
            });
            yes.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, mcontext.getPackageName());
                mcontext.startActivity(intent);
            });
            dialog.show();
        }
    }
    public void audio(Uri audioUri, TextView timer, ImageButton audioBtn, LottieAnimationView animationView, int position, Context context) {
        int playingPosition = -1;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        try {

            mediaPlayer.setDataSource(context, audioUri);
            mediaPlayer.setOnPreparedListener(mp -> {
                int duration = mediaPlayer.getDuration();
                @SuppressLint("DefaultLocale")
                String durationString = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                timer.setText(durationString);

                audioBtn.setOnClickListener(v -> {
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        audioBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow));
                        animationView.pauseAnimation();

                    }else {

                        mediaPlayer.start();
                        audioBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause));
                        animationView.playAnimation();

                    }
                });

            });
            mediaPlayer.setOnCompletionListener(mp -> {
                audioBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow));
                animationView.pauseAnimation();
            });
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void dialogVideo(Context context, int position, String mediaType, Uri media, Activity activity){
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_mediaview);
        Window dialogWindow = dialog.getWindow();

        if (dialogWindow != null) {
            dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialogWindow.setGravity(Gravity.CENTER);
        }
        Drawable customBackground  = ContextCompat.getDrawable(context, R.drawable.dialog_backgroud);
        dialog.getWindow().setBackgroundDrawable(customBackground);
        ImageButton closeBtn = dialog.findViewById(R.id.close_btn_dialog);
        VideoView videoView = dialog.findViewById(R.id.videoView_dialog);
        ImageView imageView1  = dialog.findViewById(R.id.imageView_dialog);
        if (mediaType.equals("image"))
        {
            imageView1.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            AndroidUtil.setImagePic(context, media, imageView1);

        }else if (mediaType.equals("video")){
            imageView1.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            MediaController mediaController = new MediaController(activity);
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);
            videoView.setVideoURI(media);
            videoView.start();
        }

        closeBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }


    public long getFileSize(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
        assert pfd != null;
        FileDescriptor fd = pfd.getFileDescriptor();
        FileInputStream fis = new FileInputStream(fd);
        long fileSize = fis.getChannel().size();
        fis.close();
        pfd.close();
        return fileSize;
    }
    public  String formatFileSize(long bytes, boolean inMB) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        if (inMB) {
            double fileSizeInMB = bytes / (1024.0 * 1024);
            return decimalFormat.format(fileSizeInMB) + " MB";
        } else {

            double fileSizeInKB = bytes / 1024.0;
            return decimalFormat.format(fileSizeInKB) + " KB";
        }
    }



}
