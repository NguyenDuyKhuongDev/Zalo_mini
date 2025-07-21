package com.example.myapplication.FriendFragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FriendFragmentAdapter;
import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FriendFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    Context context;
    private PreferenceManager preferenceManager;
    public FriendFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend, container, false);
//        preferenceManager =new PreferenceManager(context);
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);
        FriendFragmentAdapter adapter = new FriendFragmentAdapter(getChildFragmentManager(), FriendFragmentAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new FriendFistFragment(),"Bạn bè");
        adapter.addFragment(new RequestFriendFragment(), "Lời mời");
        viewPager.setAdapter(adapter);
        return view;

    }

    void getFriendRequest(){
//        FirebaseUtil.checkRequestFriend(preferenceManager.getString(FirebaseUtil.KEY_USER_ID)).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful() && !task.getResult().isEmpty()){
//                int countRequests = 0;
//                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
//                    countRequests++;
//                }
//                if (countRequests !=0){
//                    BadgeDrawable badgeDrawable = tabLayout.getOrCreateBadge(R.id.menu_phonebook);
//                    badgeDrawable.setVisible(true);
//                    badgeDrawable.setNumber(countRequests);
//
//
//
//                }
//
//            }else {
//                bottomNavigationView.removeBadge(R.id.menu_phonebook);
//            }
//        });
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
    }
}