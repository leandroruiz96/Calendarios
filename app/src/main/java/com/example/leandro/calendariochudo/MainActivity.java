package com.example.leandro.calendariochudo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.calendarios.CalendarioComun;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Date> marcados = new ArrayList<>();
        marcados.add(Calendar.getInstance().getTime());

        CalendarioComun cal = (CalendarioComun) findViewById(R.id.calendario);
        cal.setMarkedDates(marcados);
        cal.setOnDateChanged(new CalendarioComun.OnDateChanged() {
            @Override
            public void onDateChanged(int day, int month, int year) {
                Toast.makeText(MainActivity.this, ""+day+"/"+(month+1)+"/"+year, Toast.LENGTH_SHORT).show();
            }
        });
        cal.setOnMonthChanged(new CalendarioComun.OnMonthChanged() {
            @Override
            public void onMonthChanged(int month, int year) {
                Toast.makeText(MainActivity.this, "Un nuevo mes: "+(month+1)+"/"+year,Toast.LENGTH_SHORT).show();
            }
        });
    }
}