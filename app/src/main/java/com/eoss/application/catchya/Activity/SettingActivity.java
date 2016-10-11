package com.eoss.application.catchya.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
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

import io.apptik.widget.MultiSlider;

public class SettingActivity extends AppCompatActivity {
    private SeekBar seekbarDistance;
    private TextView textView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;

    private final Boolean[] b = new Boolean[1];

    private SwitchCompat menSwitch;
    private SwitchCompat womenSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b[0] = true;
        setContentView(R.layout.activity_setting);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Setting");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        seekbarDistance = (SeekBar) findViewById(R.id.slider_distance2);
        textView = (TextView)findViewById(R.id.number_distance);

        menSwitch = (SwitchCompat)findViewById(R.id.men_switch);
        womenSwitch = (SwitchCompat)findViewById(R.id.women_switch);
        final DatabaseReference settingRef = mDatabase.child("Setting").child(mAuth.getCurrentUser().getUid());
        settingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seekbarDistance.setProgress(Integer.parseInt(dataSnapshot.child("Radius").getValue().toString()));
                textView.setText(dataSnapshot.child("Radius").getValue().toString()+"km.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        settingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                b[0] = true;
                if(dataSnapshot.child("search_gender").getValue().toString().equals("Men")){
                    menSwitch.setChecked(true);
                    womenSwitch.setChecked(false);
                }
                else if(dataSnapshot.child("search_gender").getValue().toString().equals("Women")){
                    menSwitch.setChecked(false);
                    womenSwitch.setChecked(true);
                }
                else if(dataSnapshot.child("search_gender").getValue().toString().equals("MenAndWomen")){
                    menSwitch.setChecked(true);
                    womenSwitch.setChecked(true);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
//                    Ref.child("Radius").setValue((progress+1)+"");
//                }
//                else{
//                    textView.setText(100+"km.");
//                    userRef.child("Radius").setValue(100+"");
//                }
                textView.setText(progress+"km.");
                settingRef.child("Radius").setValue(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        menSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Bfore",b[0]+" Men");
                if (b[0] != true) {
                    Log.d("men", isChecked + "");
                    if (isChecked == true && womenSwitch.isChecked() == false) {
                        //save in db men on
                        settingRef.child("search_gender").setValue("Men");
                    } else if (isChecked == true && womenSwitch.isChecked() == true) {
                        //save in db men and women
                        settingRef.child("search_gender").setValue("MenAndWomen");
                    } else if (isChecked == false && womenSwitch.isChecked() == false) {
                        womenSwitch.setChecked(true);
                        menSwitch.setChecked(false);
                        settingRef.child("search_gender").setValue("Women");
                    } else if (isChecked == false && womenSwitch.isChecked() == true) {
                        //save in db women on
                        settingRef.child("search_gender").setValue("Women");
                    }
                }
                b[0] = false;
            }
        });

        womenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Bfore",b[0]+" Women");
                if (b[0] != true) {
                    if (isChecked == true && menSwitch.isChecked() == false) {
                        settingRef.child("search_gender").setValue("Women");
                    } else if (isChecked == true && menSwitch.isChecked() == true) {
                        //save in db men and women
                        settingRef.child("search_gender").setValue("MenAndWomen");
                    } else if (isChecked == false && menSwitch.isChecked() == false) {
                        womenSwitch.setChecked(false);
                        menSwitch.setChecked(true);
                        settingRef.child("search_gender").setValue("Men");
                    } else if (isChecked == false && menSwitch.isChecked() == true) {
                        //save in db women on
                        settingRef.child("search_gender").setValue("Men");
                    }
                }
                b[0] = false;
            }
        });

        //------------------AGE SEARCH SECTION-------------------------
        final MultiSlider ageSlider = (MultiSlider)findViewById(R.id.range_slider_age);
        ageSlider.setMin(18);
        ageSlider.setMax(50);
        final TextView maxTextView = (TextView)findViewById(R.id.max_age);
        final TextView minTextView = (TextView)findViewById(R.id.min_age);
        DatabaseReference searchAgeRef = settingRef.child("age_search");
        searchAgeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int max = Integer.parseInt(dataSnapshot.child("max").getValue().toString());
                int min = Integer.parseInt(dataSnapshot.child("min").getValue().toString());
                ageSlider.getThumb(0).setValue(Integer.parseInt(dataSnapshot.child("min").getValue().toString()));
                ageSlider.getThumb(1).setValue(Integer.parseInt(dataSnapshot.child("max").getValue().toString()));
                minTextView.setText(dataSnapshot.child("min").getValue().toString());
                maxTextView.setText(dataSnapshot.child("max").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ageSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    settingRef.child("age_search").child("min").setValue(value+"");
                    minTextView.setText(value+"");
                } else if(thumbIndex == 1) {
                    settingRef.child("age_search").child("max").setValue(value+"");
                    maxTextView.setText(value+"");
                }
            }
        });
    }
}
