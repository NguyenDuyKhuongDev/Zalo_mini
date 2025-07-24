package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.adapter.SearchUserPhoneNumberAdapter;
import com.example.myapplication.adapter.SearchUserRecyclerAdapter;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    TextView notification;

    SearchUserRecyclerAdapter adapter;
    SearchUserPhoneNumberAdapter phoneNumberAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.seach_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        notification = findViewById(R.id.notification);
        searchInput.requestFocus();

        backButton.setOnClickListener(view -> {

            onBackPressed();
        });
        searchButton.setOnClickListener(view -> {
            String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length()<3){
                searchInput.setError("Invalid Username");
                return;
            }
        });
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (input.matches("[0-9+]+")){
                    setupSearchRecyclerviewPhoneNumber(input);
                }else {
                    setupSearchRecyclerView(input);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    void setupSearchRecyclerView(String searchTerm){
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username",searchTerm)
                .whereLessThanOrEqualTo("username",searchTerm+'\uf8ff');
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();

        adapter = new SearchUserRecyclerAdapter(options,getApplicationContext(), notification);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();




    }
    void setupSearchRecyclerviewPhoneNumber(String phoneNumber)
    {
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("phone",phoneNumber);
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();
        phoneNumberAdapter = new SearchUserPhoneNumberAdapter(options,getApplicationContext(), notification);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(phoneNumberAdapter);
        phoneNumberAdapter.startListening();



    }


    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
        if (phoneNumberAdapter != null)
            phoneNumberAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
        if (phoneNumberAdapter != null) phoneNumberAdapter.stopListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null) adapter.notifyDataSetChanged();
        if (phoneNumberAdapter != null) phoneNumberAdapter.notifyDataSetChanged();
    }
}