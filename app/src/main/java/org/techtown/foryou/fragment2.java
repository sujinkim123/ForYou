package org.techtown.foryou;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.foryou.model.DateModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class fragment2 extends Fragment {

    private ViewGroup viewGroup;

    private EditText edtDiary;// 선태한 날짜의 일정을 쓰거나 기존에 저자된 일기가 있다면 보여주고 수정하는 영역
    private Button btnSave; //선택된 날짜의 파일이름
    private Button btnDelete;
    private Button btnTime;

    private  CalendarView calendarView;

    private Context context;

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mFirebaseDatabase; // 데이터베이스에 접근할 수 있는 진입점 클래스입니다.
    private FirebaseUser user;

    //firebase data object
    private DatabaseReference mDatabaseReference; // 데이터베이스의 주소를 저장합니다.

    private DateModel dateModel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);
        context = getContext();

        edtDiary = (EditText) viewGroup.findViewById(R.id.edtDairy);
        btnSave = viewGroup.findViewById(R.id.btnSave);
        btnDelete = viewGroup.findViewById(R.id.btnDelete);

        btnTime = viewGroup.findViewById(R.id.btnTime);
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),alarm_MainActivity.class);
                startActivity(intent);
            }
        });

        //Init DB
        firebaseAuth = FirebaseAuth.getInstance();
        //UID를 가지오기 위한 파이어베이스 로그인
        firebaseAuth.signInAnonymously();

        user = firebaseAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        initializeDate();
        checkedDay(getToday()); //오늘 날짜 받아옴.


        calendarView =  viewGroup.findViewById(R.id.calendarView);


        // 첫시작 할 때 일정이 있으면 캘린더에 표시해주기
            mDatabaseReference.child("calendar").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //일정데이터가 변경될 때 onDataChange함수 발생
                    if (user != null) {
                        List<EventDay> events = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            int[] date = splitDate(key);
                            Calendar event_calendar = Calendar.getInstance();
                            event_calendar.set(date[0], date[1], date[2]);

                            events.add(new EventDay(event_calendar, R.drawable.ic_tick));
                        }

                        calendarView.setEvents(events);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


        // 선택 날짜가 변경될 때 호출되는 리스너
        //TODO: Listener 따로 빼는게 좋아요.
        calendarView.setOnDayClickListener((eventDay) -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();


            //체크한 날짜 변경
            int checkYear = clickedDayCalendar.get(Calendar.YEAR);
            int checkMonth = clickedDayCalendar.get(Calendar.MONTH);
            int checkDay = clickedDayCalendar.get(Calendar.DATE);

            dateModel.setTimeFromCalendar(clickedDayCalendar);
            String selectedDate = checkYear + "-" + checkMonth + "-" + checkDay;
            //이미 선택한 날짜에 일기가 있는지 없는지 체크
            checkedDay(selectedDate);

            Log.d("[NDY]", checkYear + " " + checkMonth + " " + checkDay);
        });

        //저장/수정 버튼 누르면 실행되는 리스너
        btnSave.setOnClickListener((v) -> {
            //fileName을 넣고 저장시키는 메소드를 호출
            saveDiary(dateModel.getYear() + "-" + dateModel.getMonth() + "-" + dateModel.getDay());
        });

        btnDelete.setOnClickListener((v) -> {
            deleteDiary(dateModel.getYear() + "-" + dateModel.getMonth() + "-" + dateModel.getDay());
        });

        return viewGroup;

    }


    private void initializeDate() {
        Calendar calendar = Calendar.getInstance();
        dateModel = new DateModel(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    //일정 Database 읽기
    @SuppressWarnings("unchecked")
    private void checkedDay(String date) {
        // mDatabaseReference의 경로를 filebase/diary/userUid/date 로 설정
//        mDatabaseReference = mFirebaseDatabase.getReference()
//                .child("calendar")
//                .child(user.getUid())
//                .child(dateModel.getYear() + "-" + dateModel.getMonth() + "-" + dateModel.getDay());

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> dbMap = (Map<String, Object>) dataSnapshot.getValue();
                if(dbMap != null) {
                    Map<String, Object> uidMap = (Map<String, Object>)dbMap.get("calendar");
                    if(uidMap==null){ //uidMap
                        // 데이터가 없으면 일정이 없는 것 -> 일정을 쓰게 하기
                        edtDiary.setText("");
                        //btnSave.setText("새 일정 추가");
                        btnSave.setBackgroundResource(R.drawable.plus1);
                    }else{
                        Map<String, String> dateMap = (Map<String, String>)uidMap.get(user.getUid());
                        if(dateMap == null) {
                            edtDiary.setText("");
                        } else {
                            edtDiary.setText(dateMap.get(date));
                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {  }
        });
    }

    //일정 저장하는 메소드
    @SuppressLint("WrongConstant")
    private void saveDiary(String readDay){
        try{ //일정이 저장될때 try문 발생.

            String content = edtDiary.getText().toString();
            // filebase/calendar/userUid/date save
            mFirebaseDatabase.getReference()
                    .child("calendar")
                    .child(user.getUid())
                    .child(readDay)
                    .setValue(content);
            //일정이 저장되면 토스메세지로 "일정 저장 됨"
            Toast.makeText(context,"일정 저장 완료",Toast.LENGTH_SHORT).show();
        } catch (Exception e){             //예외처리.
            e.printStackTrace();
            Toast.makeText(context, "오류발생",Toast.LENGTH_SHORT).show();
        }
    }

    //일정 삭제하는 메소드
    @SuppressLint("WrongConstant")
    private void deleteDiary(String readDay){
        try{ //일정이 삭제될때 try문 발생.
            mFirebaseDatabase.getReference()
                    .child("calendar")
                    .child(user.getUid())
                    .child(readDay)
                    .removeValue();

            //일정이 저장되면 토스메세지로 "일정 저장 됨"
            Toast.makeText(context,"일정 삭제 완료",Toast.LENGTH_SHORT).show();
        } catch (Exception e){             //예외처리.
            e.printStackTrace();
            Toast.makeText(context, "오류발생",Toast.LENGTH_SHORT).show();
        }
    }

    //문자열을 int로 변환한다.
    private int[] splitDate(String date){
        String[] splitText = date.split("-");
        int[] result_date = {Integer.parseInt(splitText[0]), Integer.parseInt(splitText[1]), Integer.parseInt(splitText[2])};

        return result_date;
    }


    private String getToday() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return String.format("%d-%d-%d", year, month, day);
    }

}