package com.example.myapplication;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class UpdatePersonalActivity extends AppCompatActivity {
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectdImageUri;
    PreferenceManager preferenceManager;
    ProgressBar progressBar, progress_data;
    Dialog dialog;
    ImageView profilePic;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    TextView successBtn;
    TextInputEditText usernameEdt, addressEdt, birthEdt, schoolEdt, profile_phoneEdt ;
    AutoCompleteTextView genderAct, relationShipAct;
    TextInputLayout TipBirthEdt;
    ImageButton backBtn;
    Button updatePersonalBtn;
    CalendarView calendarBirth;
    String[] itemGender = {"Nam", "nữ"};
    String[] itemRelationShip = {"Độc thân", "Đã kết hôn", "Hẹn hò", "Góa"};
    ArrayAdapter<String> arrayAdapterItemGender;
    ArrayAdapter<String> arrayAdapterItemRelationShip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_personal);
        setContent();
        setInProgressData(true);
        listenNer();
        getUserData();
        clickUpdateAvt();
        setInProgressData(false);
    }
    void listenNer(){
        dropItem();
        dropItemRelationShip();
       TipBirthEdt.setEndIconOnClickListener(v -> {
           //calendarDialog();
           date();
       });

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        updatePersonalBtn.setOnClickListener(v -> {
            updateBtnClick();
        });
    }
    void date(){
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                birthEdt.setText(date);
            }
        });
        materialDatePicker.show(getSupportFragmentManager(), "tag");
    }
    void dropItem(){
        arrayAdapterItemGender = new ArrayAdapter<>(this, R.layout.item_list_textview, itemGender);
            genderAct.setAdapter(arrayAdapterItemGender);
            genderAct.setOnItemClickListener((parent, view, position, id) -> {
                String selectedItem = (String) parent.getItemAtPosition(position);
                genderAct.setText(selectedItem);
            });


    }
    @SuppressLint("ClickableViewAccessibility")
    void dropItemRelationShip(){
        arrayAdapterItemRelationShip = new ArrayAdapter<>(this, R.layout.item_list_textview, itemRelationShip);
        relationShipAct.setAdapter(arrayAdapterItemRelationShip);
        relationShipAct.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Drawable rightDrawable = relationShipAct.getCompoundDrawables()[2];
                if (rightDrawable != null && event.getRawX() >= (relationShipAct.getRight() - rightDrawable.getBounds().width())) {
                    // Hiển thị danh sách gợi ý khi mũi tên được nhấp
                    relationShipAct.showDropDown();
                    return true;
                }
            }
            return false;
        });


    }

    void setContent(){
        relationShipAct = findViewById(R.id.relationShipAct);
        genderAct = findViewById(R.id.genderAct);
        profile_phoneEdt = findViewById(R.id.profile_phoneEdt);
        schoolEdt = findViewById(R.id.schooEdt);
        addressEdt = findViewById(R.id.addressEdt);
        birthEdt = findViewById(R.id.birthEdt);
        usernameEdt = findViewById(R.id.usernameEdt);
        profilePic = findViewById(R.id.profile_image_view);
        backBtn = findViewById(R.id.back_btn);
        TipBirthEdt = findViewById(R.id.TipbirthEdt);
        updatePersonalBtn = findViewById(R.id.profle_update_btn);
        progressBar = findViewById(R.id.profile_progress_bar);
        preferenceManager = new PreferenceManager(this);
        progress_data = findViewById(R.id.progress_data);
    }
    void updateBtnClick(){
        String birth = birthEdt.getText().toString();
        String address = addressEdt.getText().toString();
        String school = schoolEdt.getText().toString();
        String gender = genderAct.getText().toString();
        String relationship = relationShipAct.getText().toString();
        String newUsername = usernameEdt.getText().toString();
        if(newUsername.isEmpty() || newUsername.length()<2){
            usernameEdt.setError("Tên không hợp lệ");
            return;
        }if (!isValidDate(birth, "dd/MM/yyyy")){
            Log.d("aaa", birth);
            birthEdt.setError("Ngày tháng không hợp lệ");
            return;
        }else {
            currentUserModel.setUsername(newUsername);
            currentUserModel.setAddress(address);
            currentUserModel.setSchool(school);
            currentUserModel.setGender(gender);
            currentUserModel.setRelationship(relationship);
            currentUserModel.setBirthDay(birth);


            setInProgress(true);
            if (selectdImageUri!=null){
                FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectdImageUri).
                        addOnCompleteListener(task -> {
                            updateToFireSore();
                        });

            }else {
                updateToFireSore();
            }
        }


    }
    void getUserData(){
        setInProgress(true);
        if (currentUser != null) {
            boolean hasEmail = currentUser.getEmail() != null;
            boolean hasPhoneNumber = currentUser.getPhoneNumber() != null;

            if (hasEmail) {
                profile_phoneEdt.setText(currentUser.getEmail());
            } else if (hasPhoneNumber) {
                profile_phoneEdt.setText(currentUser.getPhoneNumber());
            } else {
                Log.d("Auth", "NO email no phone");
            }
        }
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);

            usernameEdt.setText(currentUserModel.getUsername());
            birthEdt.setText(currentUserModel.getBirthDay());
            addressEdt.setText(currentUserModel.getAddress());
            schoolEdt.setText(currentUserModel.getSchool());
            genderAct.setText(currentUserModel.getGender());
            relationShipAct.setText(currentUserModel.getRelationship());


        });
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        Uri uri = task.getResult();
                            AndroidUtil.setProfilePic(this, uri, profilePic);
                    }

                });
    }
    void updateToFireSore(){
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()){
                        AndroidUtil.showToast(this, "Update thành công");

                    }else {
                        AndroidUtil.showToast(this, "Update thất bại");
                    }
                });
    }
    void clickUpdateAvt(){
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data!= null && data.getData()!= null){
                            selectdImageUri = data.getData();
                            AndroidUtil.setProfilePic(this, selectdImageUri, profilePic);


                        }
                    }
                });
        profilePic.setOnClickListener(view13 -> {
            ImagePicker.with(this).
                    cropSquare().compress(512).
                    maxResultSize(512, 512).
                    createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });


        });
    }
    private boolean isValidDate(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }
    void setInProgress(boolean inProgress){

        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updatePersonalBtn.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            updatePersonalBtn.setVisibility(View.VISIBLE);
        }
    }
    void setInProgressData(boolean inProgress){

        if (inProgress){
            progress_data.setVisibility(View.VISIBLE);
        }else {
            progress_data.setVisibility(View.GONE);
        }
    }
}