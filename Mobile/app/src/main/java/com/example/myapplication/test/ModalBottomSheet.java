package com.example.myapplication.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModalBottomSheet extends BottomSheetDialogFragment {
    CalendarView calendarView;
    TimePicker timePicker;
    EditText date, nameTitle, timer;
    TextView successBtn, suaBtn;
    Context context;
    String[] daysOfWeek = new String[]{"Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"};
    private final String textMessage;
    String chatroomID;
    public ModalBottomSheet(String textMessage, Context context, String chatroomID){
        this.textMessage = textMessage;
        this.context = context;
        this.chatroomID = chatroomID;
    }
    public static final String TAG = "ModalBottomSheet";
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        nameTitle = view.findViewById(R.id.nameTitle);
        calendarView = view.findViewById(R.id.calendarView);
        date = view.findViewById(R.id.dateEdt);
        successBtn = view.findViewById(R.id.successBtn);
        nameTitle.setText(textMessage);
        timer = view.findViewById(R.id.timer);
        timePicker = view.findViewById(R.id.timerPicker);
        suaBtn = view.findViewById(R.id.view6);
        listenNer();
        successBtn.setOnClickListener(v -> {
            check_input();
        });
        return view;
    }
    private void listenNer(){
        date.setOnClickListener(v -> {
            calendarView.setVisibility(View.VISIBLE);
            timePicker.setVisibility(View.GONE);
        });

        timer.setOnClickListener(v -> {
            calendarView.setVisibility(View.GONE);
            timePicker.setVisibility(View.VISIBLE);
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                date.setText( daysOfWeek[dayOfWeek -1]+" , " + dayOfMonth + "/" + (month+1) + "/" + year );
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String amPm;
                if (hourOfDay < 12) {
                    amPm = "AM";
                } else {
                    amPm = "PM";
                    if (hourOfDay > 12) {
                        hourOfDay -= 12;
                    }
                }
                timer.setText(String.format("%02d:%02d %s", hourOfDay, minute, amPm));
            }
        });
        suaBtn.setOnClickListener(v -> {
            date.setFocusableInTouchMode(true);
            date.setFocusable(true);
            timer.setFocusableInTouchMode(true);
            timer.setFocusable(true);
        });
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private void check_input(){
        String text = nameTitle.getText().toString().trim();
        String date_check = date.getText().toString();
        String timer_check = timer.getText().toString();

        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(date_check) || TextUtils.isEmpty(timer_check)) {
            AndroidUtil.showToast(context, "Không được bỏ trống");
        } else {

            Pattern pattern = Pattern.compile("^(1[0-2]|0?[1-9]):([0-5]?[0-9]) (AM|PM)$");
            Matcher matcher = pattern.matcher(timer_check);
            if (matcher.matches()) {
                if (isValidDate(date_check, "EEEE , dd/MM/yyyy")) {
                    HashMap<String, Object> remind = new HashMap<>();
                    remind.put("nameTile", text);
                    remind.put("date", date_check);
                    remind.put("timer", timer_check);

                    FirebaseUtil.addRemind(chatroomID).add(remind).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            dismiss();
                        }
                    }).addOnFailureListener(e -> {
                       AndroidUtil.showToast(context, "error");
                    });
                    AndroidUtil.showToast(context, "Định dạng ngày tháng năm hợp lệ");
                } else {
                    AndroidUtil.showToast(context, "Định dạng ngày tháng năm không hợp lệ");
                }
            }else {
                AndroidUtil.showToast(context, "Định dạng thời gian không hợp lệ");
            }

        }
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

}
