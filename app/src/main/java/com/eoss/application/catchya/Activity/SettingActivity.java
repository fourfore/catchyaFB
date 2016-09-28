package com.eoss.application.catchya.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.eoss.application.catchya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {
    private SeekBar seekbarDistance;
    private TextView textView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private SwitchCompat menSwitch;
    private SwitchCompat womenSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        seekbarDistance = (SeekBar) findViewById(R.id.slider_distance2);
        textView = (TextView)findViewById(R.id.number_distance);

        menSwitch = (SwitchCompat)findViewById(R.id.men_switch);
        womenSwitch = (SwitchCompat)findViewById(R.id.women_switch);
        final DatabaseReference userRef = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seekbarDistance.setProgress(Integer.parseInt(dataSnapshot.child("Radius").getValue().toString()));
                textView.setText(dataSnapshot.child("Radius").getValue().toString()+"km.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final Boolean[] b = new Boolean[1];
        b[0] = false;
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.child("search_gender").getValue().toString().equals("Men")){
//                    menSwitch.setChecked(true);
//                    womenSwitch.setChecked(false);
//                    b[0] =true;
//                }
//                else if(dataSnapshot.child("search_gender").getValue().toString().equals("Women")){
//                    menSwitch.setChecked(false);
//                    womenSwitch.setChecked(true);
//                    b[0] =true;
//                }
//                else if(dataSnapshot.child("search_gender").getValue().toString().equals("MenAndWomen")){
//                    menSwitch.setChecked(true);
//                    womenSwitch.setChecked(true);
//                    b[0] =true;
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        seekbarDistance.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
//            @Override
//            public void valueChanged(Number value) {
//                textView.setText(value.toString());
//            }
//        });
        seekbarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(progress!=100){
//                    textView.setText((progress+1)+"km.");
//                    userRef.child("Radius").setValue((progress+1)+"");
//                }
//                else{
//                    textView.setText(100+"km.");
//                    userRef.child("Radius").setValue(100+"");
//                }
                textView.setText(progress+"km.");
                userRef.child("Radius").setValue(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        menSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (b[0] != true) {
//                    Log.d("men", isChecked + "");
//                    if (isChecked == true && womenSwitch.isChecked() == false) {
//                        //save in db men on
//                        userRef.child("search_gender").setValue("Men");
//                    } else if (isChecked == true && womenSwitch.isChecked() == true) {
//                        //save in db men and women
//                        userRef.child("search_gender").setValue("MenAndWomen");
//                    } else if (isChecked == false && womenSwitch.isChecked() == false) {
//                        womenSwitch.setChecked(true);
//                        menSwitch.setChecked(false);
//                        userRef.child("search_gender").setValue("Women");
//                    } else if (isChecked == false && womenSwitch.isChecked() == true) {
//                        //save in db women on
//                        userRef.child("search_gender").setValue("Women");
//                    }
//                }
//                b[0] = false;
//            }
//        });
//
//        womenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (b[0] != true) {
//                    if (isChecked == true && menSwitch.isChecked() == false) {
//                        userRef.child("search_gender").setValue("Women");
//                    } else if (isChecked == true && menSwitch.isChecked() == true) {
//                        //save in db men and women
//                        userRef.child("search_gender").setValue("MenAndWomen");
//                    } else if (isChecked == false && menSwitch.isChecked() == false) {
//                        womenSwitch.setChecked(false);
//                        menSwitch.setChecked(true);
//                        userRef.child("search_gender").setValue("Men");
//                    } else if (isChecked == false && menSwitch.isChecked() == true) {
//                        //save in db women on
//                        userRef.child("search_gender").setValue("Men");
//                    }
//                }
//                b[0] = false;
//            }
//        });

    }
}
