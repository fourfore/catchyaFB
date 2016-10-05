package com.eoss.application.catchya.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.eoss.application.catchya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class DatePickerActivity extends AppCompatActivity {
    Button datePick, nextButton;
    Calendar myCalendar = Calendar.getInstance();
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String myUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        datePick = (Button) findViewById(R.id.addBirthDate);
        nextButton = (Button) findViewById(R.id.next_date);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getCurrentUser().getUid();


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                Log.d("Test-Date","year:"+year+"month:"+(monthOfYear+1)+"day:"+dayOfMonth);
                DatabaseReference userMeRef = mDatabase.child("Users").child(myUid);
                String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                userMeRef.child("BD").child("Date").setValue(date);


                Intent myIntent = new Intent(DatePickerActivity.this, AppActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                DatePickerActivity.this.startActivityForResult(myIntent, 0);
                overridePendingTransition(0,0);
                finish();
            }

        };

        datePick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(DatePickerActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    private void updateLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here


    }

}
