package com.example.myapplication.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.StoryPostActivity;
import com.example.myapplication.adapter.RecentChatRecyclerAdapter;
import com.example.myapplication.adapter.StoryRecyclerAdapter;
import com.example.myapplication.model.ChatroomModel;
import com.example.myapplication.model.PostStoryModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;


public class StoryFragment extends Fragment {
    AppCompatImageView addStoryBtn;
    TextView storyStatusBtn;
    ImageView profile_image_view;
    SwipeRefreshLayout containerSwipe;
    RecyclerView recyclerView;
    StoryRecyclerAdapter adapter;
    public StoryFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        addStoryBtn = view.findViewById(R.id.AddStoryBtn);
        storyStatusBtn = view.findViewById(R.id.storyStatusBtn);
        recyclerView = view.findViewById(R.id.recyclerViewStory);
        profile_image_view = view.findViewById(R.id.profile_image_view);
        containerSwipe = view.findViewById(R.id.container);
        //setupRecyclerView();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        containerSwipe.setOnRefreshListener(this::refreshData);
        containerSwipe.setColorSchemeColors(getResources().getColor(R.color.my_primary));
        refreshData();
        listTen();
    }
    void listTen(){
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Uri uri = task.getResult();
                        Context context = getContext();
                        if (context != null){
                            AndroidUtil.setProfilePic(getContext(), uri, profile_image_view);
                        }else {
                            return;
                        }
                    }
                });
        storyStatusBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StoryPostActivity.class);
            startActivity(intent);
        });
        addStoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StoryPostActivity.class);
            startActivity(intent);
        });
    }
    void refreshData() {
        containerSwipe.setRefreshing(true); // Bắt đầu refresh
        setupRecyclerView(); // Cập nhật dữ liệu
        containerSwipe.setRefreshing(false); // Kết thúc refresh
    }
    void setupRecyclerView(){
        Query query = FirebaseUtil.postStory().orderBy("postTimestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostStoryModel> options = new FirestoreRecyclerOptions.Builder<PostStoryModel>()
                .setQuery(query,PostStoryModel.class).build();

        adapter = new StoryRecyclerAdapter(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ImageButton groupBtn = getActivity().findViewById(R.id.main_groupBtn);
            ImageButton group_addBtn = getActivity().findViewById(R.id.main_group_addBtn);
            if (groupBtn != null && group_addBtn != null) {
                groupBtn.setVisibility(View.VISIBLE);
                group_addBtn.setVisibility(View.GONE);
            }
        }
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }

}