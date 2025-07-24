package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.PostStoryModel;
import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PersonalProfileRecyclerAdapter extends FirestoreRecyclerAdapter<PostStoryModel, PersonalProfileRecyclerAdapter.PersonalProfileReViewHolder> {
    Context context;
    private PreferenceManager preferenceManager;
    public PersonalProfileRecyclerAdapter(@NonNull FirestoreRecyclerOptions<PostStoryModel> options,  Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull PersonalProfileReViewHolder holder, int position, @NonNull PostStoryModel model) {
        preferenceManager = new PreferenceManager(context.getApplicationContext());
        holder.writeStoryTxt.setText(model.getPostText());
        FirebaseUtil.getOtherProfilePicStorageRef(model.getIdAuthor()).getDownloadUrl().
                addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Uri uri = task1.getResult();
                        AndroidUtil.setProfilePic(context, uri, holder.avatarAuthor);
                    }

                });
        holder.nameAuthor.setText(model.getNameAuthor());
        holder.timeStory.setText(getReadableDateTime(model.getPostTimestamp()));

        if (model.getPostMedia() != null && model.getImageStatus().equals("1")){
            holder.imageBackgroundCardView.setVisibility(View.VISIBLE);
            holder.videoBackgroundCardView.setVisibility(View.GONE);
            Uri imageUri = Uri.parse(model.getPostMedia());
            AndroidUtil.setImagePic(context, imageUri, holder.imageStory);

        }else if (model.getPostMedia() != null && model.getVideoStatus().equals("1")){
            holder.videoBackgroundCardView.setVisibility(View.VISIBLE);
            holder.imageBackgroundCardView.setVisibility(View.GONE);
            Uri videoUri = Uri.parse(model.getPostMedia());
            holder.videoStoryPost.setMediaController(null);

            MediaController mediaController = new MediaController(context);
            holder.videoStoryPost.setMediaController(mediaController);
            mediaController.setAnchorView(holder.videoStoryPost);
            holder.videoStoryPost.setVideoURI(videoUri);
            holder.videoStoryPost.stopPlayback();
        }else {
            holder.imageBackgroundCardView.setVisibility(View.GONE);
            holder.videoBackgroundCardView.setVisibility(View.GONE);
        }
        boolean postStoryByMe = model.getIdAuthor().equals(preferenceManager.getString(FirebaseUtil.KEY_USER_ID));
        if (postStoryByMe){
            holder.deleteStoryBtn.setOnClickListener(v -> {
                String documentId = getSnapshots().getSnapshot(position).getId();
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_info_delete, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.action_delete_chat:
                            FirebaseUtil.deletePostStory(documentId).delete().addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    Toast.makeText(context, "Đã xóa bài viết!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(context, "thất bại"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                            return true;
                        default:
                            return false;
                    }
                });

                popupMenu.show();
            });

        }
    }
    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("HH:mm · dd/MM/yyyy", Locale.getDefault()).format(date);
    }

    @NonNull
    @Override
    public PersonalProfileReViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_story_row,parent,false);
        return new PersonalProfileRecyclerAdapter.PersonalProfileReViewHolder(view);
    }

    static class PersonalProfileReViewHolder extends RecyclerView.ViewHolder{
        TextView nameAuthor, timeStory, writeStoryTxt;
        ImageView avatarAuthor, imageStory, deleteStoryBtn;
        CardView imageBackgroundCardView, videoBackgroundCardView;
        LinearLayout mediaLayout;
        VideoView videoStoryPost;
        public PersonalProfileReViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarAuthor = itemView.findViewById(R.id.profile_image_view);
            imageStory = itemView.findViewById(R.id.imageStoryPost);
            nameAuthor = itemView.findViewById(R.id.nameUserStoryTxt);
            timeStory = itemView.findViewById(R.id.timeDateTxt);
            writeStoryTxt = itemView.findViewById(R.id.writeStoryTxt);
            imageBackgroundCardView = itemView.findViewById(R.id.imageStory);
            deleteStoryBtn =itemView.findViewById(R.id.deleteStoryBtn);
            mediaLayout = itemView.findViewById(R.id.mediaLayoutStory);
            videoBackgroundCardView = itemView.findViewById(R.id.videoStory);
            videoStoryPost = itemView.findViewById(R.id.videoStoryPost);
        }
    }
}
