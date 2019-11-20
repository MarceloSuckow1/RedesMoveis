package com.example.redesmoveis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CalendarView;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    CalendarView simpleCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);



        simpleCalendarView = findViewById(R.id.simpleCalendarView);
        Calendar calendar = Calendar.getInstance();
        simpleCalendarView.setMaxDate(calendar.getTimeInMillis());
        calendar.add(Calendar.YEAR, -1);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        simpleCalendarView.setMinDate(calendar.getTimeInMillis());

        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Intent intent = new Intent(CalendarActivity.this, HistoryActivity.class);
                month++;
                intent.putExtra("date", year+"-"+month+"-"+dayOfMonth);
                startActivity(intent);

            }
        });
    }
}
